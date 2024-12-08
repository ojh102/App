package com.ojh.feature.library

import app.cash.turbine.test
import com.ojh.core.data.MusicRepository
import com.ojh.core.data.PermissionRepository
import com.ojh.core.model.Album
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class LibraryViewModelTest {
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val musicRepository: MusicRepository = mockk(relaxed = true)
    private val permissionRepository: PermissionRepository = mockk(relaxed = true)

    private lateinit var viewModel: LibraryViewModel

    private val albums = (0..10).map {
        Album(
            id = it.toLong(),
            name = "name$it",
            artist = "artist$it",
            albumArtUri = "albumArtUri$it",
        )
    }
    private val permission = "musicPermission"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { permissionRepository.isMusicReadPermissionGranted() } returns true
        coEvery { permissionRepository.permission() } returns permission
        coEvery { musicRepository.observeAlbums() } returns flowOf(albums)
        viewModel = LibraryViewModel(
            musicRepository = musicRepository,
            permissionRepository = permissionRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `권한이 없을 시 권한요청하면 권한요청 실행`() = runTest {
        val expected = LibrarySideEffect.LaunchRequestPermission(
            permission = permission
        )
        coEvery { permissionRepository.isMusicReadPermissionGranted() } returns false
        viewModel.sideEffect.test {
            viewModel.onAction(LibraryAction.RequestMusicPermission)
            val actual = awaitItem()
            assertTrue(actual is LibrarySideEffect.LaunchRequestPermission)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `권한이 있을 시 권한요청하면 권한요청 미실행`() = runTest {
        coEvery { permissionRepository.isMusicReadPermissionGranted() } returns true
        viewModel.sideEffect.test {
            viewModel.onAction(LibraryAction.RequestMusicPermission)
            expectNoEvents()
        }
    }

    @Test
    fun `SyncPermission 시 현재의 권한상태로 state 업데이트`() = runTest {
        viewModel.uiState.test {
            coEvery { permissionRepository.isMusicReadPermissionGranted() } returns true
            viewModel.onAction(LibraryAction.SyncPermission)
            assertEquals(true, awaitItem().musicPermissionGranted)

            coEvery { permissionRepository.isMusicReadPermissionGranted() } returns false
            viewModel.onAction(LibraryAction.SyncPermission)
            assertEquals(false, awaitItem().musicPermissionGranted)

            coEvery { permissionRepository.isMusicReadPermissionGranted() } returns true
            viewModel.onAction(LibraryAction.SyncPermission)
            assertEquals(true, awaitItem().musicPermissionGranted)
        }
    }

    @Test
    fun `권한 획득 성공 시 상태 업데이트`() = runTest {
        viewModel.uiState.test {
            viewModel.onAction(LibraryAction.GrantMusicPermission(true))
            val state = awaitItem()
            assertEquals(true, state.musicPermissionGranted)
            assertEquals(false, state.showSettingsDialog)
        }
    }

    @Test
    fun `권한 획득 실패 시 상태 업데이트 및 세팅 다이얼로그 띄우고 dimiss 클릭 시 닫기`() = runTest {
        viewModel.uiState.test {
            coEvery { permissionRepository.isMusicReadPermissionGranted() } returns false
            awaitItem()
            viewModel.onAction(LibraryAction.GrantMusicPermission(false))
            assertEquals(false, awaitItem().musicPermissionGranted)
            assertEquals(true, awaitItem().showSettingsDialog)

            viewModel.onAction(LibraryAction.DismissSettingDialogNegativeButton)
            assertEquals(false, awaitItem().showSettingsDialog)
        }
    }

    @Test
    fun `세팅 다이얼로그에서 설정으로 가기 클릭 시 설정으로 이동 실행`() = runTest {
        val expected = LibrarySideEffect.NavigateToSettings
        viewModel.sideEffect.test {
            viewModel.onAction(LibraryAction.ClickSettingDialogPositiveButton)
            val actual = awaitItem()
            assertTrue(actual is LibrarySideEffect.NavigateToSettings)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `앨범 클릭 시 앨범으로 이동 실행`() = runTest {
        val albumId = 0L
        val expected = LibrarySideEffect.NavigateToAlbum(albumId)
        viewModel.sideEffect.test {
            viewModel.onAction(LibraryAction.ClickAlbum(albumId))
            val actual = awaitItem()
            assertTrue(actual is LibrarySideEffect.NavigateToAlbum)
            assertEquals(expected, actual)
        }
    }
}