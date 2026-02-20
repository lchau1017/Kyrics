package com.kyrics.config

/**
 * Defines different viewer types for the karaoke display.
 * Each type has its own scrolling and positioning behavior.
 */
enum class ViewerType {
    /**
     * Center-focused viewer for karaoke/performance mode.
     * - Active line always centered in viewport
     * - Played lines fade out above
     * - Upcoming lines hidden below
     * - Ideal for: Karaoke apps, live performances, demos
     */
    CENTER_FOCUSED,

    /**
     * Smooth scrolling viewer for reading/subtitle mode.
     * - Active line scrolls to top third of viewport
     * - Multiple lines visible at once
     * - Natural reading flow
     * - Ideal for: Subtitle display, lyrics reading, following along
     */
    SMOOTH_SCROLL,

    /**
     * Fade through viewer.
     * - Pure opacity transitions
     * - No movement, just fades
     * - Minimalist approach
     * - Ideal for: Subtitles, presentations
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
