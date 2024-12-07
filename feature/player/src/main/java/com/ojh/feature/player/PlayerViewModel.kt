package com.ojh.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojh.core.media.MediaSessionRepository
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
            mediaSessionRepository.observeNowPlayingMusicInfo()
                .collectLatest { musicInfo ->
                    _uiState.update { it.copy(musicInfo = musicInfo) }
                }
        }
    }

    fun onAction(action: PlayerAction) {
        when (action) {
            is PlayerAction.ClickPlayOrPause -> clickPlayOrPause()
            is PlayerAction.ClickPlayer -> clickPlayer()
            is PlayerAction.ClickNext -> clickNext()
            is PlayerAction.ClickPrev -> clickPrev()
            is PlayerAction.ClickRepeat -> clickRepeat()
            is PlayerAction.ClickShuffle -> clickShuffle()
        }
    }

    private fun produceSideEffect(sideEffect: PlayerSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    private fun clickPlayOrPause() {
        mediaSessionRepository.playOrPause()
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

    private fun clickPlayer() {
        val isExpanded = !uiState.value.isExpanded
        if (isExpanded) {
            produceSideEffect(PlayerSideEffect.Expand)
        } else {
            produceSideEffect(PlayerSideEffect.Collapse)
        }
        _uiState.update { it.copy(isExpanded = isExpanded) }
    }
}