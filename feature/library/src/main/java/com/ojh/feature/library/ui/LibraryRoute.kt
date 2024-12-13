package com.ojh.feature.library.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojh.core.compose.OnLifecycleEvent
import com.ojh.core.compose.theme.AppTheme
import com.ojh.feature.library.LibraryAction
import com.ojh.feature.library.LibrarySideEffect
import com.ojh.feature.library.LibraryUiState
import com.ojh.feature.library.LibraryViewModel
import com.ojh.feature.library.ui.model.AlbumUiModel
import timber.log.Timber

@Composable
fun LibraryRoute(
    modifier: Modifier = Modifier,
    onNavigateToAlbum: (Long) -> Unit
) {
    LibraryScreen(modifier = modifier, onNavigateToAlbum = onNavigateToAlbum)
}

@Composable
private fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    onNavigateToAlbum: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permissionsGranted ->
        viewModel.onAction(LibraryAction.GrantMusicPermission(permissionsGranted))
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect {
            when (it) {
                is LibrarySideEffect.LaunchRequestPermission -> {
                    launcher.launch(it.permission)
                }

                is LibrarySideEffect.NavigateToSettings -> {
                    navigateToSettings(context)
                }

                is LibrarySideEffect.NavigateToAlbum -> {
                    onNavigateToAlbum(it.albumId)
                }
            }
        }
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> viewModel.onAction(LibraryAction.SyncPermission)
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(LibraryAction.RequestMusicPermission)
    }

    LibraryContent(
        uiState = uiState,
        modifier = modifier,
        onAction = viewModel::onAction
    )
}

@Composable
private fun LibraryContent(
    uiState: LibraryUiState,
    onAction: (LibraryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.musicPermissionGranted) {
        Box(modifier = modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.albums.isEmpty()) {
                    item { CircularProgressIndicator() }
                }
                items(items = uiState.albums, key = { it.id }) {
                    AlbumItem(
                        album = it,
                        onClick = { albumId -> onAction(LibraryAction.ClickAlbum(albumId)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {
        NeedPermissionLayout(onClickRequestPermission = { onAction(LibraryAction.RequestMusicPermission) })

        if (uiState.showSettingsDialog) {
            SettingsDialog(
                onDismissRequest = { onAction(LibraryAction.DismissSettingDialogNegativeButton) },
                onClickPositiveButton = { onAction(LibraryAction.ClickSettingDialogPositiveButton) },
                onClickNegativeButton = { onAction(LibraryAction.DismissSettingDialogNegativeButton) }
            )
        }
    }
}

private fun navigateToSettings(context: Context) {
    runCatching {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:${context.packageName}"))
        context.startActivity(intent)
    }.onFailure {
        Timber.e(it)
        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        context.startActivity(intent)
    }
}

@Preview
@Composable
private fun LibraryContentPreview_denied() {
    AppTheme {
        LibraryContent(
            uiState = LibraryUiState(
                musicPermissionGranted = false
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun LibraryContentPreview_granted() {
    AppTheme {
        LibraryContent(
            uiState = LibraryUiState(
                musicPermissionGranted = true,
                albums = (0..10).map {
                    AlbumUiModel(
                        id = it.toLong(),
                        name = "앨범$it",
                        artist = "아티스트$it",
                        albumArtUri = "uri$it"
                    )
                }
            ),
            onAction = {}
        )
    }
}