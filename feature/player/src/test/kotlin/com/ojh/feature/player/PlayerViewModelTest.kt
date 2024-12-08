package com.ojh.feature.player

import app.cash.turbine.test
import com.ojh.core.media.MediaSessionRepository
import com.ojh.core.media.NowPlayingInfoState
import com.ojh.core.model.NowPlayingInfo
import com.ojh.feature.player.ui.model.toUiModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
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

internal class PlayerViewModelTest {
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val mediaSessionRepository: MediaSessionRepository = mockk(relaxed = true)

    private lateinit var viewModel: PlayerViewModel

    private val nowPlayingInfo = NowPlayingInfo(
        id = 0L,
        title = "title",
        artist = "artist",
        artworkUri = "artworkUri",
        currentPosition = 0,
        duration = 10000,
        isPlaying = true,
        hasNext = true,
        hasPrev = true,
        isRepeated = false,
        isShuffled = true,
        volume = 1.0f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mediaSessionRepository.observeNowPlayingInfoState() } returns flowOf(
            NowPlayingInfoState.Connected(nowPlayingInfo)
        )
        viewModel = PlayerViewModel(mediaSessionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `nowPlayingInfo 가져와서 업데이트 하기`() = runTest {
        viewModel.uiState.test {
            assertEquals(nowPlayingInfo.toUiModel(), awaitItem().nowPlayingInfo)
        }
    }

    @Test
    fun `TogglePlay 시 togglePlay 호출하기`() = runTest {
        viewModel.onAction(PlayerAction.TogglePlay)
        verify(exactly = 1) { mediaSessionRepository.togglePlay() }
    }

    @Test
    fun `ToggleExpand 시 collpase 상태면 expand 하고 expand 상태면 collapse 하기`() = runTest {
        viewModel.uiState.test {
            assertEquals(false, awaitItem().isExpanded)
        }

        viewModel.sideEffect.test {
            viewModel.onAction(PlayerAction.ToggleExpand)
            viewModel.uiState.test {
                assertEquals(true, awaitItem().isExpanded)
            }
            assertEquals(PlayerSideEffect.Expand, awaitItem())

            viewModel.onAction(PlayerAction.ToggleExpand)
            viewModel.uiState.test {
                assertEquals(false, awaitItem().isExpanded)
            }
            assertEquals(PlayerSideEffect.Collapse, awaitItem())

            viewModel.onAction(PlayerAction.ToggleExpand)
            viewModel.uiState.test {
                assertEquals(true, awaitItem().isExpanded)
            }
            assertEquals(PlayerSideEffect.Expand, awaitItem())

            viewModel.onAction(PlayerAction.ToggleExpand)
            viewModel.uiState.test {
                assertEquals(false, awaitItem().isExpanded)
            }
            assertEquals(PlayerSideEffect.Collapse, awaitItem())
        }
    }

    @Test
    fun `expand 상태에서 뒤로가기 누르면 collapse 되기`() = runTest {
        viewModel.uiState.test {
            assertEquals(false, awaitItem().isExpanded)
        }

        // expand 시키기
        viewModel.sideEffect.test {
            viewModel.onAction(PlayerAction.ToggleExpand)
            viewModel.uiState.test {
                assertEquals(true, awaitItem().isExpanded)
            }
            assertEquals(PlayerSideEffect.Expand, awaitItem())
        }

        viewModel.sideEffect.test {
            viewModel.onAction(PlayerAction.ClickBack)
            viewModel.uiState.test {
                assertEquals(false, awaitItem().isExpanded)
            }
            assertEquals(PlayerSideEffect.Collapse, awaitItem())
        }
    }

    @Test
    fun `ClickNext 시 mediaSessionRepository의 next 호출`() = runTest {
        viewModel.onAction(PlayerAction.ClickNext)
        verify(exactly = 1) { mediaSessionRepository.next() }
    }

    @Test
    fun `ClickPrev 시 mediaSessionRepository의 prev 호출`() = runTest {
        viewModel.onAction(PlayerAction.ClickPrev)
        verify(exactly = 1) { mediaSessionRepository.prev() }
    }

    @Test
    fun `ClickRepeat 시 mediaSessionRepository의 repeat 호출`() = runTest {
        viewModel.onAction(PlayerAction.ClickRepeat)
        verify(exactly = 1) { mediaSessionRepository.repeat() }
    }

    @Test
    fun `ClickShuffle 시 mediaSessionRepository의 shuffle 호출`() = runTest {
        viewModel.onAction(PlayerAction.ClickShuffle)
        verify(exactly = 1) { mediaSessionRepository.shuffle() }
    }

    @Test
    fun `ChangeVolume 시 mediaSessionRepository의 changeVolume 호출`() = runTest {
        val volume = 0.5f
        viewModel.onAction(PlayerAction.ChangeVolume(volume))
        verify(exactly = 1) { mediaSessionRepository.changeVolume(volume) }
    }

    @Test
    fun `ChangeProgress 시 mediaSessionRepository의 changeProgress 호출`() = runTest {
        val progress = 0.2f
        viewModel.onAction(PlayerAction.ChangeProgress(progress))
        verify(exactly = 1) { mediaSessionRepository.changeProgress(progress) }
    }
}