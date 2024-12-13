package com.ojh.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ojh.core.compose.theme.AppTheme
import com.ojh.feature.library.R
import com.ojh.feature.library.ui.model.AlbumUiModel

@Composable
internal fun AlbumItem(
    album: AlbumUiModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onClick(album.id) }
    ) {
        AsyncImage(
            model = album.albumArtUri,
            contentDescription = stringResource(R.string.feature_library_artwork_cd),
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Text(
            text = album.name,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        )
        Text(
            text = album.artist,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Preview
@Composable
private fun AlbumItemPreview() {
    AppTheme {
        AlbumItem(
            album = AlbumUiModel(id = 0, name = "앨범0", artist = "아티스트0", albumArtUri = ""),
            onClick = {}
        )
    }
}
