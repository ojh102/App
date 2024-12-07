package com.ojh.feature.album

import androidx.compose.runtime.Immutable
import com.ojh.core.model.Album
import com.ojh.core.model.MusicInfo
import com.ojh.core.model.Track

@Immutable
internal data class AlbumUiState(
    val tracks: List<Track> = emptyList(),
    val album: Album? = null,
    val nowPlayingMusicInfo: MusicInfo = MusicInfo()
)