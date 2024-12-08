package com.ojh.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojh.core.media.MediaSessionRepository
import com.ojh.core.media.NowPlayingInfoState
import com.ojh.feature.player.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PlayerViewModel @Inject constructor(
    private val mediaSessionRepository: MediaSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<PlayerSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            mediaSessionRepository.observeNowPlayingInfoState()
                .collectLatest { nowPlayingInfo ->
                    _uiState.update {
                        it.copy(
                            nowPlayingInfo = (nowPlayingInfo as? NowPlayingInfoState.Connected)
                                ?.nowPlayingInfo
                                ?.toUiModel()
                        )
                    }
                }
        }
    }

    fun onAction(action: PlayerAction) {
        when (action) {
            is PlayerAction.TogglePlay -> togglePlay()
            is PlayerAction.ToggleExpand -> toggleExpand()
            is PlayerAction.ClickBack -> toggleExpand()
            is PlayerAction.ClickNext -> clickNext()
            is PlayerAction.ClickPrev -> clickPrev()
            is PlayerAction.ClickRepeat -> clickRepeat()
            is PlayerAction.ClickShuffle -> clickShuffle()
            is PlayerAction.ChangeVolume -> changeVolume(action.volume)
            is PlayerAction.ChangeProgress -> changeProgress(action.position)
        }
    }

    private fun produceSideEffect(sideEffect: PlayerSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    private fun togglePlay() {
        mediaSessionRepository.togglePlay()
    }

    private fun clickNext() {
        mediaSessionRepository.next()
    }

    private fun clickPrev() {
        mediaSessionRepository.prev()
    }

    private fun clickRepeat() {
        mediaSessionRepository.repeat()
    }

    private fun clickShuffle() {
        mediaSessionRepository.shuffle()
    }

    private fun changeVolume(volume: Float) {
        mediaSessionRepository.changeVolume(volume)
    }

    private fun changeProgress(progress: Float) {
        mediaSessionRepository.changeProgress(progress)
    }

    private fun toggleExpand() {
        val isExpanded = !uiState.value.isExpanded
        if (isExpanded) {
            produceSideEffect(PlayerSideEffect.Expand)
        } else {
            produceSideEffect(PlayerSideEffect.Collapse)
        }
        _uiState.update { it.copy(isExpanded = isExpanded) }
    }
}