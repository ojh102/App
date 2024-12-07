package com.ojh.feature.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.ojh.core.data.MusicRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService : MediaSessionService() {

    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }

    @Inject
    lateinit var musicRepository: MusicRepository

    private var mediaSession: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createMediaSession()

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
                    val albumId = intent.getLongExtra(ALBUM_ID, -1L)
                    initPlayer(albumId)
                }
            }
        }

        return START_STICKY
    }

    private fun createMediaSession(): MediaSession {
        val player = ExoPlayer.Builder(this)
            .build()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                initNotification()
            }

            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                initNotification()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                initNotification()
            }
        })
        return MediaSession.Builder(this, player)
            .setId("MusicPlayer" + System.currentTimeMillis())
            .setCallback(object : MediaSession.Callback {
                @OptIn(UnstableApi::class)
                override fun onSetMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>,
                    startIndex: Int,
                    startPositionMs: Long
                ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                    return super.onSetMediaItems(
                        mediaSession,
                        controller,
                        mediaItems,
                        startIndex,
                        startPositionMs
                    )
                }
            })
            .build()
            .also { mediaSession = it }
    }


    @OptIn(UnstableApi::class)
    private fun createNotification(mediaSession: MediaSession): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession))
            .build()
    }

    @OptIn(UnstableApi::class)
    private fun initNotification() {
        val notification = createNotification(mediaSession!!)
        notificationManager.notify(NOTIFICATION_ID, notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }
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


    private fun initPlayer(albumId: Long) {
        playTracks(albumId)
    }

    private fun playTracks(albumId: Long) {
        GlobalScope.launch {
            val tracks = musicRepository.getTracks(albumId)
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
            withContext(Dispatchers.Main) {
                player.setMediaItems(mediaItems)
                player.prepare()
                player.play()
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "channel_id::music"
        private const val CHANNEL_NAME = "Music"

        private const val ALBUM_ID = "album_id"
        private const val START_TRACK_ID = "start_track_id"

        private const val NOTIFICATION_ID = 1234

        private const val START_MUSIC_ACTION = "com.ojh.feature.player.action.START_MUSIC"

        fun startIntent(context: Context, albumId: Long, startTrackId: Long): Intent {
            return Intent(context, MusicPlayerService::class.java)
                .setAction(START_MUSIC_ACTION)
                .putExtra(ALBUM_ID, albumId)
                .putExtra(START_TRACK_ID, startTrackId)
        }

        fun startPlayer(context: Context, albumId: Long): Intent {
            return Intent(context, MusicPlayerService::class.java)
                .setAction(START_MUSIC_ACTION)
                .putExtra(ALBUM_ID, albumId)
        }
    }
}