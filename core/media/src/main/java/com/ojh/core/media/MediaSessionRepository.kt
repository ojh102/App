package com.ojh.core.media

import com.ojh.core.model.MusicInfo
import kotlinx.coroutines.flow.Flow

interface MediaSessionRepository {
    fun observeNowPlayingMusicInfo(): Flow<MusicInfo>
    fun playOrPause()
    fun next()
    fun prev()
    fun shuffle()
    fun repeat()
}