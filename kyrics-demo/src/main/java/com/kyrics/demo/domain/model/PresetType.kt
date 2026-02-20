package com.kyrics.demo.domain.model

/**
 * Pure domain representation of available presets.
 * Maps to library presets via presentation layer mapper.
 */
enum class PresetType(
    val displayName: String,
) {
    CLASSIC("Classic"),
    NEON("Neon"),
}
