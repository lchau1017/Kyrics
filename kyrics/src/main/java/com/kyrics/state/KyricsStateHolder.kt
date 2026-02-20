package com.kyrics.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsConfigBuilder
import com.kyrics.config.kyricsConfig
import com.kyrics.models.KyricsLine
import kotlin.math.abs

/**
 * State holder for karaoke viewer UI state.
 * This is the single source of truth for all UI state in the karaoke library.
 *
 * Instead of scattering state across multiple viewers and components,
 * this holder centralizes state management and provides a clean API
 * for updating state based on time changes.
 *
 * Usage:
 * ```
 * val stateHolder = rememberKyricsStateHolder(config)
 *
 * LaunchedEffect(currentTimeMs) {
 *     stateHolder.updateTime(currentTimeMs)
 * }
 *
 * KyricsViewer(
 *     stateHolder = stateHolder,
 *     ...
 * )
 * ```
 */
@Stable
class KyricsStateHolder(
    initialConfig: KyricsConfig,
) {
    private val _uiState = mutableStateOf(KyricsUiState())
    private var currentConfigInternal = initialConfig

    /**
     * Current UI state. Observe this in Composables to react to state changes.
     */
    val uiState: State<KyricsUiState> = _uiState

    /**
     * Current configuration. Can be used by viewers for additional settings.
     */
    val currentConfig: KyricsConfig get() = currentConfigInternal

    /**
     * Update the configuration. This will recalculate state with the new config.
     *
     * @param config New configuration to apply
     */
    fun updateConfig(config: KyricsConfig) {
        if (currentConfigInternal == config) return
        currentConfigInternal = config
        // Recalculate state with new config if we have lines
        val currentState = _uiState.value
        if (currentState.lines.isNotEmpty()) {
            _uiState.value =
                KyricsStateCalculator.calculateState(
                    lines = currentState.lines,
                    currentTimeMs = currentState.currentTimeMs,
                )
        }
    }

    /**
     * Set the lines to display. Call this when lyrics are loaded.
     *
     * @param lines List of synchronized lines
     */
    fun setLines(lines: List<KyricsLine>) {
        _uiState.value =
            KyricsStateCalculator.calculateState(
                lines = lines,
                currentTimeMs = _uiState.value.currentTimeMs,
            )
    }

    /**
     * Update the current playback time. Call this on each time tick.
     * This will recalculate all line states based on the new time.
     *
     * @param currentTimeMs Current playback time in milliseconds
     */
    fun updateTime(currentTimeMs: Int) {
        val currentState = _uiState.value
        if (currentState.currentTimeMs == currentTimeMs) {
            return // No change needed
        }

        _uiState.value =
            KyricsStateCalculator.calculateState(
                lines = currentState.lines,
                currentTimeMs = currentTimeMs,
            )
    }

    /**
     * Update both lines and time simultaneously.
     * Useful when switching to new content.
     *
     * @param lines List of synchronized lines
     * @param currentTimeMs Current playback time in milliseconds
     */
    fun update(
        lines: List<KyricsLine>,
        currentTimeMs: Int,
    ) {
        _uiState.value =
            KyricsStateCalculator.calculateState(
                lines = lines,
                currentTimeMs = currentTimeMs,
            )
    }

    /**
     * Reset the state holder to initial state.
     */
    fun reset() {
        _uiState.value = KyricsUiState()
    }

    /**
     * Get the current line index, if any line is currently playing.
     */
    val currentLineIndex: Int?
        get() = _uiState.value.currentLineIndex

    /**
     * Get the currently playing line, if any.
     */
    val currentLine: KyricsLine?
        get() = _uiState.value.currentLine

    /**
     * Check if the state has been initialized with lines.
     */
    val isInitialized: Boolean
        get() = _uiState.value.isInitialized
}

/**
 * Creates and remembers a [KyricsStateHolder] instance.
 *
 * The state holder is remembered across recompositions and survives
 * config changes. Config updates are handled internally via [KyricsStateHolder.updateConfig].
 *
 * @param config Library configuration for visual and layout settings
 * @return A remembered state holder instance
 */
@Composable
fun rememberKyricsStateHolder(config: KyricsConfig = KyricsConfig.Default): KyricsStateHolder {
    val stateHolder =
        remember {
            KyricsStateHolder(config)
        }
    // Update config when it changes without recreating the state holder
    LaunchedEffect(config) {
        stateHolder.updateConfig(config)
    }
    return stateHolder
}

/**
 * Creates and remembers a [KyricsStateHolder] instance with initial lines.
 *
 * @param lines Initial lines to display
 * @param config Library configuration for visual and layout settings
 * @return A remembered state holder instance initialized with lines
 */
@Composable
fun rememberKyricsStateHolder(
    lines: List<KyricsLine>,
    config: KyricsConfig = KyricsConfig.Default,
): KyricsStateHolder {
    val stateHolder =
        remember {
            KyricsStateHolder(config).also { holder ->
                holder.setLines(lines)
            }
        }
    // Update config when it changes without recreating the state holder
    LaunchedEffect(config) {
        stateHolder.updateConfig(config)
    }
    return stateHolder
}

/**
 * Creates and remembers a [KyricsStateHolder] instance with inline DSL configuration.
 *
 * Example usage:
 * ```kotlin
 * val stateHolder = rememberKyricsStateHolder {
 *     colors { playing = Color.Yellow }
 *     typography { fontSize = 28.sp }
 * }
 * ```
 *
 * @param configBuilder DSL builder for configuration
 * @return A remembered state holder instance
 */
@Composable
fun rememberKyricsStateHolder(configBuilder: KyricsConfigBuilder.() -> Unit): KyricsStateHolder {
    val config = kyricsConfig(configBuilder)
    return rememberKyricsStateHolder(config)
}

/**
 * Creates and remembers a [KyricsStateHolder] instance with initial lines and inline DSL configuration.
 *
 * Example usage:
 * ```kotlin
 * val stateHolder = rememberKyricsStateHolder(lyrics) {
 *     colors { playing = Color.Yellow }
 *     typography { fontSize = 28.sp }
 * }
 * ```
 *
 * @param lines Initial lines to display
 * @param configBuilder DSL builder for configuration
 * @return A remembered state holder instance initialized with lines
 */
@Composable
fun rememberKyricsStateHolder(
    lines: List<KyricsLine>,
    configBuilder: KyricsConfigBuilder.() -> Unit,
): KyricsStateHolder {
    val config = kyricsConfig(configBuilder)
    return rememberKyricsStateHolder(lines, config)
}

/**
 * Pure calculation logic for karaoke UI state.
 * This object has no Compose dependencies and is fully unit-testable.
 *
 * Extracts and consolidates state calculation logic.
 */
internal object KyricsStateCalculator {
    /**
     * Calculate the complete UI state based on current time.
     *
     * @param lines List of synchronized lines
     * @param currentTimeMs Current playback time in milliseconds
     * @return Complete UI state ready for rendering
     */
    fun calculateState(
        lines: List<KyricsLine>,
        currentTimeMs: Int,
    ): KyricsUiState {
        if (lines.isEmpty()) {
            return KyricsUiState(currentTimeMs = currentTimeMs, isInitialized = true)
        }

        val currentLineIndex = findCurrentLineIndex(lines, currentTimeMs)

        val lineStates =
            lines
                .mapIndexed { index, line ->
                    index to
                        calculateLineState(
                            line = line,
                            lineIndex = index,
                            currentLineIndex = currentLineIndex,
                            currentTimeMs = currentTimeMs,
                        )
                }.toMap()

        return KyricsUiState(
            lines = lines,
            currentTimeMs = currentTimeMs,
            currentLineIndex = currentLineIndex,
            lineStates = lineStates,
            isInitialized = true,
        )
    }

    /**
     * Find the index of the currently playing line.
     *
     * @param lines List of synchronized lines
     * @param currentTimeMs Current playback time
     * @return Index of the playing line, or null if no line is playing
     */
    fun findCurrentLineIndex(
        lines: List<KyricsLine>,
        currentTimeMs: Int,
    ): Int? =
        lines
            .indexOfFirst { line ->
                currentTimeMs >= line.start && currentTimeMs <= line.end
            }.takeIf { it != -1 }

    /**
     * Calculate the UI state for a single line.
     *
     * @param line The line to calculate state for
     * @param lineIndex Index of this line in the list
     * @param currentLineIndex Index of the currently playing line (if any)
     * @param currentTimeMs Current playback time
     * @return Calculated line UI state
     */
    fun calculateLineState(
        line: KyricsLine,
        lineIndex: Int,
        currentLineIndex: Int?,
        currentTimeMs: Int,
    ): LineUiState {
        val isPlaying = currentTimeMs >= line.start && currentTimeMs <= line.end
        val hasPlayed = currentTimeMs > line.end
        val isUpcoming = currentTimeMs < line.start

        val distanceFromCurrent = calculateDistanceFromCurrent(lineIndex, currentLineIndex)

        val opacity =
            calculateOpacity(
                isPlaying = isPlaying,
                hasPlayed = hasPlayed,
                distance = distanceFromCurrent,
            )

        val scale = calculateScale(isPlaying = isPlaying)

        return LineUiState(
            isPlaying = isPlaying,
            hasPlayed = hasPlayed,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            opacity = opacity,
            scale = scale,
        )
    }

    /**
     * Calculate the distance (in lines) from the current playing line.
     */
    fun calculateDistanceFromCurrent(
        lineIndex: Int,
        currentLineIndex: Int?,
    ): Int =
        if (currentLineIndex != null) {
            abs(lineIndex - currentLineIndex)
        } else {
            lineIndex
        }

    /**
     * Calculate opacity based on line state and distance.
     */
    fun calculateOpacity(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        distance: Int,
    ): Float {
        if (isPlaying) return 1f
        if (hasPlayed) return 0.25f
        val distanceReduction = (distance * 0.1f).coerceAtMost(0.4f)
        return (0.6f - distanceReduction).coerceAtLeast(0.2f)
    }

    /**
     * Calculate scale based on playing state.
     */
    fun calculateScale(isPlaying: Boolean): Float = if (isPlaying) 1.05f else 1f
}
