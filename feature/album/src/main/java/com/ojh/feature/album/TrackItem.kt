package com.ojh.feature.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.Track

@Composable
internal fun TrackItem(
    track: Track,
    index: Int,
    isSelected: Boolean,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
            .clickable { onClick(track.id) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (index + 1).toString(),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = track.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Preview
@Composable
private fun TrackItemPreview() {
    AppTheme {
        TrackItem(
            track = Track(
                id = 0,
                trackNumber = 0,
                title = "타이틀",
                artist = "아티스트",
                data = ""
            ),
            index = 0,
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun TrackItemPreview_selected() {
    AppTheme {
        TrackItem(
            track = Track(
                id = 0,
                trackNumber = 0,
                title = "타이틀",
                artist = "아티스트",
                data = ""
            ),
            index = 0,
            isSelected = true,
            onClick = {}
        )
    }
}