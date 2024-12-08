package com.ojh.feature.album

import androidx.compose.runtime.Immutable
import com.ojh.feature.album.ui.model.AlbumUiModel
import com.ojh.feature.album.ui.model.NowPlayingInfoUiModel
import com.ojh.feature.album.ui.model.TrackUiModel

@Immutable
internal data class AlbumUiState(
    val tracks: List<TrackUiModel> = emptyList(),
    val album: AlbumUiModel? = null,
    val nowPlayingInfo: NowPlayingInfoUiModel? = null
)