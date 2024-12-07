package com.ojh.feature.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojh.core.model.Album
import com.ojh.core.model.Track

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

    AlbumContent(
        uiState = uiState,
        onAction = viewmodel::onAction,
        modifier = modifier
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
            onClickPlay = { onAction(AlbumAction.ClickPlay) },
            onClickRandomPlay = { onAction(AlbumAction.ClickRandomPlay) }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(items = uiState.tracks, key = { _, track -> track.id }) { index, track ->
                TrackItem(
                    track = track,
                    index = index,
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
                Track(
                    id = it.toLong(),
                    trackNumber = it,
                    title = "타이틀$it",
                    artist = "아티스트$it",
                    data = ""
                )
            },
            album = Album(id = 0, name = "앨범0", artist = "아티스트0", albumArtUri = "")
        ),
        onAction = {}
    )
}

