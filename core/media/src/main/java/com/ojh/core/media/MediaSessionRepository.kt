package com.ojh.core.media

import kotlinx.coroutines.flow.Flow

interface MediaSessionRepository {
    fun observeNowPlayingInfoState(): Flow<NowPlayingInfoState>
    fun playOrPause()
    fun next()
    fun prev()
    fun shuffle()
    fun repeat()
    fun changeVolume(volume: Float)
    fun changeProgress(progress: Float)
}