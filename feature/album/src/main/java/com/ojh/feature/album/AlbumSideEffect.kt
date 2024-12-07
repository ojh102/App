package com.ojh.feature.album

internal sealed interface AlbumSideEffect {
    data class StartMusicPlayService(val albumId: Long, val trackId: Long) : AlbumSideEffect
}