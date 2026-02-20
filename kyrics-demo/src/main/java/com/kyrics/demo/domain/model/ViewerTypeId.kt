package com.kyrics.demo.domain.model

/**
 * Pure domain model representing viewer type options.
 * Maps to library ViewerType via presentation layer mapper.
 */
enum class ViewerTypeId(
    val displayName: String,
) {
    SMOOTH_SCROLL("Smooth"),
    FADE_THROUGH("Fade"),
}
