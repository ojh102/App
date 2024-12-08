package com.ojh.feature.library.ui.model

import androidx.compose.runtime.Immutable
import com.ojh.core.model.Album

@Immutable
internal data class AlbumUiModel(
    val id: Long,
    val name: String,
    val artist: String,
    val albumArtUri: String
)

internal fun Album.toUiModel(): AlbumUiModel {
    return AlbumUiModel(
        id = id,
        name = name,
        artist = artist,
        albumArtUri = albumArtUri
    )
}