package com.ojh.feature.album

internal sealed interface AlbumSideEffect {
    data class StartMusicPlayService(
        val albumId: Long,
        val isShuffled: Boolean,
        val selectedTrackId: Long? = null,
    ) : AlbumSideEffect
}