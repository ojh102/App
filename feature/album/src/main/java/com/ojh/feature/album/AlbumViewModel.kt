package com.ojh.feature.album

import androidx.lifecycle.ViewModel
import com.ojh.core.data.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class AlbumViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState = _uiState.asStateFlow()

    init {
//        viewModelScope.launch {
//            _uiState.update { it.copy(album = musicRepository.getAlbumById(albumId = )) }
//        }
//        musicRepository.
    }

    fun onAction(action: AlbumAction) {
    }
}