package com.kyrics.demo.domain.model

/**
 * Lyrics source format for demo.
 */
enum class LyricsSource(
    val extension: String,
    val displayName: String,
) {
    TTML("ttml", "TTML"),
    ENHANCED_LRC("elrc", "Enhanced LRC"),
    LRC("lrc", "LRC"),
}

/**
 * Pure domain model for demo settings.
 * No framework dependencies - uses primitive types only.
 */
data class DemoSettings(
    // Lyrics source
    val lyricsSource: LyricsSource = LyricsSource.TTML,
    // Text settings
    val fontSize: Float = 32f,
    val fontWeightValue: Int = 700, // Bold = 700
    val fontFamilyName: String = "default",
    val textAlignName: String = "center",
    // Colors (stored as ARGB Long values)
    val sungColorArgb: Long = 0xFF00FF00, // Green
    val unsungColorArgb: Long = 0xFFFFFFFF, // White
    val activeColorArgb: Long = 0xFFFFFF00, // Yellow
    val backgroundColorArgb: Long = 0xFF000000, // Black
    // Visual effects
    val gradientEnabled: Boolean = false,
    val gradientAngle: Float = 45f,
    val blurEnabled: Boolean = false,
    val blurIntensity: Float = 1f,
    // Character animations
    val charAnimEnabled: Boolean = false,
    val charMaxScale: Float = 1.2f,
    val charFloatOffset: Float = 8f,
    val charRotationDegrees: Float = 5f,
    // Line animations
    val lineAnimEnabled: Boolean = false,
    val lineScaleOnPlay: Float = 1.05f,
    // Pulse effect
    val pulseEnabled: Boolean = false,
    val pulseMinScale: Float = 0.95f,
    val pulseMaxScale: Float = 1.05f,
    // Layout
    val lineSpacing: Float = 80f,
    // Viewer type (12 types total)
    val viewerTypeIndex: Int = 0,
) {
    companion object {
        val Default = DemoSettings()
    }
}
