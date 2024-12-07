package com.ojh.core.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.ojh.core.model.MusicInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class MediaSessionRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : MediaSessionRepository {

    private val _nowPlayingMusicInfo = MutableStateFlow(MusicInfo())
    private var mediaController: MediaController? = null

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
                        updateMusicInfo()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        updateMusicInfo()
                    }

                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        super.onPlayWhenReadyChanged(playWhenReady, reason)
                        updateMusicInfo()
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        updateMusicInfo()
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {
                        super.onRepeatModeChanged(repeatMode)
                        updateMusicInfo()
                    }

                    override fun onVolumeChanged(volume: Float) {
                        super.onVolumeChanged(volume)
                        updateMusicInfo()
                    }
                })
            }, MoreExecutors.directExecutor()
        )
    }

    override fun observeNowPlayingMusicInfo(): Flow<MusicInfo> {
        return _nowPlayingMusicInfo
    }

    override fun playOrPause() {
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
        mediaController?.volume = volume
    }

    private fun updateMusicInfo() {
        val controller = mediaController ?: return
        val mediaMetadata = controller.mediaMetadata

        val musicInfo = MusicInfo(
            id = controller.currentMediaItem?.mediaId?.toLong() ?: 0L,
            title = mediaMetadata.title.toString(),
            artist = mediaMetadata.artist.toString(),
            artworkUri = mediaMetadata.artworkUri.toString(),
            currentPosition = controller.currentPosition,
            contentPosition = controller.contentPosition,
            isPlaying = controller.isPlaying,
            hasPrev = controller.hasPreviousMediaItem(),
            hasNext = controller.hasNextMediaItem(),
            isRepeated = controller.repeatMode == Player.REPEAT_MODE_ONE,
            isShuffled = controller.shuffleModeEnabled,
            volume = controller.volume
        )

        _nowPlayingMusicInfo.update { musicInfo }
    }
}