package com.ojh.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.MusicInfo

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
            .clickable {
                onAction(PlayerAction.ClickPlayer)
            }
    ) {
        if (uiState.isExpanded) {
            ExpandedPlayerLayout(
                uiState = uiState,
                onClickRepeat = { onAction(PlayerAction.ClickRepeat) },
                onClickPrev = { onAction(PlayerAction.ClickPrev) },
                onClickPlayOrPause = { onAction(PlayerAction.ClickPlayOrPause) },
                onClickNext = { onAction(PlayerAction.ClickNext) },
                onClickShuffle = { onAction(PlayerAction.ClickShuffle) },
                onChangeVolume = { onAction(PlayerAction.ChangeVolume(it)) },
                onChangeProgress = { onAction(PlayerAction.ChangeProgress(it)) }
            )
        } else {
            CollapsedPlayerLayout(
                uiState = uiState,
                onClickPlayOrPause = { onAction(PlayerAction.ClickPlayOrPause) }
            )
        }
    }
}

@Preview
@Composable
private fun PlayerContentPreview_collpase() {
    AppTheme {
        PlayerContent(
            uiState = PlayerUiState(
                musicInfo = MusicInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                ),
                isExpanded = false
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun PlayerContentPreview_expand() {
    AppTheme {
        PlayerContent(
            uiState = PlayerUiState(
                musicInfo = MusicInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                ),
                isExpanded = true
            ),
            onAction = {}
        )
    }
}