package com.kyrics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsConfigBuilder
import com.kyrics.config.kyricsConfig
import com.kyrics.models.KyricsLine
import com.kyrics.parser.ParseResult
import com.kyrics.config.KyricsPresets as ConfigPresets

/**
 * Parses TTML lyrics content.
 *
 * @param content The raw TTML lyrics file content
 * @return [ParseResult.Success] with parsed lines or [ParseResult.Failure] with error
 */
fun parseLyrics(content: String): ParseResult =
    com.kyrics.parser.LyricsParserFactory
        .parse(content)

/**
 * Complete lyrics viewer with automatic scrolling and synchronization.
 *
 * @param lines List of synchronized lines to display.
 * @param currentTimeMs Current playback time in milliseconds.
 * @param config Configuration for visual and layout. Use [kyricsConfig] DSL or [KyricsPresets].
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked. Receives the line and its index.
 */
@Composable
fun KyricsViewer(
    lines: List<KyricsLine>,
    currentTimeMs: Int,
    config: KyricsConfig = KyricsConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((KyricsLine, Int) -> Unit)? = null,
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
 * @param lines List of synchronized lines to display.
 * @param currentTimeMs Current playback time in milliseconds.
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked.
 * @param configBuilder DSL block to configure the viewer.
 */
@Composable
fun KyricsViewer(
    lines: List<KyricsLine>,
    currentTimeMs: Int,
    modifier: Modifier = Modifier,
    onLineClick: ((KyricsLine, Int) -> Unit)? = null,
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
 */
object KyricsPresets {
    val Classic = ConfigPresets.Classic
    val Neon = ConfigPresets.Neon
    val all = ConfigPresets.allPresets
}
