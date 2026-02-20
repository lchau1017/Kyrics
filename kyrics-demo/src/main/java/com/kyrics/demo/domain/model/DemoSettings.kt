package com.kyrics.demo.domain.model

/**
 * Pure domain model for demo settings.
 * No framework dependencies - uses primitive types only.
 */
data class DemoSettings(
    // Text settings
    val fontSize: Float = 20f,
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
    val blurEnabled: Boolean = true,
    // Layout
    val lineSpacing: Float = 0f,
    // Viewer type
    val viewerTypeIndex: Int = 0,
) {
    companion object {
        val Default = DemoSettings()
    }
}
