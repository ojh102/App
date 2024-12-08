package com.ojh.feature.library

import androidx.compose.runtime.Immutable
import com.ojh.core.model.Album

@Immutable
internal data class LibraryUiState(
    val musicPermissionGranted: Boolean = true,
    val albums: List<Album> = emptyList(),
    val showSettingsDialog: Boolean = false
)