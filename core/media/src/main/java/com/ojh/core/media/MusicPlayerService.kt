package com.ojh.core.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import com.ojh.core.coroutine.AppDispatchers
import com.ojh.core.coroutine.AppScope
import com.ojh.core.coroutine.Dispatcher
import com.ojh.core.data.MusicRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService : MediaSessionService() {
    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    @AppScope
    lateinit var appScope: CoroutineScope

    @Inject
    @Dispatcher(AppDispatchers.MAIN)
    lateinit var mainDispatcher: CoroutineDispatcher

    private var mediaSession: MediaSession? = null

    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }

    private var albumId = EMPTY_ID

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initMediaSession()
        setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                mediaButtonPreferences: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                return MediaNotification(NOTIFICATION_ID, createNotification(mediaSession))
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean {
                return false
            }
        })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (player.playWhenReady) {
            player.pause()
        }
        stopSelf()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {
            when (intent.action) {
                START_MUSIC_ACTION -> {
                    val albumId = intent.getLongExtra(ALBUM_ID, EMPTY_ID).also { this.albumId = it }
                    val isShuffled = intent.getBooleanExtra(IS_SHUFFLED, false)
                    val selectedTrackId = intent.getLongExtra(SELECTED_TRACK_ID, EMPTY_ID)
                    initPlayer(albumId, isShuffled, selectedTrackId)
                }
            }
        }

        return START_STICKY
    }

    @OptIn(UnstableApi::class)
    private fun initMediaSession(): MediaSession {
        val player = ExoPlayer.Builder(this)
            .setWakeMode(PowerManager.PARTIAL_WAKE_LOCK)
            .setHandleAudioBecomingNoisy(true)
            .setDeviceVolumeControlEnabled(true)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
        return MediaSession.Builder(this, player)
            .setId("MusicPlayer")
            .build()
            .also { mediaSession = it }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @OptIn(UnstableApi::class)
    private fun createNotification(mediaSession: MediaSession): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession))
            .setSilent(true)
            .apply {
                if (albumId != -1L) {
                    setContentIntent(
                        PendingIntent.getActivity(
                            this@MusicPlayerService,
                            System.currentTimeMillis().toInt(),
                            Intent.parseUri(
                                ALBUM_DEEPLINK_PREFIX + albumId,
                                Intent.URI_INTENT_SCHEME
                            ),
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    )
                }
            }
            .build()
    }

    private fun initPlayer(albumId: Long, isShuffled: Boolean, selectedTrackId: Long) {
        appScope.launch {
            val tracks = musicRepository.getTracks(albumId).run {
                if (isShuffled) {
                    shuffled()
                } else {
                    this
                }
            }

            val selectedTrackIndex =
                selectedTrackId.takeIf { it != EMPTY_ID }
                    ?.let { tracks.indexOfFirst { track -> track.id == it } } ?: 0

            val albumArtUri = musicRepository.getAlbumById(albumId)?.albumArtUri.orEmpty()
            val player = mediaSession?.player ?: return@launch
            val mediaItems = tracks.map { track ->
                MediaItem.Builder()
                    .setMediaId(track.id.toString())
                    .setUri(track.data)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(track.title)
                            .setArtist(track.artist)
                            .setArtworkUri(Uri.parse(albumArtUri))
                            .build()
                    )
                    .build()
            }
            withContext(mainDispatcher) {
                player.setMediaItems(mediaItems)
                player.seekTo(selectedTrackIndex, 0)
                player.prepare()
                player.play()
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "channel_id::music"
        private const val CHANNEL_NAME = "Music"

        private const val ALBUM_ID = "album_id"
        private const val IS_SHUFFLED = "is_shuffled"
        private const val SELECTED_TRACK_ID = "selected_track_id"

        private const val NOTIFICATION_ID = 1234

        private const val START_MUSIC_ACTION = "com.ojh.feature.player.action.START_MUSIC"
        private const val ALBUM_DEEPLINK_PREFIX = "ojh://album/"
        private const val EMPTY_ID = -1L

        fun startIntent(
            context: Context,
            albumId: Long,
            isShuffled: Boolean,
            selectedTrackId: Long?,
        ): Intent {
            return Intent(context, MusicPlayerService::class.java)
                .setAction(START_MUSIC_ACTION)
                .putExtra(ALBUM_ID, albumId)
                .putExtra(IS_SHUFFLED, isShuffled)
                .putExtra(SELECTED_TRACK_ID, selectedTrackId)
        }
    }
}