package com.ojh.feature.album

internal sealed interface AlbumAction {
    data object ClickPlay : AlbumAction
    data object ClickRandomPlay : AlbumAction
    data class ClickTrack(val trackId: Long) : AlbumAction
}