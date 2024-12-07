package com.ojh.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.MusicInfo

@Composable
internal fun ExpandedPlayerLayout(
    uiState: PlayerUiState,
    onClickRepeat: () -> Unit,
    onClickPrev: () -> Unit,
    onClickPlayOrPause: () -> Unit,
    onClickNext: () -> Unit,
    onClickShuffle: () -> Unit,
    onChangeVolume: (Float) -> Unit,
    onChangeProgress: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .navigationBarsPadding()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumInfo(
            title = uiState.musicInfo.title,
            artist = uiState.musicInfo.artist,
            artworkUri = uiState.musicInfo.artworkUri
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerController(
            isRepeated = uiState.musicInfo.isRepeated,
            hasPrev = uiState.musicInfo.hasPrev,
            isPlaying = uiState.musicInfo.isPlaying,
            hasNext = uiState.musicInfo.hasNext,
            isShuffled = uiState.musicInfo.isShuffled,
            onClickRepeat = onClickRepeat,
            onClickPrev = onClickPrev,
            onClickPlayOrPause = onClickPlayOrPause,
            onClickNext = onClickNext,
            onClickShuffle = onClickShuffle
        )

        Spacer(modifier = Modifier.height(16.dp))

        VolumeSlider(volume = uiState.musicInfo.volume, onChangeVolume = onChangeVolume)

        Spacer(modifier = Modifier.height(4.dp))

        PlayerSeekBar(
            currentPosition = uiState.musicInfo.currentPosition,
            duration = uiState.musicInfo.duration,
            onChangeProgress = onChangeProgress
        )
    }
}

@Composable
private fun AlbumInfo(
    title: String?,
    artist: String?,
    artworkUri: String?
) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = title ?: "재생중인 음악이 없습니다.",
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = artist ?: "앨범에서 음악을 선택하세요.",
        style = MaterialTheme.typography.bodyLarge
    )

    AsyncImage(
        model = artworkUri,
        contentDescription = null,
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
    onClickPlayOrPause: () -> Unit,
    onClickNext: () -> Unit,
    onClickShuffle: () -> Unit,
) {
    Row {
        IconButton(onClick = onClickRepeat) {
            if (isRepeated) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_repeat_one),
                    contentDescription = "한곡반복"
                )
            } else {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_repeat_off),
                    contentDescription = "반복"
                )
            }
        }

        IconButton(onClick = onClickPrev, enabled = hasPrev) {
            Icon(
                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_previous),
                contentDescription = "이전"
            )
        }

        IconButton(onClick = onClickPlayOrPause) {
            if (isPlaying) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_pause),
                    contentDescription = "알시정지"
                )
            } else {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_play),
                    contentDescription = "재생"
                )
            }
        }

        IconButton(onClick = onClickNext, enabled = hasNext) {
            Icon(
                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_next),
                contentDescription = "다음"
            )
        }

        IconButton(onClick = onClickShuffle) {
            if (isShuffled) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_shuffle_on),
                    contentDescription = "셔플"
                )
            } else {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_shuffle_off),
                    contentDescription = "순서대로듣기"
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
        Text(text = "현재 볼륨 : ${volume}")
        Slider(
            value = volume,
            onValueChange = onChangeVolume,
            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.secondary),
            steps = 10
        )
    }
}

@Composable
private fun PlayerSeekBar(
    currentPosition: Long,
    duration: Long,
    onChangeProgress: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "음악 진행도")

        var sliderPosition by remember { mutableFloatStateOf(currentPosition / duration.toFloat()) }
        LaunchedEffect(currentPosition, duration) {
            sliderPosition = currentPosition / duration.toFloat()
        }
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                onChangeProgress(sliderPosition)
            },
            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Preview
@Composable
private fun ExpandedPlayerLayoutPreview() {
    AppTheme {
        ExpandedPlayerLayout(
            uiState = PlayerUiState(
                musicInfo = MusicInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                )
            ),
            onClickRepeat = {},
            onClickNext = {},
            onClickShuffle = {},
            onClickPrev = {},
            onClickPlayOrPause = {},
            onChangeVolume = {},
            onChangeProgress = {}
        )
    }
}

@Preview
@Composable
private fun ExpandedPlayerLayoutPreview_empty() {
    AppTheme {
        ExpandedPlayerLayout(
            uiState = PlayerUiState(
                musicInfo = MusicInfo()
            ),
            onClickRepeat = {},
            onClickNext = {},
            onClickShuffle = {},
            onClickPrev = {},
            onClickPlayOrPause = {},
            onChangeVolume = {},
            onChangeProgress = {}
        )
    }
}