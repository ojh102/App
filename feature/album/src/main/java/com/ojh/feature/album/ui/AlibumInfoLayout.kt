package com.ojh.feature.album.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ojh.core.compose.theme.AppTheme
import com.ojh.feature.album.ui.model.AlbumUiModel

@Composable
internal fun AlbumInfoLayout(album: AlbumUiModel?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .height(96.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (album == null) {
            CircularProgressIndicator()
        } else {
            AsyncImage(
                model = album.albumArtUri,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = album.name, style = MaterialTheme.typography.titleLarge)
                Text(text = album.artist, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview
@Composable
private fun AlbumInfoLayoutPreview() {
    AppTheme {
        AlbumInfoLayout(
            album = AlbumUiModel(id = 0, name = "앨범0", artist = "아티스트0", albumArtUri = "")
        )
    }
}

@Preview
@Composable
private fun AlbumInfoLayoutPreview_loading() {
    AppTheme {
        AlbumInfoLayout(
            album = null
        )
    }
}