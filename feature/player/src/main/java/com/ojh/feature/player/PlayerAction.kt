package com.ojh.feature.player

internal sealed interface PlayerAction {
    data object TogglePlay : PlayerAction
    data object ToggleExpand : PlayerAction
    data object ClickBack : PlayerAction
    data object ClickNext : PlayerAction
    data object ClickPrev : PlayerAction
    data object ClickShuffle : PlayerAction
    data object ClickRepeat : PlayerAction
    data class ChangeVolume(val volume: Float) : PlayerAction
    data class ChangeProgress(val position: Float) : PlayerAction
}