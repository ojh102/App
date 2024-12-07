package com.ojh.core.model

data class Track(
    val id: Long,
    val order: Int,
    val title: String,
    val artist: String,
    val data: String
)