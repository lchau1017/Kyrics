package com.kyrics.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine
import com.kyrics.rendering.KaraokeCanvas
import com.kyrics.rendering.KaraokeMath
import com.kyrics.state.LineUiState

/**
 * Stateless composable for displaying a single karaoke line with synchronized highlighting.
 * Uses pre-calculated LineUiState for efficient rendering.
 */
@Composable
internal fun KyricsSingleLine(
    line: KyricsLine,
    lineUiState: LineUiState,
    currentTimeMs: Int,
    config: KyricsConfig,
    modifier: Modifier = Modifier,
    onLineClick: ((KyricsLine) -> Unit)? = null,
) {
    val animatedScale by animateFloatAsState(
        targetValue = lineUiState.scale,
        animationSpec =
            tween(
                durationMillis = config.animation.lineAnimationDuration.toInt(),
                easing = FastOutSlowInEasing,
            ),
        label = "lineScale",
    )

    val animatedOpacity by animateFloatAsState(
        targetValue = lineUiState.opacity,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "lineOpacity",
    )

    val animatedBlur by animateFloatAsState(
        targetValue = lineUiState.blurRadius,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "lineBlur",
    )

    val pulseScale =
        if (config.animation.enablePulse && lineUiState.isPlaying) {
            KaraokeMath.calculatePulseScale(
                currentTimeMs = currentTimeMs,
                minScale = config.animation.pulseMinScale,
                maxScale = config.animation.pulseMaxScale,
                duration = config.animation.pulseDuration,
            )
        } else {
            1f
        }

    val textStyle = createTextStyle(line, config)
    val textColor = calculateTextColor(line, lineUiState, config)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(config.layout.linePadding)
                .scale(animatedScale * pulseScale)
                .alpha(animatedOpacity)
                .then(
                    if (animatedBlur > 0f) {
                        Modifier.blur(animatedBlur.dp)
                    } else {
                        Modifier
                    },
                ).then(
                    if (config.layout.enableLineClick && onLineClick != null) {
                        Modifier.clickable { onLineClick(line) }
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = getContentAlignment(config.visual.textAlign),
    ) {
        KaraokeCanvas(
            line = line,
            currentTimeMs = currentTimeMs,
            config = config,
            textStyle = textStyle,
            baseColor = textColor,
        )
    }
}

/**
 * Create text style based on line type and configuration
 */
private fun createTextStyle(
    line: KyricsLine,
    config: KyricsConfig,
): TextStyle =
    TextStyle(
        fontSize =
            if (line.isAccompaniment) {
                config.visual.accompanimentFontSize
            } else {
                config.visual.fontSize
            },
        fontWeight = config.visual.fontWeight,
        fontFamily = config.visual.fontFamily,
        letterSpacing = config.visual.letterSpacing,
        textAlign = config.visual.textAlign,
    )

/**
 * Calculate text color based on LineUiState
 */
private fun calculateTextColor(
    line: KyricsLine,
    lineUiState: LineUiState,
    config: KyricsConfig,
): Color =
    when {
        line.isAccompaniment -> config.visual.accompanimentTextColor
        lineUiState.isPlaying -> config.visual.upcomingTextColor
        lineUiState.hasPlayed -> config.visual.playedTextColor
        else -> config.visual.upcomingTextColor
    }

/**
 * Get content alignment based on text align
 */
private fun getContentAlignment(textAlign: TextAlign?): Alignment =
    when (textAlign) {
        TextAlign.Start, TextAlign.Left -> Alignment.CenterStart
        TextAlign.End, TextAlign.Right -> Alignment.CenterEnd
        else -> Alignment.Center
    }
