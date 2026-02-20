package com.kyrics.config

/**
 * Defines different viewer types for the karaoke display.
 * Each type has its own scrolling and positioning behavior.
 */
enum class ViewerType {
    /**
     * Smooth scrolling viewer for reading/subtitle mode.
     * - Active line scrolls to top third of viewport
     * - Multiple lines visible at once
     * - Natural reading flow
     */
    SMOOTH_SCROLL,

    /**
     * Fade through viewer with pure opacity transitions.
     * - No movement, just fades between lines
     * - Minimalist approach
     */
    FADE_THROUGH,
}

/**
 * Configuration specific to each viewer type.
 */
data class ViewerConfig(
    val type: ViewerType = ViewerType.SMOOTH_SCROLL,
    // SMOOTH_SCROLL specific: Where to position active line (0.33 = top third)
    val scrollPosition: Float = 0.33f,
)
