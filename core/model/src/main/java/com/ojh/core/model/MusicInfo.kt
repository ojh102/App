package com.ojh.core.model

data class MusicInfo(
    val id: Long = 0,
    val title: String? = null,
    val artist: String? = null,
    val artworkUri: String? = null,
    val currentPosition: Long = 0,
    val contentPosition: Long = 0,
    val isPlaying: Boolean = false,
    val hasPrev: Boolean = false,
    val hasNext: Boolean = false,
    val isRepeated: Boolean = false,
    val isShuffled: Boolean = false,
    val volume: Float = 1.0f
)