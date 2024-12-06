package com.ojh.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojh.core.data.MusicRepository
import com.ojh.core.data.PermissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val permissionRepository: PermissionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LibrarySideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            uiState.map { it.musicPermissionGranted }
                .filter { it }
                .flatMapLatest { musicRepository.observeAlbums() }
                .collect { albums ->
                    _uiState.update { it.copy(albums = albums) }
                }
        }
    }

    fun onAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.OnResume -> syncMusicPermission()
            is LibraryAction.RequestMusicPermission -> requestMusicPermission()
            is LibraryAction.GrantMusicPermission -> grantMusicPermission(action.permissionGranted)
            is LibraryAction.DismissSettingDialogNegativeButton -> dismissSettingsDialogNegativeButton()
            is LibraryAction.ClickSettingDialogPositiveButton -> clickSettingsDialogPositiveButton()
            is LibraryAction.ClickAlbum -> clickAlbum(action.albumId)
        }
    }

    private fun produceSideEffect(sideEffect: LibrarySideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    private fun syncMusicPermission() {
        val musicPermissionGranted = permissionRepository.isMusicReadPermissionGranted()
        _uiState.update {
            it.copy(musicPermissionGranted = musicPermissionGranted)
        }
        if (!musicPermissionGranted) {
            requestMusicPermission()
        }
    }

    private fun requestMusicPermission() {
        produceSideEffect(
            LibrarySideEffect.LaunchRequestPermission(
                permission = permissionRepository.permission()
            )
        )
    }

    private fun grantMusicPermission(granted: Boolean) {
        _uiState.update { it.copy(musicPermissionGranted = granted) }
        if (!granted) {
            _uiState.update { it.copy(showSettingsDialog = true) }
        }
    }

    private fun dismissSettingsDialogNegativeButton() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    private fun clickSettingsDialogPositiveButton() {
        produceSideEffect(LibrarySideEffect.NavigateToSettings)
    }

    private fun clickAlbum(albumId: Long) {
        produceSideEffect(LibrarySideEffect.NavigateToAlbum(albumId))
    }
}