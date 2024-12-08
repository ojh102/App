package com.ojh.feature.album.ui.model

import androidx.compose.runtime.Immutable
import com.ojh.core.model.NowPlayingInfo

@Immutable
internal data class NowPlayingInfoUiModel(
    val id: Long
)

internal fun NowPlayingInfo.toUiModel(): NowPlayingInfoUiModel {
    return NowPlayingInfoUiModel(id = id)
}