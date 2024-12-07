package com.ojh.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.MusicInfo
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheet(scaffoldState: BottomSheetScaffoldState) {
    PlayerScreen(scaffoldState = scaffoldState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerScreen(
    scaffoldState: BottomSheetScaffoldState,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect {
            when (it) {
                PlayerSideEffect.Expand -> {
                    scaffoldState.bottomSheetState.expand()
                }

                PlayerSideEffect.Collapse -> {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }
    }

    PlayerContent(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
private fun PlayerContent(
    uiState: PlayerUiState,
    onAction: (PlayerAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                onAction(PlayerAction.ClickPlayer)
            }
    ) {
        if (uiState.isExpanded) {
            ExpandedPlayerContent(
                uiState = uiState,
                onClickRepeat = { onAction(PlayerAction.ClickRepeat) },
                onClickPrev = { onAction(PlayerAction.ClickPrev) },
                onClickPlayOrPause = { onAction(PlayerAction.ClickPlayOrPause) },
                onClickNext = { onAction(PlayerAction.ClickNext) },
                onClickShuffle = { onAction(PlayerAction.ClickShuffle) },
                onChangeVolume = { onAction(PlayerAction.ChangeVolume(it)) }
            )
        } else {
            CollapsedPlayerContent(
                uiState = uiState,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun ExpandedPlayerContent(
    uiState: PlayerUiState,
    onClickRepeat: () -> Unit,
    onClickPrev: () -> Unit,
    onClickPlayOrPause: () -> Unit,
    onClickNext: () -> Unit,
    onClickShuffle: () -> Unit,
    onChangeVolume: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = uiState.musicInfo.title ?: "재생중인 음악이 없습니다.",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = uiState.musicInfo.artist ?: "앨범에서 음악을 선택하세요.",
            style = MaterialTheme.typography.bodyLarge
        )

        AsyncImage(
            model = uiState.musicInfo.artworkUri,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
        )


        Row {
            IconButton(onClick = onClickRepeat) {
                if (uiState.musicInfo.isRepeated) {
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

            IconButton(onClick = onClickPrev, enabled = uiState.musicInfo.hasPrev) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_previous),
                    contentDescription = "이전"
                )
            }

            IconButton(onClick = onClickPlayOrPause) {
                if (uiState.musicInfo.isPlaying) {
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

            IconButton(onClick = onClickNext, enabled = uiState.musicInfo.hasNext) {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_next),
                    contentDescription = "다음"
                )
            }

            IconButton(onClick = onClickShuffle) {
                if (uiState.musicInfo.isShuffled) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Timber.e("재환아 uiState.musicInfo.volume = ${uiState.musicInfo.volume}")
        Slider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = uiState.musicInfo.volume,
            onValueChange = onChangeVolume,
            steps = 10
        )
        Text(text = "현재 볼륨 : ${uiState.musicInfo.volume}")
    }
}

@Composable
private fun CollapsedPlayerContent(
    uiState: PlayerUiState,
    onAction: (PlayerAction) -> Unit
) {
    Row(
        modifier = Modifier
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { onAction(PlayerAction.ClickPlayOrPause) }) {
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

@Preview(showBackground = true)
@Composable
private fun PlayerContentPreview_empty() {
    AppTheme {
        CollapsedPlayerContent(
            uiState = PlayerUiState(musicInfo = MusicInfo()),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerContentPreview() {
    AppTheme {
        CollapsedPlayerContent(
            uiState = PlayerUiState(
                musicInfo = MusicInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandedPlayerContentPreview() {
    AppTheme {
        ExpandedPlayerContent(
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
            onChangeVolume = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandedPlayerContentPreview_empty() {
    AppTheme {
        ExpandedPlayerContent(
            uiState = PlayerUiState(
                musicInfo = MusicInfo()
            ),
            onClickRepeat = {},
            onClickNext = {},
            onClickShuffle = {},
            onClickPrev = {},
            onClickPlayOrPause = {},
            onChangeVolume = {}
        )
    }
}