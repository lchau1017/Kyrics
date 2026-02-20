package com.kyrics.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import com.kyrics.config.KyricsConfig

/**
 * Groups per-character rendering state for passing between rendering stages.
 */
data class CharacterDrawInfo(
    val layout: TextLayoutResult,
    val x: Float,
    val y: Float,
    val color: Color,
    val progress: Float,
    val animationState: RenderingCalculations.CharacterAnimationState? = null,
)

/**
 * Handles Canvas-based rendering of characters with effects.
 * Delegates calculations to RenderingCalculations and gradients to GradientFactory.
 *
 * This is an object (singleton) as it has no mutable state.
 */
object EffectsManager {
    /**
     * Render a character with all configured effects (animation, gradient).
     */
    fun renderCharacterWithEffects(
        drawScope: DrawScope,
        charInfo: CharacterDrawInfo,
        config: KyricsConfig,
    ) {
        val animationState = charInfo.animationState
        with(drawScope) {
            if (animationState != null && (animationState.scale != 1f || animationState.rotation != 0f)) {
                val pivotX = charInfo.x + charInfo.layout.size.width / 2f
                val pivotY = charInfo.y + charInfo.layout.size.height / 2f

                drawIntoCanvas {
                    scale(scale = animationState.scale, pivot = Offset(pivotX, pivotY)) {
                        rotate(degrees = animationState.rotation, pivot = Offset(pivotX, pivotY)) {
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
                    GradientFactory.createCharacterGradient(
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
}
