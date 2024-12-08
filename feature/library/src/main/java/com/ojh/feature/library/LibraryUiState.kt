package com.ojh.feature.library

import androidx.compose.runtime.Immutable
import com.ojh.feature.library.ui.model.AlbumUiModel

@Immutable
internal data class LibraryUiState(
    val musicPermissionGranted: Boolean = true,
    val albums: List<AlbumUiModel> = emptyList(),
    val showSettingsDialog: Boolean = false
)