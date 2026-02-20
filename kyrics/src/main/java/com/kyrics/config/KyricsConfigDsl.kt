package com.kyrics.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DSL marker for karaoke configuration builders.
 */
@DslMarker
annotation class KyricsConfigDsl

/**
 * Creates a [KyricsConfig] using a type-safe DSL builder.
 *
 * Example usage:
 * ```kotlin
 * val config = kyricsConfig {
 *     colors {
 *         playing = Color.Yellow
 *         played = Color.Green
 *         upcoming = Color.White
 *     }
 *     typography {
 *         fontSize = 34.sp
 *         fontWeight = FontWeight.Bold
 *     }
 *     viewer {
 *         type = ViewerType.SMOOTH_SCROLL
 *     }
 * }
 * ```
 */
fun kyricsConfig(block: KyricsConfigBuilder.() -> Unit): KyricsConfig = KyricsConfigBuilder().apply(block).build()

/**
 * Main builder for [KyricsConfig].
 */
@KyricsConfigDsl
class KyricsConfigBuilder {
    private var colorsBuilder = ColorsBuilder()
    private var typographyBuilder = TypographyBuilder()
    private var viewerBuilder = ViewerBuilder()
    private var layoutBuilder = LayoutBuilder()
    private var gradientBuilder: GradientBuilder? = null

    /**
     * Configure text colors for different line states.
     */
    fun colors(block: ColorsBuilder.() -> Unit) {
        colorsBuilder.apply(block)
    }

    /**
     * Configure typography settings (font size, weight, family).
     */
    fun typography(block: TypographyBuilder.() -> Unit) {
        typographyBuilder.apply(block)
    }

    /**
     * Configure the viewer type and behavior.
     */
    fun viewer(block: ViewerBuilder.() -> Unit) {
        viewerBuilder.apply(block)
    }

    /**
     * Configure layout settings (spacing, padding).
     */
    fun layout(block: LayoutBuilder.() -> Unit) {
        layoutBuilder.apply(block)
    }

    /**
     * Configure gradient effects.
     */
    fun gradient(block: GradientBuilder.() -> Unit) {
        gradientBuilder = GradientBuilder().apply(block)
    }

    internal fun build(): KyricsConfig {
        val colors = colorsBuilder.build()
        val typography = typographyBuilder.build()
        val gradient = gradientBuilder?.build()
        val viewer = viewerBuilder.build()
        val layout = layoutBuilder.build(viewer)

        return KyricsConfig(
            visual =
                VisualConfig(
                    playingTextColor = colors.playing,
                    playedTextColor = colors.played,
                    upcomingTextColor = colors.upcoming,
                    accompanimentTextColor = colors.accompaniment,
                    fontSize = typography.fontSize,
                    accompanimentFontSize = typography.accompanimentFontSize,
                    fontFamily = typography.fontFamily,
                    fontWeight = typography.fontWeight,
                    letterSpacing = typography.letterSpacing,
                    textAlign = typography.textAlign,
                    backgroundColor = colors.background,
                    lineBackgroundColor = colors.lineBackground,
                    playingGradientColors = gradient?.colors ?: listOf(Color(0xFF00BCD4), Color(0xFFE91E63)),
                    gradientAngle = gradient?.angle ?: 45f,
                    gradientEnabled = gradient?.enabled ?: false,
                    gradientType = gradient?.type ?: GradientType.LINEAR,
                    colors =
                        ColorConfig(
                            sung = colors.sung,
                            unsung = colors.unsung,
                            active = colors.active,
                        ),
                ),
            layout = layout,
        )
    }
}

/**
 * Builder for color configuration.
 */
@KyricsConfigDsl
class ColorsBuilder {
    /** Color for the currently playing line */
    var playing: Color = Color.White

    /** Color for lines that have been sung */
    var played: Color = Color.Gray

    /** Color for upcoming lines */
    var upcoming: Color = Color.White.copy(alpha = 0.8f)

    /** Color for accompaniment/background vocals */
    var accompaniment: Color = Color(0xFFFFE082)

    /** Background color for the entire viewer */
    var background: Color = Color.Transparent

    /** Background color for individual lines */
    var lineBackground: Color = Color.Transparent

    /** Color for sung portions in gradient mode */
    var sung: Color = Color.Green

    /** Color for unsung portions in gradient mode */
    var unsung: Color = Color.White

    /** Color for active character in gradient mode */
    var active: Color = Color.Yellow

    internal fun build() = this
}

/**
 * Builder for typography configuration.
 */
@KyricsConfigDsl
class TypographyBuilder {
    /** Main font size for lyrics */
    var fontSize: TextUnit = 34.sp

    /** Font size for accompaniment text */
    var accompanimentFontSize: TextUnit = 20.sp

    /** Font family (null = system default) */
    var fontFamily: FontFamily? = null

    /** Font weight */
    var fontWeight: FontWeight = FontWeight.Bold

    /** Letter spacing */
    var letterSpacing: TextUnit = 0.sp

    /** Text alignment */
    var textAlign: TextAlign = TextAlign.Center

    internal fun build() = this
}

/**
 * Builder for viewer configuration.
 */
@KyricsConfigDsl
class ViewerBuilder {
    /** Type of viewer to use */
    var type: ViewerType = ViewerType.SMOOTH_SCROLL

    /** Scroll position for SMOOTH_SCROLL (0.0-1.0, where 0.33 = top third) */
    var scrollPosition: Float = 0.33f

    internal fun build() =
        ViewerConfig(
            type = type,
            scrollPosition = scrollPosition,
        )
}

/**
 * Builder for layout configuration.
 */
@KyricsConfigDsl
class LayoutBuilder {
    /** Padding around each line */
    var linePadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp)

    /** Vertical spacing between lines */
    var lineSpacing: Dp = 12.dp

    /** Spacing between words */
    var wordSpacing: Dp = 4.dp

    /** Spacing between characters */
    var characterSpacing: Dp = 0.dp

    /** Line height multiplier */
    var lineHeight: Float = 1.2f

    /** Line height multiplier for accompaniment */
    var accompanimentLineHeight: Float = 1.0f

    /** Container padding */
    var containerPadding: PaddingValues = PaddingValues(16.dp)

    /** Maximum line width (null = full width) */
    var maxLineWidth: Dp? = null

    /** Force text direction (null = auto-detect) */
    var textDirection: LayoutDirection? = null

    /** Enable line click interactions */
    var enableLineClick: Boolean = true

    internal fun build(viewerConfig: ViewerConfig) =
        LayoutConfig(
            viewerConfig = viewerConfig,
            linePadding = linePadding,
            lineSpacing = lineSpacing,
            wordSpacing = wordSpacing,
            characterSpacing = characterSpacing,
            lineHeightMultiplier = lineHeight,
            accompanimentLineHeightMultiplier = accompanimentLineHeight,
            containerPadding = containerPadding,
            maxLineWidth = maxLineWidth,
            forceTextDirection = textDirection,
            enableLineClick = enableLineClick,
        )
}

/**
 * Builder for gradient configuration.
 */
@KyricsConfigDsl
class GradientBuilder {
    /** Enable gradient effects */
    var enabled: Boolean = true

    /** Gradient type */
    var type: GradientType = GradientType.LINEAR

    /** Gradient angle in degrees */
    var angle: Float = 45f

    /** Colors for multi-color gradient */
    var colors: List<Color> = listOf(Color(0xFF00BCD4), Color(0xFFE91E63))

    internal fun build() = this
}
