package com.ojh.feature.player

internal sealed interface PlayerAction {
    data object ClickPlayOrPause : PlayerAction
    data object ClickPlayer : PlayerAction
    data object ClickNext : PlayerAction
    data object ClickPrev : PlayerAction
    data object ClickShuffle : PlayerAction
    data object ClickRepeat : PlayerAction
}