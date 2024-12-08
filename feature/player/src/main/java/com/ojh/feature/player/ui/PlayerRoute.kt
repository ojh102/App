package com.ojh.feature.player.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojh.core.compose.theme.AppTheme
import com.ojh.core.model.NowPlayingInfo
import com.ojh.feature.player.PlayerAction
import com.ojh.feature.player.PlayerSideEffect
import com.ojh.feature.player.PlayerUiState
import com.ojh.feature.player.PlayerViewModel
import com.ojh.feature.player.ui.model.toUiModel

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
    BackHandler(uiState.isExpanded) {
        onAction(PlayerAction.ClickBack)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        when {
            uiState.nowPlayingInfo == null -> {
                EmptyPlayerLayout()
            }

            uiState.isExpanded -> {
                ExpandedPlayerLayout(
                    nowPlayingInfo = uiState.nowPlayingInfo,
                    onClickRepeat = { onAction(PlayerAction.ClickRepeat) },
                    onClickPrev = { onAction(PlayerAction.ClickPrev) },
                    onTogglePlay = { onAction(PlayerAction.TogglePlay) },
                    onClickNext = { onAction(PlayerAction.ClickNext) },
                    onClickShuffle = { onAction(PlayerAction.ClickShuffle) },
                    onChangeVolume = { onAction(PlayerAction.ChangeVolume(it)) },
                    onChangeProgress = { onAction(PlayerAction.ChangeProgress(it)) },
                    modifier = Modifier.clickable { onAction(PlayerAction.ToggleExpand) }
                )
            }

            !uiState.isExpanded -> {
                CollapsedPlayerLayout(
                    nowPlayingInfo = uiState.nowPlayingInfo,
                    onTogglePlay = { onAction(PlayerAction.TogglePlay) },
                    modifier = Modifier.clickable { onAction(PlayerAction.ToggleExpand) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PlayerContentPreview_collpase() {
    AppTheme {
        PlayerContent(
            uiState = PlayerUiState(
                nowPlayingInfo = NowPlayingInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                ).toUiModel(),
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
                nowPlayingInfo = NowPlayingInfo(
                    title = "타이틀",
                    artist = "아티스트",
                    artworkUri = null,
                    isPlaying = true
                ).toUiModel(),
                isExpanded = true
            ),
            onAction = {}
        )
    }
}