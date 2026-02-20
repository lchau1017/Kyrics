package com.kyrics.state

import com.kyrics.config.VisualConfig
import com.kyrics.models.KyricsLine
import kotlin.math.abs

/**
 * Pure calculation logic for karaoke UI state.
 * This object has no Compose dependencies and is fully unit-testable.
 *
 * Extracts and consolidates state calculation logic.
 */
internal object StateCalculator {
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
        visualConfig: VisualConfig = VisualConfig(),
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
                            visualConfig = visualConfig,
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
        visualConfig: VisualConfig = VisualConfig(),
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

        val blurRadius =
            calculateBlurRadius(
                isPlaying = isPlaying,
                hasPlayed = hasPlayed,
                distance = distanceFromCurrent,
                visualConfig = visualConfig,
            )

        return LineUiState(
            isPlaying = isPlaying,
            hasPlayed = hasPlayed,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            opacity = opacity,
            scale = scale,
            blurRadius = blurRadius,
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

    /**
     * Calculate blur radius based on line state and distance.
     * Returns 0 if blur is disabled.
     */
    fun calculateBlurRadius(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        distance: Int,
        visualConfig: VisualConfig,
    ): Float {
        if (!visualConfig.enableBlur) return 0f
        if (isPlaying) return 0f
        if (hasPlayed) return visualConfig.playedLineBlur.value
        return if (distance > 2) {
            visualConfig.distantLineBlur.value
        } else {
            visualConfig.upcomingLineBlur.value
        }
    }
}
