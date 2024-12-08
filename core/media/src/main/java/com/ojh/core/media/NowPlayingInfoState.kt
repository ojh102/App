package com.ojh.core.media

import com.ojh.core.model.NowPlayingInfo

sealed interface NowPlayingInfoState {
    data object DisConnected : NowPlayingInfoState
    data class Connected(val nowPlayingInfo: NowPlayingInfo) : NowPlayingInfoState
}