package com.kyrics.state

import androidx.compose.runtime.Immutable

/**
 * Immutable UI state for a single karaoke line.
 * Contains pre-calculated visual properties for efficient rendering.
 */
@Immutable
data class LineUiState(
    /** Whether this line is currently being played (time is within start-end range) */
    val isPlaying: Boolean = false,
    /** Whether this line has already been played (time is past end) */
    val hasPlayed: Boolean = false,
    /** Whether this line is upcoming (time is before start) */
    val isUpcoming: Boolean = true,
    /** Distance from the current playing line (0 if this is the current line) */
    val distanceFromCurrent: Int = 0,
    /** Pre-calculated opacity value (0.0 to 1.0) */
    val opacity: Float = 0.6f,
    /** Pre-calculated scale value (typically 1.0 to 1.1) */
    val scale: Float = 1f,
)
