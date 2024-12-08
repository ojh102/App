package com.ojh.feature.library

internal sealed interface LibrarySideEffect {
    data class LaunchRequestPermission(val permission: String) : LibrarySideEffect
    data object NavigateToSettings : LibrarySideEffect
    data class NavigateToAlbum(val albumId: Long) : LibrarySideEffect
}