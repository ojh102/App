package com.ojh.feature.player

import androidx.compose.runtime.Immutable
import com.ojh.feature.player.ui.model.NowPlayingInfoUiModel

@Immutable
internal data class PlayerUiState(
    val nowPlayingInfo: NowPlayingInfoUiModel? = null,
    val isExpanded: Boolean = false,
)