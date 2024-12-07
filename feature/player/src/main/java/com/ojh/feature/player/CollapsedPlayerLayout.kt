package com.ojh.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.MusicInfo

@Composable
internal fun CollapsedPlayerLayout(
    uiState: PlayerUiState,
    onClickPlayOrPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onClickPlayOrPause) {
            Icon(
                painter = if (uiState.musicInfo.isPlaying) {
                    painterResource(androidx.media3.session.R.drawable.media3_icon_pause)
                } else {
                    painterResource(androidx.media3.session.R.drawable.media3_icon_play)
                },
                contentDescription = null
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiState.musicInfo.title ?: "재생중인 음악이 없습니다.",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.musicInfo.artist ?: "앨범에서 음악을 선택하세요.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        AsyncImage(
            modifier = Modifier.size(80.dp),
            model = uiState.musicInfo.artworkUri,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun CollapsedPlayerLayout_empty() {
    AppTheme {
        CollapsedPlayerLayout(
            uiState = PlayerUiState(musicInfo = MusicInfo()),
            onClickPlayOrPause = {}
        )
    }
}

@Preview
@Composable
private fun CollapsedPlayerLayoutPreview() {
    AppTheme {
        CollapsedPlayerLayout(
            uiState = PlayerUiState(
                musicInfo = MusicInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                )
            ),
            onClickPlayOrPause = {}
        )
    }
}