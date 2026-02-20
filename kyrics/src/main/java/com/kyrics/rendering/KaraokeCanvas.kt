package com.kyrics.rendering

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Configuration that stays constant for all characters in a rendering pass.
 */
private data class RenderContext(
    val currentTimeMs: Int,
    val config: KyricsConfig,
    val textStyle: TextStyle,
    val baseColor: Color,
    val textMeasurer: TextMeasurer,
)

/**
 * Composable that renders a karaoke line character by character on Canvas.
 * Handles layout, per-character timing, color, animation, and drawing.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun KaraokeCanvas(
    line: KyricsLine,
    currentTimeMs: Int,
    config: KyricsConfig,
    textStyle: TextStyle,
    baseColor: Color,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()

        val layoutInfo =
            remember(line, textStyle, maxWidthPx) {
                KaraokeLayout.calculateLayout(
                    line = line,
                    textMeasurer = textMeasurer,
                    textStyle = textStyle,
                    maxWidth = maxWidthPx,
                )
            }

        val ctx =
            RenderContext(
                currentTimeMs = currentTimeMs,
                config = config,
                textStyle = textStyle,
                baseColor = baseColor,
                textMeasurer = textMeasurer,
            )

        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        with(density) {
                            layoutInfo.totalHeight.toDp()
                        },
                    ),
        ) {
            layoutInfo.lines.forEachIndexed { lineIndex, lineData ->
                val yPosition = lineIndex * layoutInfo.lineHeight

                lineData.syllables.forEach { syllableData ->
                    drawSyllableCharacters(
                        syllable = syllableData.syllable,
                        xOffset = syllableData.xOffset,
                        yOffset = yPosition,
                        ctx = ctx,
                    )
                }
            }
        }
    }
}

/**
 * Draws each character in a syllable with timing-based color and animation.
 * Divides syllable duration evenly across characters, then for each:
 * 1. KaraokeMath calculates color, progress, and animation state
 * 2. KaraokeDrawing draws the character on Canvas
 */
private fun DrawScope.drawSyllableCharacters(
    syllable: KyricsSyllable,
    xOffset: Float,
    yOffset: Float,
    ctx: RenderContext,
) {
    val syllableDuration = syllable.end - syllable.start
    val charCount = syllable.content.length
    val charDuration =
        if (charCount > 0) syllableDuration.toFloat() / charCount else 0f

    var charX = xOffset

    syllable.content.forEachIndexed { charIndex, char ->
        val charStartTime =
            syllable.start + (charIndex * charDuration).toInt()
        val charEndTime =
            syllable.start + ((charIndex + 1) * charDuration).toInt()

        val charInfo =
            buildCharacterDrawInfo(
                char = char,
                charStartTime = charStartTime,
                charEndTime = charEndTime,
                charX = charX,
                yOffset = yOffset,
                ctx = ctx,
            )

        KaraokeDrawing.drawCharacterWithEffects(
            drawScope = this,
            charInfo = charInfo,
            config = ctx.config,
        )

        charX += charInfo.layout.size.width
    }
}

/**
 * Builds the draw info for a single character: color, layout, progress, animation.
 */
private fun buildCharacterDrawInfo(
    char: Char,
    charStartTime: Int,
    charEndTime: Int,
    charX: Float,
    yOffset: Float,
    ctx: RenderContext,
): CharacterDrawInfo {
    val charColor =
        KaraokeMath.calculateCharacterColor(
            currentTimeMs = ctx.currentTimeMs,
            charStartTime = charStartTime,
            charEndTime = charEndTime,
            baseColor = ctx.baseColor,
            playingColor = ctx.config.visual.playingTextColor,
            playedColor = ctx.config.visual.playedTextColor,
        )

    val charLayout =
        ctx.textMeasurer.measure(char.toString(), ctx.textStyle)

    val charProgress =
        KaraokeMath.calculateProgress(
            currentTime = ctx.currentTimeMs,
            startTime = charStartTime,
            endTime = charEndTime,
        )

    val animDurationInt =
        ctx.config.animation.characterAnimationDuration
            .toInt()
    val isCharActive =
        ctx.currentTimeMs >= charStartTime &&
            ctx.currentTimeMs <= charEndTime + animDurationInt

    val animationState =
        if (ctx.config.animation.enableCharacterAnimations && isCharActive) {
            KaraokeMath.calculateCharacterAnimation(
                characterStartTime = charStartTime,
                characterEndTime = charEndTime,
                currentTime = ctx.currentTimeMs,
                animationDuration =
                    ctx.config.animation.characterAnimationDuration,
                maxScale = ctx.config.animation.characterMaxScale,
                floatOffset = ctx.config.animation.characterFloatOffset,
                rotationDegrees =
                    ctx.config.animation.characterRotationDegrees,
            )
        } else {
            null
        }

    return CharacterDrawInfo(
        layout = charLayout,
        x = charX,
        y = yOffset,
        color = charColor,
        progress = charProgress,
        animationState = animationState,
    )
}
