package com.kyrics.demo.domain.model

import com.kyrics.config.ViewerType

/**
 * Domain model representing a viewer type option for the UI.
 */
data class ViewerTypeOption(
    val type: ViewerType,
    val displayName: String,
) {
    companion object {
        val all: List<ViewerTypeOption> =
            listOf(
                ViewerTypeOption(ViewerType.CENTER_FOCUSED, "Center"),
                ViewerTypeOption(ViewerType.SMOOTH_SCROLL, "Smooth"),
                ViewerTypeOption(ViewerType.STACKED, "Stacked"),
                ViewerTypeOption(ViewerType.HORIZONTAL_PAGED, "H-Paged"),
                ViewerTypeOption(ViewerType.WAVE_FLOW, "Wave"),
                ViewerTypeOption(ViewerType.SPIRAL, "Spiral"),
                ViewerTypeOption(ViewerType.CAROUSEL_3D, "3D-Carousel"),
                ViewerTypeOption(ViewerType.SPLIT_DUAL, "Split"),
                ViewerTypeOption(ViewerType.ELASTIC_BOUNCE, "Bounce"),
                ViewerTypeOption(ViewerType.FADE_THROUGH, "Fade"),
                ViewerTypeOption(ViewerType.RADIAL_BURST, "Burst"),
                ViewerTypeOption(ViewerType.FLIP_CARD, "Flip"),
            )
    }
}
