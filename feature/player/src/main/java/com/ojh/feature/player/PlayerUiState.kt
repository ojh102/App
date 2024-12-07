package com.ojh.feature.player

import androidx.compose.runtime.Immutable
import com.ojh.core.model.MusicInfo

@Immutable
internal data class PlayerUiState(
    val musicInfo: MusicInfo = MusicInfo(),
    val isExpanded: Boolean = false,
)