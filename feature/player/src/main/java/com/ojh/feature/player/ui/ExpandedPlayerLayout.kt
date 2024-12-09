package com.ojh.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.time.Duration
import java.util.Locale

@Composable
internal fun ExpandedPlayerLayout(
    nowPlayingInfo: NowPlayingInfoUiModel,
    onClickBack: () -> Unit,
    onClickRepeat: () -> Unit,
    onClickPrev: () -> Unit,
    onTogglePlay: () -> Unit,
    onClickNext: () -> Unit,
    onClickShuffle: () -> Unit,
    onChangeVolume: (Float) -> Unit,
    onChangeProgress: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClickBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.feature_player_back_cd)
            )
        }

        AlbumInfo(
            title = nowPlayingInfo.title,
            artist = nowPlayingInfo.artist,
            artworkUri = nowPlayingInfo.artworkUri
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerController(
            isRepeated = nowPlayingInfo.isRepeated,
            hasPrev = nowPlayingInfo.hasPrev,
            isPlaying = nowPlayingInfo.isPlaying,
            hasNext = nowPlayingInfo.hasNext,
            isShuffled = nowPlayingInfo.isShuffled,
            onClickRepeat = onClickRepeat,
            onClickPrev = onClickPrev,
            onTogglePlay = onTogglePlay,
            onClickNext = onClickNext,
            onClickShuffle = onClickShuffle
        )

        Spacer(modifier = Modifier.height(16.dp))

        VolumeSlider(volume = nowPlayingInfo.volume, onChangeVolume = onChangeVolume)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = nowPlayingInfo.toDurationText(),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.End)
        )
        PlayerSeekBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            currentPosition = nowPlayingInfo.currentPosition,
            duration = nowPlayingInfo.duration,
            onChangeProgress = onChangeProgress
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun NowPlayingInfoUiModel.toDurationText(): String {
    return "${currentPosition.toDurationText()}/${duration.toDurationText()}"
}

private fun Long.toDurationText(): String {
    val (minutes, seconds) = with(Duration.ofMillis(this)) {
        toMinutesPart() to toSecondsPart()
    }
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
private fun AlbumInfo(
    title: String?,
    artist: String?,
    artworkUri: String?
) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = title ?: stringResource(R.string.feature_player_empty_title),
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = artist ?: stringResource(R.string.feature_player_empty_artist),
        style = MaterialTheme.typography.bodyLarge
    )

    AsyncImage(
        model = artworkUri,
        contentDescription = stringResource(R.string.feature_player_artwork_cd),
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}

@Composable
private fun PlayerController(
    isRepeated: Boolean,
    hasPrev: Boolean,
    isPlaying: Boolean,
    hasNext: Boolean,
    isShuffled: Boolean,
    onClickRepeat: () -> Unit,
    onClickPrev: () -> Unit,
    onTogglePlay: () -> Unit,
    onClickNext: () -> Unit,
    onClickShuffle: () -> Unit,
) {
    Row {
        IconButton(onClick = onClickRepeat) {
            if (isRepeated) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_repeat_one),
                    contentDescription = stringResource(R.string.feature_player_repeat_one_cd)
                )
            } else {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_repeat_off),
                    contentDescription = stringResource(R.string.feature_player_repeat_off_cd)
                )
            }
        }

        IconButton(onClick = onClickPrev, enabled = hasPrev) {
            Icon(
                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_previous),
                contentDescription = stringResource(R.string.feature_player_prev_cd)
            )
        }

        IconButton(onClick = onTogglePlay) {
            if (isPlaying) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_pause),
                    contentDescription = stringResource(R.string.feature_player_stop_cd)
                )
            } else {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_play),
                    contentDescription = stringResource(R.string.feature_player_play_cd)
                )
            }
        }

        IconButton(onClick = onClickNext, enabled = hasNext) {
            Icon(
                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_next),
                contentDescription = stringResource(R.string.feature_player_next_cd)
            )
        }

        IconButton(onClick = onClickShuffle) {
            if (isShuffled) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_shuffle_on),
                    contentDescription = stringResource(R.string.feature_player_shuffle_on_cd)
                )
            } else {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_shuffle_off),
                    contentDescription = stringResource(R.string.feature_player_shuffle_off_cd)
                )
            }
        }
    }
}

@Composable
private fun VolumeSlider(
    volume: Float,
    onChangeVolume: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = stringResource(R.string.feature_player_volume_slider_title))
        Slider(
            value = volume,
            onValueChange = onChangeVolume,
            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.secondary)
        )
    }
}

@Preview
@Composable
private fun ExpandedPlayerLayoutPreview() {
    AppTheme {
        ExpandedPlayerLayout(
            nowPlayingInfo = NowPlayingInfo(
                title = "타이틀",
                artist = "아티스트",
                artworkUri = null,
                isPlaying = true,
                currentPosition = 100000,
                duration = 200000
            ).toUiModel(),
            onClickBack = {},
            onClickRepeat = {},
            onClickNext = {},
            onClickShuffle = {},
            onClickPrev = {},
            onTogglePlay = {},
            onChangeVolume = {},
            onChangeProgress = {}
        )
    }
}