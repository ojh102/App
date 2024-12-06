package com.ojh.feature.library

internal sealed interface LibraryAction {
    data object OnResume : LibraryAction
    data object RequestMusicPermission : LibraryAction
    data class GrantMusicPermission(val permissionGranted: Boolean) : LibraryAction
    data object DismissSettingDialogNegativeButton : LibraryAction
    data object ClickSettingDialogPositiveButton : LibraryAction
    data class ClickAlbum(val albumId: Long) : LibraryAction
}