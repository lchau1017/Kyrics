package com.kyrics.config

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for KyricsPresets.
 * Verifies all preset configurations are valid and consistent.
 */
class KyricsPresetsTest {
    // ==================== Preset Existence Tests ====================

    @Test
    fun `Classic preset exists and is valid`() {
        val preset = KyricsPresets.Classic

        assertThat(preset).isNotNull()
        assertThat(preset.visual).isNotNull()
    }

    @Test
    fun `Neon preset exists and is valid`() {
        val preset = KyricsPresets.Neon

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    @Test
    fun `Minimal preset exists and is valid`() {
        val preset = KyricsPresets.Minimal

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isFalse()
    }

    // ==================== allPresets Tests ====================

    @Test
    fun `allPresets contains all 3 presets`() {
        val presets = KyricsPresets.allPresets

        assertThat(presets).hasSize(3)
    }

    @Test
    fun `allPresets has correct names`() {
        val names = KyricsPresets.allPresets.map { it.first }

        assertThat(names).containsExactly(
            "Classic",
            "Neon",
            "Minimal",
        )
    }

    @Test
    fun `allPresets configs match individual presets`() {
        val presetsMap = KyricsPresets.allPresets.toMap()

        assertThat(presetsMap["Classic"]).isEqualTo(KyricsPresets.Classic)
        assertThat(presetsMap["Neon"]).isEqualTo(KyricsPresets.Neon)
        assertThat(presetsMap["Minimal"]).isEqualTo(KyricsPresets.Minimal)
    }

    // ==================== Preset Properties Tests ====================

    @Test
    fun `Classic preset has no gradient`() {
        val preset = KyricsPresets.Classic

        assertThat(preset.visual.gradientEnabled).isFalse()
    }

    @Test
    fun `Neon preset has gradient enabled`() {
        val preset = KyricsPresets.Neon

        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    // ==================== Preset Consistency Tests ====================

    @Test
    fun `all presets have valid visual config`() {
        KyricsPresets.allPresets.forEach { (_, preset) ->
            assertThat(preset.visual.fontSize.value).isGreaterThan(0f)
            assertThat(preset.visual.fontWeight).isNotNull()
        }
    }

    @Test
    fun `presets with gradients have color config`() {
        KyricsPresets.allPresets.forEach { (_, preset) ->
            if (preset.visual.gradientEnabled) {
                assertThat(preset.visual.colors).isNotNull()
            }
        }
    }
}
