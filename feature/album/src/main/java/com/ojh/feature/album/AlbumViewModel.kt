package com.ojh.feature.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojh.core.data.MusicRepository
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
internal class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val musicRepository: MusicRepository,
    private val mediaSessionRepository: MediaSessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AlbumSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val albumId = savedStateHandle.get<Long>(ALBUM_UD)
        ?: throw IllegalArgumentException("invalid args")

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(album = musicRepository.getAlbumById(albumId)) }
        }
        viewModelScope.launch {
            musicRepository.observeTracksByAlbumId(albumId)
                .collectLatest { tracks ->
                    _uiState.update { it.copy(tracks = tracks) }
                }
        }
        viewModelScope.launch {
            mediaSessionRepository.observeNowPlayingMusicInfo()
                .collectLatest { nowPlayingMusicInfo ->
                    _uiState.update { it.copy(nowPlayingMusicInfo = nowPlayingMusicInfo) }
                }
        }
    }

    fun onAction(action: AlbumAction) {
        when (action) {
            is AlbumAction.ClickSequencePlay -> clickSequencePlay()
            is AlbumAction.ClickRandomPlay -> clickRandomPlay()
            is AlbumAction.ClickTrack -> clickTrack(action.trackId)
        }
    }

    private fun produceSideEffect(sideEffect: AlbumSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    private fun clickSequencePlay() {
        produceSideEffect(
            AlbumSideEffect.StartMusicPlayService(
                albumId = albumId,
                isShuffled = false
            )
        )
    }

    private fun clickRandomPlay() {
        produceSideEffect(
            AlbumSideEffect.StartMusicPlayService(
                albumId = albumId,
                isShuffled = true
            )
        )
    }

    private fun clickTrack(trackId: Long) {
        produceSideEffect(
            AlbumSideEffect.StartMusicPlayService(
                albumId = albumId,
                isShuffled = false,
                selectedTrackId = trackId
            )
        )
    }

    companion object {
        private const val ALBUM_UD = "album_id"
    }
}