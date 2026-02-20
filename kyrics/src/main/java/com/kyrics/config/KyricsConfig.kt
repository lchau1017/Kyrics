package com.kyrics.config

/**
 * Complete configuration for the Karaoke UI Library.
 * This is completely independent from any app user settings.
 * The app is responsible for mapping its user settings to this configuration.
 */
data class KyricsConfig(
    val visual: VisualConfig = VisualConfig(),
    val animation: AnimationConfig = AnimationConfig(),
    val layout: LayoutConfig = LayoutConfig(),
    val effects: EffectsConfig = EffectsConfig(),
) {
    companion object {
        /**
         * Default configuration with balanced settings
         */
        val Default = KyricsConfig()
    }
}
