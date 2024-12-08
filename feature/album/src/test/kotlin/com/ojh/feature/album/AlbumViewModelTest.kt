package com.ojh.feature.album

import app.cash.turbine.test
import com.ojh.core.data.MusicRepository
import com.ojh.core.media.MediaSessionRepository
import com.ojh.core.media.NowPlayingInfoState
import com.ojh.core.model.Album
import com.ojh.core.model.NowPlayingInfo
import com.ojh.core.model.Track
import com.ojh.feature.album.ui.model.toUiModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AlbumViewModelTest {
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val albumParams: AlbumParams = mockk()
    private val musicRepository: MusicRepository = mockk()
    private val mediaSessionRepository: MediaSessionRepository = mockk()

    private lateinit var viewModel: AlbumViewModel

    private val albumId = 0L
    private val trackList = (0..10).map {
        Track(
            id = it.toLong(),
            trackNumber = it,
            title = "title$it",
            artist = "artist$it",
            data = "data$it"
        )
    }
    private val nowPlayingInfoState = NowPlayingInfoState.Connected(
        NowPlayingInfo(id = 0)
    )
    private val album = Album(
        id = 0,
        name = "name0",
        artist = "artist0",
        albumArtUri = "albumArtUri0"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { albumParams.albumId } returns albumId
        coEvery { musicRepository.observeTracksByAlbumId(albumId) } returns flowOf(trackList)
        coEvery { musicRepository.getAlbumById(albumId) } returns album
        coEvery { mediaSessionRepository.observeNowPlayingInfoState() } returns flowOf(
            nowPlayingInfoState
        )
        viewModel = AlbumViewModel(
            albumParams = albumParams,
            musicRepository = musicRepository,
            mediaSessionRepository = mediaSessionRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `데이터를 통해 state 할당하기`() = runTest {
        val expected = AlbumUiState(
            tracks = trackList.mapIndexed { index, track -> track.toUiModel(index) },
            album = album.toUiModel(),
            nowPlayingInfo = nowPlayingInfoState.nowPlayingInfo.toUiModel()
        )

        viewModel.uiState.test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `순차재생 클릭 시 순차재생 실행`() = runTest {
        val expected = AlbumSideEffect.StartMusicPlayService(
            albumId = albumId,
            isShuffled = false
        )

        viewModel.sideEffect.test {
            viewModel.onAction(AlbumAction.ClickSequencePlay)
            val actual = awaitItem()
            assertTrue(actual is AlbumSideEffect.StartMusicPlayService)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `랜덤재생 클릭 시 랜덤재생 실행`() = runTest {
        val expected = AlbumSideEffect.StartMusicPlayService(
            albumId = albumId,
            isShuffled = true,
        )

        viewModel.sideEffect.test {
            viewModel.onAction(AlbumAction.ClickRandomPlay)
            val actual = awaitItem()
            assertTrue(actual is AlbumSideEffect.StartMusicPlayService)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `선택한 트랙 클릭 시 선택트랙부터 재생 실행`() = runTest {
        val selectedTrackId = 2L
        val expected = AlbumSideEffect.StartMusicPlayService(
            albumId = albumId,
            isShuffled = false,
            selectedTrackId = selectedTrackId
        )

        viewModel.sideEffect.test {
            viewModel.onAction(AlbumAction.ClickTrack(selectedTrackId))
            val actual = awaitItem()
            assertTrue(actual is AlbumSideEffect.StartMusicPlayService)
            assertEquals(expected, actual)
        }
    }
}