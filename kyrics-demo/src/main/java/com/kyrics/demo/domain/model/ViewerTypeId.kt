package com.kyrics.demo.domain.model

/**
 * Pure domain model representing viewer type options.
 * Maps to library ViewerType via presentation layer mapper.
 */
enum class ViewerTypeId(
    val displayName: String,
) {
    CENTER_FOCUSED("Center"),
    SMOOTH_SCROLL("Smooth"),
    STACKED("Stacked"),
    HORIZONTAL_PAGED("H-Paged"),
    WAVE_FLOW("Wave"),
    SPIRAL("Spiral"),
    CAROUSEL_3D("3D-Carousel"),
    SPLIT_DUAL("Split"),
    ELASTIC_BOUNCE("Bounce"),
    FADE_THROUGH("Fade"),
    RADIAL_BURST("Burst"),
    FLIP_CARD("Flip"),
}
