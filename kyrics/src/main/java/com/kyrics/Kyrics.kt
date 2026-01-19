package com.kyrics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsConfigBuilder
import com.kyrics.config.kyricsConfig
import com.kyrics.models.SyncedLine
import com.kyrics.config.KyricsPresets as ConfigPresets

// ============================================================================
// Type aliases for convenient single-package imports
// ============================================================================

/**
 * A synchronized lyrics line with timing information.
 * @see com.kyrics.models.SyncedLine
 */
public typealias SyncedLine = com.kyrics.models.SyncedLine

/**
 * A lyrics line with syllable-level timing for karaoke highlighting.
 * @see com.kyrics.models.KyricsLine
 */
public typealias KyricsLine = com.kyrics.models.KyricsLine

/**
 * A syllable within a [KyricsLine] with timing information.
 * @see com.kyrics.models.KyricsSyllable
 */
public typealias KyricsSyllable = com.kyrics.models.KyricsSyllable

/**
 * DSL builder for creating [KyricsLine] instances.
 * @see com.kyrics.models.KyricsLineBuilder
 */
public typealias KyricsLineBuilder = com.kyrics.models.KyricsLineBuilder

/**
 * DSL builder for creating lists of [KyricsLine] instances.
 * @see com.kyrics.models.KyricsLyricsBuilder
 */
public typealias KyricsLyricsBuilder = com.kyrics.models.KyricsLyricsBuilder

/**
 * Factory object for creating [KyricsLine] instances.
 * @see com.kyrics.models.KyricsLineFactory
 */
public typealias KyricsLineFactory = com.kyrics.models.KyricsLineFactory

/**
 * Configuration for the Kyrics viewer.
 * @see com.kyrics.config.KyricsConfig
 */
public typealias KyricsConfig = com.kyrics.config.KyricsConfig

/**
 * DSL builder for creating [KyricsConfig] instances.
 * @see com.kyrics.config.KyricsConfigBuilder
 */
public typealias KyricsConfigBuilder = com.kyrics.config.KyricsConfigBuilder

/**
 * Viewer type options for different display styles.
 * @see com.kyrics.config.ViewerType
 */
public typealias ViewerType = com.kyrics.config.ViewerType

// ============================================================================
// Re-export DSL functions for convenient single-package imports
// ============================================================================

/**
 * Creates a [KyricsConfig] using a type-safe DSL builder.
 *
 * @see com.kyrics.config.kyricsConfig for full documentation
 */
fun kyricsConfig(block: KyricsConfigBuilder.() -> Unit): KyricsConfig = com.kyrics.config.kyricsConfig(block)

/**
 * Creates a [KyricsLine][com.kyrics.models.KyricsLine] using a type-safe DSL builder.
 *
 * @see com.kyrics.models.kyricsLine for full documentation
 */
fun kyricsLine(
    start: Int,
    end: Int,
    block: com.kyrics.models.KyricsLineBuilder.() -> Unit,
): com.kyrics.models.KyricsLine = com.kyrics.models.kyricsLine(start, end, block)

/**
 * Creates a list of [KyricsLine][com.kyrics.models.KyricsLine] using a type-safe DSL builder.
 *
 * @see com.kyrics.models.kyricsLyrics for full documentation
 */
fun kyricsLyrics(block: com.kyrics.models.KyricsLyricsBuilder.() -> Unit): List<com.kyrics.models.KyricsLine> =
    com.kyrics.models.kyricsLyrics(block)

/**
 * Creates a simple [KyricsLine][com.kyrics.models.KyricsLine] from plain text.
 *
 * @see com.kyrics.models.kyricsLineFromText for full documentation
 */
fun kyricsLineFromText(
    content: String,
    start: Int,
    end: Int,
): com.kyrics.models.KyricsLine = com.kyrics.models.kyricsLineFromText(content, start, end)

/**
 * Creates a [KyricsLine][com.kyrics.models.KyricsLine] by splitting text on whitespace.
 *
 * @see com.kyrics.models.kyricsLineFromWords for full documentation
 */
fun kyricsLineFromWords(
    content: String,
    start: Int,
    end: Int,
): com.kyrics.models.KyricsLine = com.kyrics.models.kyricsLineFromWords(content, start, end)

/**
 * Creates an accompaniment [KyricsLine][com.kyrics.models.KyricsLine].
 *
 * @see com.kyrics.models.kyricsAccompaniment for full documentation
 */
fun kyricsAccompaniment(
    content: String,
    start: Int,
    end: Int,
): com.kyrics.models.KyricsLine = com.kyrics.models.kyricsAccompaniment(content, start, end)

/**
 * Complete lyrics viewer with automatic scrolling and synchronization.
 *
 * This composable manages the entire lyrics display experience:
 * - Auto-scrolling to keep current line in view
 * - Distance-based visual effects (blur, opacity)
 * - Per-character and per-line animations
 * - Multiple viewer styles (scroll, stacked, carousel, etc.)
 *
 * ## Quick Start
 *
 * ```kotlin
 * // Simplest usage with defaults
 * KyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition
 * )
 *
 * // With a preset
 * KyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition,
 *     config = KyricsPresets.Neon
 * )
 *
 * // With custom configuration using DSL
 * KyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition,
 *     config = kyricsConfig {
 *         colors {
 *             playing = Color.Yellow
 *             played = Color.Green
 *         }
 *         animations {
 *             characterAnimations = true
 *             characterScale = 1.2f
 *         }
 *     }
 * )
 * ```
 *
 * @param lines List of synchronized lines to display. Use [KyricsLine] or implement [SyncedLine].
 * @param currentTimeMs Current playback time in milliseconds.
 * @param config Configuration for visual, animation, and behavior. Use [kyricsConfig] DSL or [KyricsPresets].
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked. Receives the line and its index.
 *
 * @see kyricsConfig for creating custom configurations
 * @see KyricsPresets for predefined styles
 */
@Composable
fun KyricsViewer(
    lines: List<SyncedLine>,
    currentTimeMs: Int,
    config: KyricsConfig = KyricsConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((SyncedLine, Int) -> Unit)? = null,
) {
    com.kyrics.components.KyricsViewer(
        lines = lines,
        currentTimeMs = currentTimeMs,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick,
    )
}

/**
 * Creates a lyrics viewer with inline configuration using DSL.
 *
 * Example:
 * ```kotlin
 * KyricsViewer(
 *     lines = lyrics,
 *     currentTimeMs = position
 * ) {
 *     colors {
 *         playing = Color.Yellow
 *         sung = Color.Green
 *     }
 *     animations {
 *         characterScale = 1.3f
 *     }
 * }
 * ```
 *
 * @param lines List of synchronized lines to display.
 * @param currentTimeMs Current playback time in milliseconds.
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked.
 * @param configBuilder DSL block to configure the viewer.
 */
@Composable
fun KyricsViewer(
    lines: List<SyncedLine>,
    currentTimeMs: Int,
    modifier: Modifier = Modifier,
    onLineClick: ((SyncedLine, Int) -> Unit)? = null,
    configBuilder: KyricsConfigBuilder.() -> Unit,
) {
    val config = kyricsConfig(configBuilder)
    com.kyrics.components.KyricsViewer(
        lines = lines,
        currentTimeMs = currentTimeMs,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick,
    )
}

/**
 * Predefined configurations for common use cases.
 *
 * Available presets:
 * - [Classic] - Simple and clean style
 * - [Neon] - Vibrant neon colors with gradient effects
 * - [Rainbow] - Multi-color gradient animation
 * - [Fire] - Warm colors with flickering animation
 * - [Ocean] - Cool blue tones with wave-like motion
 * - [Retro] - 80s style with bold effects
 * - [Minimal] - Clean, no-frills design
 * - [Elegant] - Subtle gold/silver styling
 * - [Party] - Maximum effects for high energy
 * - [Matrix] - Green monospace cyber style
 *
 * Example:
 * ```kotlin
 * KyricsViewer(
 *     lines = lyrics,
 *     currentTimeMs = position,
 *     config = KyricsPresets.Neon
 * )
 * ```
 */
object KyricsPresets {
    /** Classic style - simple and clean */
    val Classic = ConfigPresets.Classic

    /** Neon style with gradient effects */
    val Neon = ConfigPresets.Neon

    /** Rainbow gradient style */
    val Rainbow = ConfigPresets.Rainbow

    /** Fire effect style with warm colors */
    val Fire = ConfigPresets.Fire

    /** Ocean/Water style with cool colors */
    val Ocean = ConfigPresets.Ocean

    /** Retro 80s style */
    val Retro = ConfigPresets.Retro

    /** Minimal style - clean and simple */
    val Minimal = ConfigPresets.Minimal

    /** Elegant style with subtle effects */
    val Elegant = ConfigPresets.Elegant

    /** Party mode with all effects maxed out */
    val Party = ConfigPresets.Party

    /** Matrix/Cyber style */
    val Matrix = ConfigPresets.Matrix

    /** All available presets as name-config pairs */
    val all = ConfigPresets.allPresets
}
