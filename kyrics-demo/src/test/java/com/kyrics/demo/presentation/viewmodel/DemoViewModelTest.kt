package com.kyrics.demo.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kyrics.demo.data.datasource.DemoLyricsDataSource
import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.model.LyricsData
import com.kyrics.demo.domain.model.PresetType
import com.kyrics.demo.domain.usecase.GetDemoSettingsUseCase
import com.kyrics.demo.domain.usecase.GetLyricsUseCase
import com.kyrics.demo.domain.usecase.UpdateDemoSettingsUseCase
import com.kyrics.demo.presentation.mapper.DemoUiMapper
import com.kyrics.demo.presentation.model.ColorPickerTarget
import com.kyrics.demo.testutil.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DemoViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testDispatcherProvider = TestDispatcherProvider(testDispatcher)
    private lateinit var getDemoSettingsUseCase: GetDemoSettingsUseCase
    private lateinit var updateDemoSettingsUseCase: UpdateDemoSettingsUseCase
    private lateinit var getLyricsUseCase: GetLyricsUseCase
    private lateinit var uiMapper: DemoUiMapper
    private lateinit var viewModel: DemoViewModel

    private val defaultLyricsData =
        LyricsData(
            lines = emptyList(),
            totalDurationMs = DemoLyricsDataSource.TOTAL_DURATION_MS,
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDemoSettingsUseCase = mockk()
        updateDemoSettingsUseCase = mockk()
        getLyricsUseCase = mockk()
        uiMapper = DemoUiMapper()

        every { getDemoSettingsUseCase() } returns flowOf(DemoSettings.Default)
        coEvery { getLyricsUseCase(any()) } returns defaultLyricsData
        coEvery { updateDemoSettingsUseCase(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DemoViewModel =
        DemoViewModel(
            getDemoSettingsUseCase,
            updateDemoSettingsUseCase,
            getLyricsUseCase,
            testDispatcherProvider,
            uiMapper,
        )

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has default values`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertThat(state.isPlaying).isFalse()
            assertThat(state.currentTimeMs).isEqualTo(0L)
            assertThat(state.selectedLineIndex).isEqualTo(0)
            assertThat(state.showColorPicker).isNull()
        }

    @Test
    fun `initial state loads settings from use case`() =
        runTest {
            val customSettings = DemoSettings(fontSize = 40f)
            every { getDemoSettingsUseCase() } returns flowOf(customSettings)

            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(viewModel.state.value.fontSize).isEqualTo(40f)
        }

    @Test
    fun `initial state loads demo lyrics from use case`() =
        runTest {
            val mockLines = listOf(mockk<com.kyrics.models.KyricsLine>())
            val lyricsData = LyricsData(lines = mockLines, totalDurationMs = 20_000L)
            coEvery { getLyricsUseCase(any()) } returns lyricsData

            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(viewModel.state.value.demoLines).isEqualTo(mockLines)
            assertThat(viewModel.state.value.totalDurationMs).isEqualTo(20_000L)
        }

    // ==================== Playback Tests ====================

    @Test
    fun `TogglePlayPause starts playback`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Playback.TogglePlayPause)
            assertThat(viewModel.state.value.isPlaying).isTrue()

            // Stop playback to cancel the timer coroutine
            viewModel.onIntent(DemoIntent.Playback.TogglePlayPause)
        }

    @Test
    fun `TogglePlayPause pauses when playing`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Playback.TogglePlayPause) // Start
            assertThat(viewModel.state.value.isPlaying).isTrue()

            viewModel.onIntent(DemoIntent.Playback.TogglePlayPause) // Pause
            assertThat(viewModel.state.value.isPlaying).isFalse()
        }

    @Test
    fun `Reset stops playback and resets time`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Playback.TogglePlayPause)
            viewModel.onIntent(DemoIntent.Playback.UpdateTime(5000))
            viewModel.onIntent(DemoIntent.Playback.Reset)

            assertThat(viewModel.state.value.isPlaying).isFalse()
            assertThat(viewModel.state.value.currentTimeMs).isEqualTo(0L)
            assertThat(viewModel.state.value.selectedLineIndex).isEqualTo(0)
        }

    @Test
    fun `Seek updates current time`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Playback.Seek(10_000))

            assertThat(viewModel.state.value.currentTimeMs).isEqualTo(10_000)
        }

    @Test
    fun `UpdateTime wraps around at end of song`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Playback.UpdateTime(DemoLyricsDataSource.TOTAL_DURATION_MS + 100))

            assertThat(viewModel.state.value.currentTimeMs).isEqualTo(0L)
        }

    // ==================== Color Picker Tests ====================

    @Test
    fun `ShowColorPicker sets target`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.ColorPicker.Show(ColorPickerTarget.SUNG_COLOR))

            assertThat(viewModel.state.value.showColorPicker).isEqualTo(ColorPickerTarget.SUNG_COLOR)
        }

    @Test
    fun `DismissColorPicker clears target`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.ColorPicker.Show(ColorPickerTarget.SUNG_COLOR))
            viewModel.onIntent(DemoIntent.ColorPicker.Dismiss)

            assertThat(viewModel.state.value.showColorPicker).isNull()
        }

    @Test
    fun `UpdateColor updates sung color`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.ColorPicker.UpdateColor(ColorPickerTarget.SUNG_COLOR, Color.Red))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.sungColorArgb == Color.Red.value.toLong() }) }
        }

    @Test
    fun `UpdateColor updates unsung color`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.ColorPicker.UpdateColor(ColorPickerTarget.UNSUNG_COLOR, Color.Blue))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.unsungColorArgb == Color.Blue.value.toLong() }) }
        }

    @Test
    fun `UpdateColor updates active color`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.ColorPicker.UpdateColor(ColorPickerTarget.ACTIVE_COLOR, Color.Yellow))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.activeColorArgb == Color.Yellow.value.toLong() }) }
        }

    @Test
    fun `UpdateColor updates background color`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.ColorPicker.UpdateColor(ColorPickerTarget.BACKGROUND_COLOR, Color.Gray))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.backgroundColorArgb == Color.Gray.value.toLong() }) }
        }

    // ==================== Font Settings Tests ====================

    @Test
    fun `UpdateFontSize calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Font.UpdateSize(50f))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.fontSize == 50f }) }
        }

    @Test
    fun `UpdateFontWeight calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Font.UpdateWeight(FontWeight.Light))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.fontWeightValue == FontWeight.Light.weight }) }
        }

    @Test
    fun `UpdateFontFamily calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Font.UpdateFamily(FontFamily.Monospace))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.fontFamilyName == "monospace" }) }
        }

    @Test
    fun `UpdateTextAlign calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Font.UpdateAlign(TextAlign.Start))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.textAlignName == "start" }) }
        }

    // ==================== Viewer Type Tests ====================

    @Test
    fun `SelectViewerType calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Selection.SelectViewerType(5))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.viewerTypeIndex == 5 }) }
        }

    // ==================== Visual Effects Tests ====================

    @Test
    fun `ToggleGradient calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.VisualEffect.ToggleGradient(true))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.gradientEnabled }) }
        }

    @Test
    fun `UpdateGradientAngle calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.VisualEffect.UpdateGradientAngle(180f))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.gradientAngle == 180f }) }
        }

    @Test
    fun `ToggleBlur calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.VisualEffect.ToggleBlur(true))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.blurEnabled }) }
        }

    @Test
    fun `UpdateBlurIntensity calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.VisualEffect.UpdateBlurIntensity(2.5f))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.blurIntensity == 2.5f }) }
        }

    // ==================== Animation Tests ====================

    @Test
    fun `ToggleCharAnimation calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Animation.ToggleCharAnimation(true))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.charAnimEnabled }) }
        }

    @Test
    fun `UpdateCharMaxScale calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Animation.UpdateCharMaxScale(1.5f))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.charMaxScale == 1.5f }) }
        }

    @Test
    fun `ToggleLineAnimation calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Animation.ToggleLineAnimation(true))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.lineAnimEnabled }) }
        }

    @Test
    fun `TogglePulse calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Animation.TogglePulse(true))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.pulseEnabled }) }
        }

    // ==================== Preset Tests ====================

    @Test
    fun `LoadPreset Classic updates settings`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.LoadPreset(PresetType.CLASSIC))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(any()) }
        }

    @Test
    fun `LoadPreset Neon updates settings`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.LoadPreset(PresetType.NEON))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(any()) }
        }

    @Test
    fun `LoadPreset sends PresetLoaded effect`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onIntent(DemoIntent.LoadPreset(PresetType.CLASSIC))
                testDispatcher.scheduler.advanceUntilIdle()

                val effect = awaitItem()
                assertThat(effect).isEqualTo(DemoEffect.PresetLoaded)
            }
        }

    // ==================== Line Selection Tests ====================

    @Test
    fun `SelectLine updates selected line index`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Selection.SelectLine(3))

            assertThat(viewModel.state.value.selectedLineIndex).isEqualTo(3)
        }

    // ==================== Layout Tests ====================

    @Test
    fun `UpdateLineSpacing calls update use case`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(DemoIntent.Layout.UpdateLineSpacing(100f))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateDemoSettingsUseCase(match { it.lineSpacing == 100f }) }
        }
}
