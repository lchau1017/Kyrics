package com.kyrics.state

import com.google.common.truth.Truth.assertThat
import com.kyrics.config.KyricsConfig
import com.kyrics.testdata.TestData
import com.kyrics.testdata.TestData.TimePoints
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for KyricsStateCalculator.
 * Tests the pure calculation logic that determines line states.
 */
class KyricsStateCalculatorTest {
    private val calculator = KyricsStateCalculator
    private lateinit var defaultConfig: KyricsConfig

    @Before
    fun setup() {
        defaultConfig = KyricsConfig.Default
    }

    // ==================== findCurrentLineIndex Tests ====================

    @Test
    fun `findCurrentLineIndex returns null for empty list`() {
        val result = calculator.findCurrentLineIndex(emptyList(), 1000)
        assertThat(result).isNull()
    }

    @Test
    fun `findCurrentLineIndex returns null when time is before all lines`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.BEFORE_ALL_LINES)
        assertThat(result).isNull()
    }

    @Test
    fun `findCurrentLineIndex returns 0 when first line is playing`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.LINE_0_PLAYING)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `findCurrentLineIndex returns null when between lines`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.BETWEEN_LINE_0_AND_1)
        assertThat(result).isNull()
    }

    @Test
    fun `findCurrentLineIndex returns correct index for middle line`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.LINE_1_PLAYING)
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `findCurrentLineIndex returns index at exact start time`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.LINE_2_START)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `findCurrentLineIndex returns index at exact end time`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.LINE_2_END)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `findCurrentLineIndex returns last line index when playing`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.LINE_4_PLAYING)
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun `findCurrentLineIndex returns null when after all lines`() {
        val lines = TestData.createSimpleLines()
        val result = calculator.findCurrentLineIndex(lines, TimePoints.AFTER_ALL_LINES)
        assertThat(result).isNull()
    }

    // ==================== getLineStateCategory Tests ====================

    @Test
    fun `getLineStateCategory returns PLAYING when time is within range`() {
        val line = TestData.createSimpleLines()[0] // 0-2000ms
        val result = calculator.getLineStateCategory(line, 1000)
        assertThat(result).isEqualTo(KyricsStateCalculator.LineStateCategory.PLAYING)
    }

    @Test
    fun `getLineStateCategory returns PLAYING at exact start`() {
        val line = TestData.createSimpleLines()[0] // 0-2000ms
        val result = calculator.getLineStateCategory(line, 0)
        assertThat(result).isEqualTo(KyricsStateCalculator.LineStateCategory.PLAYING)
    }

    @Test
    fun `getLineStateCategory returns PLAYING at exact end`() {
        val line = TestData.createSimpleLines()[0] // 0-2000ms
        val result = calculator.getLineStateCategory(line, 2000)
        assertThat(result).isEqualTo(KyricsStateCalculator.LineStateCategory.PLAYING)
    }

    @Test
    fun `getLineStateCategory returns PLAYED when time is after end`() {
        val line = TestData.createSimpleLines()[0] // 0-2000ms
        val result = calculator.getLineStateCategory(line, 2001)
        assertThat(result).isEqualTo(KyricsStateCalculator.LineStateCategory.PLAYED)
    }

    @Test
    fun `getLineStateCategory returns UPCOMING when time is before start`() {
        val line = TestData.createSimpleLines()[1] // 2500-4500ms
        val result = calculator.getLineStateCategory(line, 2000)
        assertThat(result).isEqualTo(KyricsStateCalculator.LineStateCategory.UPCOMING)
    }

    // ==================== calculateDistanceFromCurrent Tests ====================

    @Test
    fun `calculateDistanceFromCurrent returns 0 for current line`() {
        val result = calculator.calculateDistanceFromCurrent(lineIndex = 2, currentLineIndex = 2)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateDistanceFromCurrent returns positive for lines after current`() {
        val result = calculator.calculateDistanceFromCurrent(lineIndex = 5, currentLineIndex = 2)
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `calculateDistanceFromCurrent returns positive for lines before current`() {
        val result = calculator.calculateDistanceFromCurrent(lineIndex = 1, currentLineIndex = 4)
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `calculateDistanceFromCurrent returns lineIndex when no current line`() {
        val result = calculator.calculateDistanceFromCurrent(lineIndex = 3, currentLineIndex = null)
        assertThat(result).isEqualTo(3)
    }

    // ==================== calculateOpacity Tests ====================

    @Test
    fun `calculateOpacity returns 1 when playing`() {
        val result =
            calculator.calculateOpacity(
                isPlaying = true,
                hasPlayed = false,
                distance = 0,
                config = defaultConfig,
            )
        assertThat(result).isEqualTo(1f)
    }

    @Test
    fun `calculateOpacity returns 0_25 when played`() {
        val result =
            calculator.calculateOpacity(
                isPlaying = false,
                hasPlayed = true,
                distance = 1,
                config = defaultConfig,
            )
        assertThat(result).isEqualTo(0.25f)
    }

    @Test
    fun `calculateOpacity returns 0_6 for close upcoming line`() {
        val result =
            calculator.calculateOpacity(
                isPlaying = false,
                hasPlayed = false,
                distance = 0,
                config = defaultConfig,
            )
        assertThat(result).isEqualTo(0.6f)
    }

    @Test
    fun `calculateOpacity reduces opacity for distant upcoming lines`() {
        val closeResult =
            calculator.calculateOpacity(
                isPlaying = false,
                hasPlayed = false,
                distance = 1,
                config = defaultConfig,
            )
        val farResult =
            calculator.calculateOpacity(
                isPlaying = false,
                hasPlayed = false,
                distance = 3,
                config = defaultConfig,
            )
        assertThat(farResult).isLessThan(closeResult)
    }

    @Test
    fun `calculateOpacity has minimum of 0_2 for very distant lines`() {
        val result =
            calculator.calculateOpacity(
                isPlaying = false,
                hasPlayed = false,
                distance = 100,
                config = defaultConfig,
            )
        // Use tolerance for floating point comparison
        assertThat(result).isWithin(0.001f).of(0.2f)
    }

    // ==================== calculateScale Tests ====================

    @Test
    fun `calculateScale returns 1_05 when playing`() {
        val result = calculator.calculateScale(isPlaying = true, config = defaultConfig)
        assertThat(result).isEqualTo(1.05f)
    }

    @Test
    fun `calculateScale returns 1 when not playing`() {
        val result = calculator.calculateScale(isPlaying = false, config = defaultConfig)
        assertThat(result).isEqualTo(1f)
    }

    // ==================== calculateBlurRadius Tests ====================

    @Test
    fun `calculateBlurRadius always returns 0`() {
        val result =
            calculator.calculateBlurRadius(
                isPlaying = false,
                hasPlayed = true,
                distance = 5,
                config = defaultConfig,
            )
        assertThat(result).isEqualTo(0f)
    }

    // ==================== calculateLineState Tests ====================

    @Test
    fun `calculateLineState returns playing state correctly`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateLineState(
                line = lines[1],
                lineIndex = 1,
                currentLineIndex = 1,
                currentTimeMs = 3500, // Middle of line 1
                config = defaultConfig,
            )

        assertThat(result.isPlaying).isTrue()
        assertThat(result.hasPlayed).isFalse()
        assertThat(result.isUpcoming).isFalse()
        assertThat(result.distanceFromCurrent).isEqualTo(0)
        assertThat(result.opacity).isEqualTo(1f)
    }

    @Test
    fun `calculateLineState returns played state correctly`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateLineState(
                line = lines[0],
                lineIndex = 0,
                currentLineIndex = 2,
                currentTimeMs = 5500, // Line 0 has ended
                config = defaultConfig,
            )

        assertThat(result.isPlaying).isFalse()
        assertThat(result.hasPlayed).isTrue()
        assertThat(result.isUpcoming).isFalse()
        assertThat(result.distanceFromCurrent).isEqualTo(2)
    }

    @Test
    fun `calculateLineState returns upcoming state correctly`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateLineState(
                line = lines[3],
                lineIndex = 3,
                currentLineIndex = 1,
                currentTimeMs = 3500, // Line 3 hasn't started
                config = defaultConfig,
            )

        assertThat(result.isPlaying).isFalse()
        assertThat(result.hasPlayed).isFalse()
        assertThat(result.isUpcoming).isTrue()
        assertThat(result.distanceFromCurrent).isEqualTo(2)
    }

    // ==================== calculateState (full state) Tests ====================

    @Test
    fun `calculateState returns empty state for empty lines but preserves time`() {
        val result =
            calculator.calculateState(
                lines = emptyList(),
                currentTimeMs = 1000,
                config = defaultConfig,
            )

        assertThat(result.lines).isEmpty()
        assertThat(result.currentTimeMs).isEqualTo(1000)
        assertThat(result.currentLineIndex).isNull()
        assertThat(result.lineStates).isEmpty()
        assertThat(result.isInitialized).isTrue()
    }

    @Test
    fun `calculateState returns correct state for all lines`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateState(
                lines = lines,
                currentTimeMs = 3500, // Line 1 is playing
                config = defaultConfig,
            )

        assertThat(result.lines).hasSize(5)
        assertThat(result.currentLineIndex).isEqualTo(1)
        assertThat(result.lineStates).hasSize(5)
        assertThat(result.isInitialized).isTrue()

        // Verify individual line states
        assertThat(result.lineStates[0]?.hasPlayed).isTrue()
        assertThat(result.lineStates[1]?.isPlaying).isTrue()
        assertThat(result.lineStates[2]?.isUpcoming).isTrue()
        assertThat(result.lineStates[3]?.isUpcoming).isTrue()
        assertThat(result.lineStates[4]?.isUpcoming).isTrue()
    }

    @Test
    fun `calculateState returns null currentLineIndex when between lines`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateState(
                lines = lines,
                currentTimeMs = TimePoints.BETWEEN_LINE_0_AND_1,
                config = defaultConfig,
            )

        assertThat(result.currentLineIndex).isNull()
        // Line 0 should be played, rest upcoming
        assertThat(result.lineStates[0]?.hasPlayed).isTrue()
        assertThat(result.lineStates[1]?.isUpcoming).isTrue()
    }

    @Test
    fun `calculateState currentLine property returns correct line`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateState(
                lines = lines,
                currentTimeMs = 3500, // Line 1 is playing
                config = defaultConfig,
            )

        assertThat(result.currentLine).isEqualTo(lines[1])
    }

    @Test
    fun `calculateState currentLine property returns null when no line playing`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateState(
                lines = lines,
                currentTimeMs = TimePoints.BETWEEN_LINE_0_AND_1,
                config = defaultConfig,
            )

        assertThat(result.currentLine).isNull()
    }

    @Test
    fun `calculateState getLineState returns default for invalid index`() {
        val lines = TestData.createSimpleLines()
        val result =
            calculator.calculateState(
                lines = lines,
                currentTimeMs = 1000,
                config = defaultConfig,
            )

        val invalidState = result.getLineState(999)
        assertThat(invalidState).isEqualTo(LineUiState())
    }
}
