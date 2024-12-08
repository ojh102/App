package com.ojh.feature.player.ui.model

import androidx.compose.runtime.Immutable
import com.ojh.core.model.NowPlayingInfo

@Immutable
internal data class NowPlayingInfoUiModel(
    val id: Long,
    val title: String?,
    val artist: String?,
    val artworkUri: String?,
    val currentPosition: Long,
    val duration: Long,
    val isPlaying: Boolean,
    val hasPrev: Boolean,
    val hasNext: Boolean,
    val isRepeated: Boolean,
    val isShuffled: Boolean,
    val volume: Float
)

internal fun NowPlayingInfo.toUiModel(): NowPlayingInfoUiModel {
    return NowPlayingInfoUiModel(
        id = id,
        title = title,
        artist = artist,
        artworkUri = artworkUri,
        currentPosition = currentPosition,
        duration = duration,
        isPlaying = isPlaying,
        hasPrev = hasPrev,
        hasNext = hasNext,
        isRepeated = isRepeated,
        isShuffled = isShuffled,
        volume = volume
    )
}