package com.kyrics.rendering.character

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsSyllable
import com.kyrics.rendering.CharacterDrawInfo
import com.kyrics.rendering.EffectsManager
import com.kyrics.rendering.RenderingCalculations

/**
 * Groups the configuration parameters that remain constant across all characters in a syllable.
 */
data class CharacterRenderContext(
    val currentTimeMs: Int,
    val config: KyricsConfig,
    val textStyle: TextStyle,
    val baseColor: Color,
    val textMeasurer: TextMeasurer,
)

/**
 * Handles the rendering of individual characters within syllables.
 * Uses RenderingCalculations for state calculations and EffectsManager for Canvas rendering.
 *
 * This is an object (singleton) as it has no mutable state.
 */
object CharacterRenderer {
    fun renderSyllableCharacters(
        drawScope: DrawScope,
        syllable: KyricsSyllable,
        xOffset: Float,
        yOffset: Float,
        context: CharacterRenderContext,
    ) {
        val syllableDuration = syllable.end - syllable.start
        val charCount = syllable.content.length
        val charDuration = if (charCount > 0) syllableDuration.toFloat() / charCount else 0f

        var charX = xOffset

        syllable.content.forEachIndexed { charIndex, char ->
            val charStartTime = syllable.start + (charIndex * charDuration).toInt()
            val charEndTime = syllable.start + ((charIndex + 1) * charDuration).toInt()

            val charColor =
                RenderingCalculations.calculateCharacterColor(
                    currentTimeMs = context.currentTimeMs,
                    charStartTime = charStartTime,
                    charEndTime = charEndTime,
                    baseColor = context.baseColor,
                    playingColor = context.config.visual.playingTextColor,
                    playedColor = context.config.visual.playedTextColor,
                )

            val charLayout = context.textMeasurer.measure(char.toString(), context.textStyle)

            val charProgress =
                RenderingCalculations.calculateProgress(
                    currentTime = context.currentTimeMs,
                    startTime = charStartTime,
                    endTime = charEndTime,
                )

            val isCharActive =
                context.currentTimeMs >= charStartTime &&
                    context.currentTimeMs <= charEndTime +
                    context.config.animation.characterAnimationDuration
                        .toInt()

            val animationState =
                if (context.config.animation.enableCharacterAnimations && isCharActive) {
                    RenderingCalculations.calculateCharacterAnimation(
                        characterStartTime = charStartTime,
                        characterEndTime = charEndTime,
                        currentTime = context.currentTimeMs,
                        animationDuration = context.config.animation.characterAnimationDuration,
                        maxScale = context.config.animation.characterMaxScale,
                        floatOffset = context.config.animation.characterFloatOffset,
                        rotationDegrees = context.config.animation.characterRotationDegrees,
                    )
                } else {
                    null
                }

            val charInfo =
                CharacterDrawInfo(
                    layout = charLayout,
                    x = charX,
                    y = yOffset,
                    color = charColor,
                    progress = charProgress,
                    animationState = animationState,
                )

            EffectsManager.renderCharacterWithEffects(
                drawScope = drawScope,
                charInfo = charInfo,
                config = context.config,
            )

            charX += charLayout.size.width
        }
    }
}
