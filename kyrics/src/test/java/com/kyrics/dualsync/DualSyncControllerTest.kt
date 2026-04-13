package com.kyrics.dualsync

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kyrics.dualsync.model.DualTrackLyrics
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import com.kyrics.testdata.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DualSyncControllerTest {
    private fun createController(
        lyrics: DualTrackLyrics,
        positionFlow: MutableStateFlow<Long>,
    ): DualSyncController {
        val scope = kotlinx.coroutines.CoroutineScope(UnconfinedTestDispatcher())
        return DualSyncController(lyrics, positionFlow, scope)
    }

    @Test
    fun `both tracks highlight correctly at the same position`() =
        runTest {
            val lyrics = TestData.createDualTrackLyrics()
            val positionFlow = MutableStateFlow(0L)
            val controller = createController(lyrics, positionFlow)

            // At position 0: both tracks should highlight line 0
            val initial = controller.state.value
            assertThat(initial.primaryHighlight.currentLineIndex).isEqualTo(0)
            assertThat(initial.secondaryHighlight.currentLineIndex).isEqualTo(0)

            // Move to middle of line 1 (both tracks)
            positionFlow.value = 3500L
            val midLine1 = controller.state.value
            assertThat(midLine1.primaryHighlight.currentLineIndex).isEqualTo(1)
            assertThat(midLine1.secondaryHighlight.currentLineIndex).isEqualTo(1)
            assertThat(midLine1.primaryHighlight.currentTimeMs).isEqualTo(3500)
            assertThat(midLine1.secondaryHighlight.currentTimeMs).isEqualTo(3500)
        }

    @Test
    fun `position 0 highlights first line in both tracks`() =
        runTest {
            val lyrics = TestData.createDualTrackLyrics()
            val positionFlow = MutableStateFlow(0L)
            val controller = createController(lyrics, positionFlow)

            val state = controller.state.value
            assertThat(state.primaryHighlight.currentLineIndex).isEqualTo(0)
            assertThat(state.secondaryHighlight.currentLineIndex).isEqualTo(0)
            assertThat(state.primaryHighlight.isInitialized).isTrue()
            assertThat(state.secondaryHighlight.isInitialized).isTrue()
        }

    @Test
    fun `end-of-track position has no active line`() =
        runTest {
            val lyrics = TestData.createDualTrackLyrics()
            val positionFlow = MutableStateFlow(99_999L)
            val controller = createController(lyrics, positionFlow)

            val state = controller.state.value
            assertThat(state.primaryHighlight.currentLineIndex).isNull()
            assertThat(state.secondaryHighlight.currentLineIndex).isNull()
        }

    @Test
    fun `empty tracks produce initialized but empty state`() =
        runTest {
            val lyrics = DualTrackLyrics(primary = emptyList(), secondary = emptyList())
            val positionFlow = MutableStateFlow(1000L)
            val controller = createController(lyrics, positionFlow)

            val state = controller.state.value
            assertThat(state.primaryHighlight.lines).isEmpty()
            assertThat(state.secondaryHighlight.lines).isEmpty()
            assertThat(state.primaryHighlight.currentLineIndex).isNull()
            assertThat(state.secondaryHighlight.currentLineIndex).isNull()
            assertThat(state.primaryHighlight.isInitialized).isTrue()
        }

    @Test
    fun `tracks with different line counts work independently`() =
        runTest {
            val primary =
                listOf(
                    KyricsLine(
                        listOf(KyricsSyllable("Hello", 0, 2000)),
                        start = 0,
                        end = 2000,
                    ),
                    KyricsLine(
                        listOf(KyricsSyllable("World", 3000, 5000)),
                        start = 3000,
                        end = 5000,
                    ),
                )
            val secondary =
                listOf(
                    KyricsLine(
                        listOf(KyricsSyllable("\u4F60\u597D", 0, 5000)),
                        start = 0,
                        end = 5000,
                    ),
                )
            val lyrics = DualTrackLyrics(primary = primary, secondary = secondary)
            val positionFlow = MutableStateFlow(4000L)
            val controller = createController(lyrics, positionFlow)

            val state = controller.state.value
            assertThat(state.primaryHighlight.currentLineIndex).isEqualTo(1)
            assertThat(state.secondaryHighlight.currentLineIndex).isEqualTo(0)
        }

    @Test
    fun `line states are calculated for both tracks`() =
        runTest {
            val lyrics = TestData.createDualTrackLyrics()
            val positionFlow = MutableStateFlow(1000L)
            val controller = createController(lyrics, positionFlow)

            val state = controller.state.value
            assertThat(state.primaryHighlight.getLineState(0).isPlaying).isTrue()
            assertThat(state.secondaryHighlight.getLineState(0).isPlaying).isTrue()
            assertThat(state.primaryHighlight.getLineState(1).isUpcoming).isTrue()
            assertThat(state.secondaryHighlight.getLineState(1).isUpcoming).isTrue()
        }

    @Test
    fun `between-lines gap has no active line`() =
        runTest {
            val lyrics = TestData.createDualTrackLyrics()
            val positionFlow = MutableStateFlow(2250L)
            val controller = createController(lyrics, positionFlow)

            val state = controller.state.value
            assertThat(state.primaryHighlight.currentLineIndex).isNull()
            assertThat(state.secondaryHighlight.currentLineIndex).isNull()
            assertThat(state.primaryHighlight.getLineState(0).hasPlayed).isTrue()
            assertThat(state.primaryHighlight.getLineState(1).isUpcoming).isTrue()
        }

    @Test
    fun `state updates when position changes`() =
        runTest {
            val lyrics = TestData.createDualTrackLyrics()
            val positionFlow = MutableStateFlow(0L)
            val controller = createController(lyrics, positionFlow)

            controller.state.test {
                val initial = awaitItem()
                assertThat(initial.primaryHighlight.currentLineIndex).isEqualTo(0)

                positionFlow.value = 3500L
                val updated = awaitItem()
                assertThat(updated.primaryHighlight.currentLineIndex).isEqualTo(1)
                assertThat(updated.secondaryHighlight.currentLineIndex).isEqualTo(1)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
