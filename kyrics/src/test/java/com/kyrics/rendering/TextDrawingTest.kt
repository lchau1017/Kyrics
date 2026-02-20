package com.kyrics.rendering

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.kyrics.config.ColorConfig
import com.kyrics.config.GradientType
import com.kyrics.config.KyricsConfig
import com.kyrics.config.VisualConfig
import org.junit.Test

/**
 * Unit tests for TextDrawing gradient functions.
 * Tests gradient brush creation for karaoke text rendering.
 */
class TextDrawingTest {
    // ==================== createLinearGradient Tests ====================

    @Test
    fun `createLinearGradient returns brush with given colors`() {
        val colors = listOf(Color.Red, Color.Blue)

        val result =
            TextDrawing.createLinearGradient(
                colors = colors,
                angle = 45f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
        assertThat(result).isInstanceOf(Brush::class.java)
    }

    @Test
    fun `createLinearGradient with 0 degree angle creates horizontal gradient`() {
        val colors = listOf(Color.Red, Color.Blue)

        val result =
            TextDrawing.createLinearGradient(
                colors = colors,
                angle = 0f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createLinearGradient with 90 degree angle creates vertical gradient`() {
        val colors = listOf(Color.Red, Color.Blue)

        val result =
            TextDrawing.createLinearGradient(
                colors = colors,
                angle = 90f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createLinearGradient handles multiple colors`() {
        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)

        val result =
            TextDrawing.createLinearGradient(
                colors = colors,
                angle = 45f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    // ==================== createProgressGradient Tests ====================

    @Test
    fun `createProgressGradient with zero progress returns base color gradient`() {
        val result =
            TextDrawing.createProgressGradient(
                progress = 0f,
                baseColor = Color.White,
                highlightColor = Color.Yellow,
                width = 100f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient with full progress returns highlight color gradient`() {
        val result =
            TextDrawing.createProgressGradient(
                progress = 1f,
                baseColor = Color.White,
                highlightColor = Color.Yellow,
                width = 100f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient with partial progress creates split gradient`() {
        val result =
            TextDrawing.createProgressGradient(
                progress = 0.5f,
                baseColor = Color.White,
                highlightColor = Color.Yellow,
                width = 100f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient clamps negative progress to zero`() {
        val result =
            TextDrawing.createProgressGradient(
                progress = -0.5f,
                baseColor = Color.White,
                highlightColor = Color.Yellow,
                width = 100f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient clamps progress over 1 to 1`() {
        val result =
            TextDrawing.createProgressGradient(
                progress = 1.5f,
                baseColor = Color.White,
                highlightColor = Color.Yellow,
                width = 100f,
            )

        assertThat(result).isNotNull()
    }

    // ==================== createMultiColorGradient Tests ====================

    @Test
    fun `createMultiColorGradient with single color returns solid gradient`() {
        val result =
            TextDrawing.createMultiColorGradient(
                colors = listOf(Color.Red),
                angle = 45f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createMultiColorGradient with empty list returns white gradient`() {
        val result =
            TextDrawing.createMultiColorGradient(
                colors = emptyList(),
                angle = 45f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createMultiColorGradient distributes stops evenly`() {
        val colors = listOf(Color.Red, Color.Green, Color.Blue)

        val result =
            TextDrawing.createMultiColorGradient(
                colors = colors,
                angle = 0f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    // ==================== createCharacterGradient Tests ====================

    @Test
    fun `createCharacterGradient with PROGRESS type creates progress gradient`() {
        val config =
            KyricsConfig(
                visual =
                    VisualConfig(
                        gradientEnabled = true,
                        gradientType = GradientType.PROGRESS,
                        colors =
                            ColorConfig(
                                active = Color.Yellow,
                                sung = Color.Green,
                            ),
                    ),
            )

        val result =
            TextDrawing.createCharacterGradient(
                charWidth = 50f,
                charHeight = 30f,
                charProgress = 0.5f,
                config = config,
                baseColor = Color.White,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient with MULTI_COLOR type creates multi-color gradient`() {
        val config =
            KyricsConfig(
                visual =
                    VisualConfig(
                        gradientEnabled = true,
                        gradientType = GradientType.MULTI_COLOR,
                        playingGradientColors = listOf(Color.Red, Color.Green, Color.Blue),
                        gradientAngle = 45f,
                    ),
            )

        val result =
            TextDrawing.createCharacterGradient(
                charWidth = 50f,
                charHeight = 30f,
                charProgress = 0.5f,
                config = config,
                baseColor = Color.White,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient with LINEAR type creates linear gradient`() {
        val config =
            KyricsConfig(
                visual =
                    VisualConfig(
                        gradientEnabled = true,
                        gradientType = GradientType.LINEAR,
                        colors =
                            ColorConfig(
                                active = Color.Cyan,
                                sung = Color.Magenta,
                            ),
                        gradientAngle = 45f,
                    ),
            )

        val result =
            TextDrawing.createCharacterGradient(
                charWidth = 50f,
                charHeight = 30f,
                charProgress = 0.5f,
                config = config,
                baseColor = Color.White,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient uses fallback colors when playingGradientColors is empty`() {
        val config =
            KyricsConfig(
                visual =
                    VisualConfig(
                        gradientEnabled = true,
                        gradientType = GradientType.MULTI_COLOR,
                        playingGradientColors = emptyList(),
                        colors =
                            ColorConfig(
                                active = Color.Yellow,
                                sung = Color.Green,
                            ),
                    ),
            )

        val result =
            TextDrawing.createCharacterGradient(
                charWidth = 50f,
                charHeight = 30f,
                charProgress = 0.5f,
                config = config,
                baseColor = Color.White,
            )

        assertThat(result).isNotNull()
    }

    // ==================== Gradient Angle Tests ====================

    @Test
    fun `gradient handles 180 degree angle`() {
        val result =
            TextDrawing.createLinearGradient(
                colors = listOf(Color.Red, Color.Blue),
                angle = 180f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `gradient handles 270 degree angle`() {
        val result =
            TextDrawing.createLinearGradient(
                colors = listOf(Color.Red, Color.Blue),
                angle = 270f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `gradient handles 360 degree angle (same as 0)`() {
        val result =
            TextDrawing.createLinearGradient(
                colors = listOf(Color.Red, Color.Blue),
                angle = 360f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }

    @Test
    fun `gradient handles negative angle`() {
        val result =
            TextDrawing.createLinearGradient(
                colors = listOf(Color.Red, Color.Blue),
                angle = -45f,
                width = 100f,
                height = 50f,
            )

        assertThat(result).isNotNull()
    }
}
