package com.ojh.feature.album.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun AlbumPlayLayout(
    onClickSequencePlay: () -> Unit,
    onClickRandomPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        IconButton(
            onClick = onClickSequencePlay,
            modifier = Modifier
                .weight(1f)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = onClickRandomPlay,
            modifier = Modifier
                .weight(1f)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun AlbumPlayLayoutPreview() {
    AlbumPlayLayout(onClickSequencePlay = {}, onClickRandomPlay = {})
}