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
                StateCalculator.calculateState(
                    lines = currentState.lines,
                    currentTimeMs = currentState.currentTimeMs,
                    visualConfig = config.visual,
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
            StateCalculator.calculateState(
                lines = lines,
                currentTimeMs = _uiState.value.currentTimeMs,
                visualConfig = currentConfigInternal.visual,
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
            StateCalculator.calculateState(
                lines = currentState.lines,
                currentTimeMs = currentTimeMs,
                visualConfig = currentConfigInternal.visual,
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
            StateCalculator.calculateState(
                lines = lines,
                currentTimeMs = currentTimeMs,
                visualConfig = currentConfigInternal.visual,
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
