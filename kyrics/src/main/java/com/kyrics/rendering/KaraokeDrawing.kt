package com.kyrics.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import com.kyrics.config.GradientType
import com.kyrics.config.KyricsConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Per-character rendering state passed to drawing functions.
 */
data class CharacterDrawInfo(
    val layout: TextLayoutResult,
    val x: Float,
    val y: Float,
    val color: Color,
    val progress: Float,
    val animationState: KaraokeMath.CharacterAnimationState? = null,
)

/**
 * Canvas drawing functions for karaoke text.
 * Handles drawing text with gradients, scale, and rotation transforms.
 */
object KaraokeDrawing {
    // ==================== Character Drawing ====================

    /**
     * Draw a character with all configured effects (animation, gradient).
     */
    fun drawCharacterWithEffects(
        drawScope: DrawScope,
        charInfo: CharacterDrawInfo,
        config: KyricsConfig,
    ) {
        val animationState = charInfo.animationState
        with(drawScope) {
            if (animationState != null &&
                (animationState.scale != 1f || animationState.rotation != 0f)
            ) {
                val pivotX = charInfo.x + charInfo.layout.size.width / 2f
                val pivotY = charInfo.y + charInfo.layout.size.height / 2f

                drawIntoCanvas {
                    scale(
                        scale = animationState.scale,
                        pivot = Offset(pivotX, pivotY),
                    ) {
                        rotate(
                            degrees = animationState.rotation,
                            pivot = Offset(pivotX, pivotY),
                        ) {
                            val offsetInfo =
                                charInfo.copy(
                                    x = charInfo.x + animationState.offset.x,
                                    y = charInfo.y + animationState.offset.y,
                                )
                            drawCharacter(this, offsetInfo, config)
                        }
                    }
                }
            } else {
                drawCharacter(this, charInfo, config)
            }
        }
    }

    /**
     * Draw a single character with gradient or solid color.
     */
    private fun drawCharacter(
        drawScope: DrawScope,
        charInfo: CharacterDrawInfo,
        config: KyricsConfig,
    ) {
        with(drawScope) {
            if (config.visual.gradientEnabled && charInfo.progress > 0f) {
                val charSize = charInfo.layout.size
                val gradient =
                    createCharacterGradient(
                        charWidth = charSize.width.toFloat(),
                        charHeight = charSize.height.toFloat(),
                        charProgress = charInfo.progress,
                        config = config,
                        baseColor = charInfo.color,
                    )
                drawText(
                    textLayoutResult = charInfo.layout,
                    brush = gradient,
                    topLeft = Offset(charInfo.x, charInfo.y),
                )
            } else {
                drawText(
                    textLayoutResult = charInfo.layout,
                    color = charInfo.color,
                    topLeft = Offset(charInfo.x, charInfo.y),
                )
            }
        }
    }

    // ==================== Gradient Creation ====================

    /**
     * Create a gradient brush for a character based on configuration.
     */
    fun createCharacterGradient(
        charWidth: Float,
        charHeight: Float,
        charProgress: Float,
        config: KyricsConfig,
        baseColor: Color,
    ): Brush =
        when (config.visual.gradientType) {
            GradientType.PROGRESS -> {
                createProgressGradient(
                    progress = charProgress,
                    baseColor = baseColor,
                    highlightColor = config.visual.colors.active,
                    width = charWidth,
                )
            }
            GradientType.MULTI_COLOR -> {
                val colors =
                    config.visual.playingGradientColors
                        .takeIf { it.size > 1 }
                        ?: listOf(
                            config.visual.colors.active,
                            config.visual.colors.sung,
                        )
                createMultiColorGradient(
                    colors = colors,
                    angle = config.visual.gradientAngle,
                    width = charWidth,
                    height = charHeight,
                )
            }
            else -> {
                createLinearGradient(
                    colors =
                        listOf(
                            config.visual.colors.active,
                            config.visual.colors.sung,
                        ),
                    angle = config.visual.gradientAngle,
                    width = charWidth,
                    height = charHeight,
                )
            }
        }

    /**
     * Create a linear gradient brush based on angle.
     */
    fun createLinearGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f,
    ): Brush {
        val (start, end) = calculateGradientEndpoints(angle, width, height)
        return Brush.linearGradient(
            colors = colors,
            start = start,
            end = end,
        )
    }

    /**
     * Create a progress-based gradient for karaoke highlighting effect.
     * Shows highlight color up to progress point, then base color.
     */
    fun createProgressGradient(
        progress: Float,
        baseColor: Color,
        highlightColor: Color,
        width: Float = 1000f,
    ): Brush {
        if (progress <= 0f) {
            return Brush.linearGradient(
                colors = listOf(baseColor, baseColor),
                start = Offset.Zero,
                end = Offset(width, 0f),
            )
        }

        if (progress >= 1f) {
            return Brush.linearGradient(
                colors = listOf(highlightColor, highlightColor),
                start = Offset.Zero,
                end = Offset(width, 0f),
            )
        }

        val stopPosition = progress.coerceIn(0f, 1f)
        return Brush.linearGradient(
            colorStops =
                arrayOf(
                    0f to highlightColor,
                    stopPosition to highlightColor,
                    stopPosition to baseColor,
                    1f to baseColor,
                ),
            start = Offset.Zero,
            end = Offset(width, 0f),
        )
    }

    /**
     * Create a multi-color gradient with evenly distributed color stops.
     */
    fun createMultiColorGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f,
    ): Brush {
        if (colors.size < 2) {
            val color = colors.firstOrNull() ?: Color.White
            return Brush.linearGradient(colors = listOf(color, color))
        }

        val stops =
            colors
                .mapIndexed { index, color ->
                    index.toFloat() / (colors.size - 1) to color
                }.toTypedArray()

        val (start, end) = calculateGradientEndpoints(angle, width, height)
        return Brush.linearGradient(
            colorStops = stops,
            start = start,
            end = end,
        )
    }

    /**
     * Calculate gradient start and end points based on angle.
     */
    private fun calculateGradientEndpoints(
        angle: Float,
        width: Float,
        height: Float,
    ): Pair<Offset, Offset> {
        val angleRad = angle * PI / 180
        val cos = cos(angleRad).toFloat()
        val sin = sin(angleRad).toFloat()
        val halfWidth = width / 2
        val halfHeight = height / 2

        val start =
            Offset(
                halfWidth - halfWidth * cos - halfHeight * sin,
                halfHeight - halfWidth * sin + halfHeight * cos,
            )
        val end =
            Offset(
                halfWidth + halfWidth * cos + halfHeight * sin,
                halfHeight + halfWidth * sin - halfHeight * cos,
            )
        return start to end
    }
}
