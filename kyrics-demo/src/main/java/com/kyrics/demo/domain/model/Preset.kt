package com.kyrics.demo.domain.model

import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsPresets

/**
 * Type-safe representation of available presets.
 * Each preset maps to a [KyricsConfig] from the library.
 */
sealed class Preset(
    val displayName: String,
    val config: KyricsConfig,
) {
    data object Classic : Preset("Classic", KyricsPresets.Classic)

    data object Neon : Preset("Neon", KyricsPresets.Neon)

    data object Minimal : Preset("Minimal", KyricsPresets.Minimal)

    data object Rainbow : Preset("Rainbow", KyricsPresets.Rainbow)

    data object Fire : Preset("Fire", KyricsPresets.Fire)

    data object Ocean : Preset("Ocean", KyricsPresets.Ocean)

    data object Retro : Preset("Retro", KyricsPresets.Retro)

    data object Elegant : Preset("Elegant", KyricsPresets.Elegant)

    data object Party : Preset("Party", KyricsPresets.Party)

    data object Matrix : Preset("Matrix", KyricsPresets.Matrix)

    companion object {
        /**
         * All available presets in display order.
         */
        val all: List<Preset> =
            listOf(
                Classic,
                Neon,
                Minimal,
                Rainbow,
                Fire,
                Ocean,
                Retro,
                Elegant,
                Party,
                Matrix,
            )
    }
}
