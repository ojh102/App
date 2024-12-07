package com.ojh.feature.player

internal sealed interface PlayerSideEffect {
    data object Expand : PlayerSideEffect
    data object Collapse : PlayerSideEffect
}