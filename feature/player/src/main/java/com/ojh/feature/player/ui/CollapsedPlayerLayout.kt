package com.ojh.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.NowPlayingInfo
import com.ojh.feature.player.R
import com.ojh.feature.player.ui.model.NowPlayingInfoUiModel
import com.ojh.feature.player.ui.model.toUiModel

@Composable
internal fun CollapsedPlayerLayout(
    nowPlayingInfo: NowPlayingInfoUiModel,
    onTogglePlay: () -> Unit,
    onChangeProgress: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .height(CollapsedPlayerLayoutHeight),
    ) {
        PlayerSeekBar(
            currentPosition = nowPlayingInfo.currentPosition,
            duration = nowPlayingInfo.duration,
            showDurationText = false,
            onChangeProgress = onChangeProgress
        )
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onTogglePlay) {
                Icon(
                    painter = if (nowPlayingInfo.isPlaying) {
                        painterResource(androidx.media3.session.R.drawable.media3_icon_pause)
                    } else {
                        painterResource(androidx.media3.session.R.drawable.media3_icon_play)
                    },
                    contentDescription = stringResource(R.string.feature_player_play_toggle_button_cd)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nowPlayingInfo.title.orEmpty(),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nowPlayingInfo.artist.orEmpty(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AsyncImage(
                modifier = Modifier.fillMaxHeight(),
                model = nowPlayingInfo.artworkUri,
                contentDescription = stringResource(R.string.feature_player_artwork_cd)
            )
        }
    }
}

val CollapsedPlayerLayoutHeight = 84.dp

@Preview
@Composable
private fun CollapsedPlayerLayoutPreview() {
    AppTheme {
        CollapsedPlayerLayout(
            nowPlayingInfo = NowPlayingInfo(
                title = "타이틀",
                artist = "아티스트",
                artworkUri = null,
                isPlaying = true
            ).toUiModel(),
            onTogglePlay = {},
            onChangeProgress = {}
        )
    }
}