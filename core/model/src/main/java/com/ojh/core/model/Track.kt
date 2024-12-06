package com.ojh.core.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val data: String
)