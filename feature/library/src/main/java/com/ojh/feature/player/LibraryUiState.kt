package com.ojh.feature.player

import androidx.compose.runtime.Immutable
import com.ojh.core.model.Album

@Immutable
internal data class LibraryUiState(
    val musicPermissionGranted: Boolean = false,
    val albums: List<Album> = emptyList(),
    val showSettingsDialog: Boolean = false
)