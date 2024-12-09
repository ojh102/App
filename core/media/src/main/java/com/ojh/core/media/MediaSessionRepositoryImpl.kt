package com.ojh.core.media

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.ojh.core.coroutine.AppDispatchers
import com.ojh.core.coroutine.AppScope
import com.ojh.core.coroutine.Dispatcher
import com.ojh.core.model.NowPlayingInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaSessionRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    @AppScope private val appScope: CoroutineScope,
    @Dispatcher(AppDispatchers.MAIN) mainDispatcher: CoroutineDispatcher
) : MediaSessionRepository {

    private val _nowPlayingNowPlayingInfo =
        MutableStateFlow<NowPlayingInfoState>(NowPlayingInfoState.DisConnected)
    private var mediaController: MediaController? = null

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private var lastUpdateTime = 0L

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        super.onMediaMetadataChanged(mediaMetadata)
                        updateNowPlayingInfo()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        updateNowPlayingInfo()
                    }

                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        super.onPlayWhenReadyChanged(playWhenReady, reason)
                        updateNowPlayingInfo()
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        updateNowPlayingInfo()
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {
                        super.onRepeatModeChanged(repeatMode)
                        updateNowPlayingInfo()
                    }

                    override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                        super.onDeviceVolumeChanged(volume, muted)
                        updateNowPlayingInfo()
                    }
                })
            }, MoreExecutors.directExecutor()
        )

        appScope.launch {
            while (true) {
                delay(500)
                withContext(mainDispatcher) {
                    if (mediaController?.isPlaying == true) {
                        val elapsedTime = System.currentTimeMillis() - lastUpdateTime
                        if (elapsedTime >= 500) {
                            updateNowPlayingInfo()
                        }
                    }
                }
            }
        }
    }

    override fun observeNowPlayingInfoState(): Flow<NowPlayingInfoState> {
        return _nowPlayingNowPlayingInfo
    }

    override fun togglePlay() {
        val controller = mediaController ?: return
        if (!controller.isPlaying && !controller.currentTracks.isEmpty) {
            controller.play()
        } else {
            controller.pause()
        }
    }

    override fun next() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        }
    }

    override fun prev() {
        val controller = mediaController ?: return
        if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        }
    }

    override fun shuffle() {
        val controller = mediaController ?: return
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
    }

    override fun repeat() {
        val controller = mediaController ?: return
        if (controller.repeatMode == Player.REPEAT_MODE_OFF) {
            controller.repeatMode = Player.REPEAT_MODE_ONE
        } else {
            controller.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    override fun changeVolume(volume: Float) {
        mediaController?.setDeviceVolume((volume * maxVolume).toInt(), C.VOLUME_FLAG_SHOW_UI)
    }

    override fun changeProgress(progress: Float) {
        val duration = mediaController?.duration ?: return
        mediaController?.seekTo((duration * progress).toLong())
    }

    private fun updateNowPlayingInfo() {
        lastUpdateTime = System.currentTimeMillis()

        val controller = mediaController ?: return
        val mediaMetadata = controller.mediaMetadata

        val nowPlayingInfo = NowPlayingInfo(
            id = controller.currentMediaItem?.mediaId?.toLong() ?: 0L,
            title = mediaMetadata.title?.toString(),
            artist = mediaMetadata.artist?.toString(),
            artworkUri = mediaMetadata.artworkUri?.toString(),
            currentPosition = controller.currentPosition,
            duration = controller.duration,
            isPlaying = controller.isPlaying,
            hasPrev = controller.hasPreviousMediaItem(),
            hasNext = controller.hasNextMediaItem(),
            isRepeated = controller.repeatMode == Player.REPEAT_MODE_ONE,
            isShuffled = controller.shuffleModeEnabled,
            volume = controller.deviceVolume / maxVolume.toFloat()
        )

        _nowPlayingNowPlayingInfo.update { NowPlayingInfoState.Connected(nowPlayingInfo) }
    }
}