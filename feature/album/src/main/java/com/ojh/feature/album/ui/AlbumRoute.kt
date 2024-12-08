package com.ojh.feature.album.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojh.core.media.MusicPlayerService
import com.ojh.feature.album.AlbumAction
import com.ojh.feature.album.AlbumSideEffect
import com.ojh.feature.album.AlbumUiState
import com.ojh.feature.album.AlbumViewModel
import com.ojh.feature.album.ui.model.AlbumUiModel
import com.ojh.feature.album.ui.model.NowPlayingInfoUiModel
import com.ojh.feature.album.ui.model.TrackUiModel

@Composable
fun AlbumRoute(modifier: Modifier = Modifier) {
    AlbumScreen(modifier)
}

@Composable
private fun AlbumScreen(
    modifier: Modifier = Modifier,
    viewmodel: AlbumViewModel = hiltViewModel()
) {
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewmodel.sideEffect.collect {
            when (it) {
                is AlbumSideEffect.StartMusicPlayService -> {
                    startMusicPlayService(
                        context = context,
                        albumId = it.albumId,
                        isShuffled = it.isShuffled,
                        selectedTrackId = it.selectedTrackId
                    )
                }
            }
        }
    }

    AlbumContent(
        uiState = uiState,
        onAction = viewmodel::onAction,
        modifier = modifier
    )
}

private fun startMusicPlayService(
    context: Context,
    albumId: Long,
    isShuffled: Boolean,
    selectedTrackId: Long?,
) {
    context.startService(
        MusicPlayerService.startIntent(
            context = context,
            albumId = albumId,
            isShuffled = isShuffled,
            selectedTrackId = selectedTrackId
        )
    )
}

@Composable
private fun AlbumContent(
    uiState: AlbumUiState,
    onAction: (AlbumAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        AlbumInfoLayout(uiState.album)
        AlbumPlayLayout(
            onClickSequencePlay = { onAction(AlbumAction.ClickSequencePlay) },
            onClickRandomPlay = { onAction(AlbumAction.ClickRandomPlay) }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(items = uiState.tracks, key = { it.id }) { track ->
                TrackItem(
                    track = track,
                    isSelected = track.id == uiState.nowPlayingInfo?.id,
                    onClick = { trackId ->
                        onAction(AlbumAction.ClickTrack(trackId = trackId))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun AlbumContentPreview() {
    AlbumContent(
        uiState = AlbumUiState(
            tracks = (0..10).map {
                TrackUiModel(
                    id = it.toLong(),
                    displayedTrackNumber = it + 1,
                    title = "타이틀$it",
                )
            },
            album = AlbumUiModel(id = 0, name = "앨범0", artist = "아티스트0", albumArtUri = ""),
            nowPlayingInfo = NowPlayingInfoUiModel(id = 1)
        ),
        onAction = {}
    )
}

