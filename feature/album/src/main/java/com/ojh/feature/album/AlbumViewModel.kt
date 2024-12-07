package com.ojh.feature.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojh.core.data.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AlbumViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val albumId = savedStateHandle.get<Long>(ALBUM_UD)
            ?: throw IllegalArgumentException("invalid args")
        viewModelScope.launch {
            _uiState.update { it.copy(album = musicRepository.getAlbumById(albumId)) }
        }
        viewModelScope.launch {
            musicRepository.observeTracksByAlbumId(albumId)
                .collectLatest { tracks ->
                    _uiState.update { it.copy(tracks = tracks) }
                }
        }
    }

    fun onAction(action: AlbumAction) {
    }

    companion object {
        private const val ALBUM_UD = "album_id"
    }
}