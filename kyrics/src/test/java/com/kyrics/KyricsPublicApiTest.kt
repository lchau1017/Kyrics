package com.kyrics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests for the Kyrics public API.
 *
 * These tests verify that all DSL functions and type aliases are properly
 * exported from the `com.kyrics` package, allowing client apps to use the
 * library with a single import.
 *
 * This ensures that the public API contract is maintained and that client
 * apps don't need to import from internal packages like `com.kyrics.models`
 * or `com.kyrics.config`.
 */
class KyricsPublicApiTest {
    // ==================== Type Alias Tests ====================

    @Test
    fun `SyncedLine type alias is accessible`() {
        // Verify type alias works - create a KyricsLine which implements SyncedLine
        val line: SyncedLine = kyricsLineFromText("Test", 0, 1000)
        assertThat(line).isNotNull()
        assertThat(line.start).isEqualTo(0)
        assertThat(line.end).isEqualTo(1000)
    }

    @Test
    fun `KyricsLine type alias is accessible`() {
        val line: KyricsLine = kyricsLineFromText("Test", 0, 1000)
        assertThat(line).isNotNull()
        assertThat(line.syllables).hasSize(1)
    }

    @Test
    fun `KyricsSyllable type alias is accessible`() {
        val line = kyricsLineFromText("Test", 0, 1000)
        val syllable: KyricsSyllable = line.syllables.first()
        assertThat(syllable.content).isEqualTo("Test")
        assertThat(syllable.start).isEqualTo(0)
        assertThat(syllable.end).isEqualTo(1000)
    }

    @Test
    fun `KyricsConfig type alias is accessible`() {
        val config: KyricsConfig = KyricsConfig.Default
        assertThat(config).isNotNull()
    }

    @Test
    fun `ViewerType type alias is accessible`() {
        val viewerType: ViewerType = ViewerType.SMOOTH_SCROLL
        assertThat(viewerType).isEqualTo(ViewerType.SMOOTH_SCROLL)
    }

    @Test
    fun `KyricsLineFactory type alias is accessible`() {
        val line = KyricsLineFactory.fromText("Test", 0, 1000)
        assertThat(line).isNotNull()
    }

    // ==================== DSL Function Tests ====================

    @Test
    fun `kyricsConfig DSL function creates valid config`() {
        val config =
            kyricsConfig {
                colors {
                    playing = androidx.compose.ui.graphics.Color.Yellow
                }
                animations {
                    characterAnimations = true
                    characterScale = 1.5f
                }
            }

        assertThat(config).isNotNull()
        assertThat(config.visual.playingTextColor).isEqualTo(androidx.compose.ui.graphics.Color.Yellow)
        assertThat(config.animation.enableCharacterAnimations).isTrue()
        assertThat(config.animation.characterMaxScale).isEqualTo(1.5f)
    }

    @Test
    fun `kyricsLine DSL function creates valid line`() {
        val line =
            kyricsLine(start = 0, end = 2000) {
                syllable("Hel", duration = 300)
                syllable("lo ", duration = 300)
                syllable("World", duration = 400)
            }

        assertThat(line.start).isEqualTo(0)
        assertThat(line.end).isEqualTo(2000)
        assertThat(line.syllables).hasSize(3)
        assertThat(line.getContent()).isEqualTo("Hello World")
    }

    @Test
    fun `kyricsLine with explicit timing creates valid syllables`() {
        val line =
            kyricsLine(start = 1000, end = 3000) {
                syllable("First", start = 1000, end = 1500)
                syllable(" ", start = 1500, end = 1600)
                syllable("Second", start = 1600, end = 2500)
            }

        assertThat(line.syllables).hasSize(3)
        assertThat(line.syllables[0].start).isEqualTo(1000)
        assertThat(line.syllables[0].end).isEqualTo(1500)
        assertThat(line.syllables[2].start).isEqualTo(1600)
        assertThat(line.syllables[2].end).isEqualTo(2500)
    }

    @Test
    fun `kyricsLyrics DSL function creates valid lyrics list`() {
        val lyrics =
            kyricsLyrics {
                line(start = 0, end = 2000) {
                    syllable("First ", duration = 500)
                    syllable("line", duration = 500)
                }
                line(start = 2500, end = 4500) {
                    syllable("Second ", duration = 600)
                    syllable("line", duration = 400)
                }
            }

        assertThat(lyrics).hasSize(2)
        assertThat(lyrics[0].getContent()).isEqualTo("First line")
        assertThat(lyrics[1].getContent()).isEqualTo("Second line")
    }

    @Test
    fun `kyricsLyrics with accompaniment creates valid list`() {
        val lyrics =
            kyricsLyrics {
                line(start = 0, end = 2000) {
                    syllable("Main vocal", duration = 1000)
                }
                accompaniment(start = 2000, end = 3000) {
                    syllable("(ooh)", duration = 500)
                }
            }

        assertThat(lyrics).hasSize(2)
        assertThat(lyrics[0].isAccompaniment).isFalse()
        assertThat(lyrics[1].isAccompaniment).isTrue()
    }

    @Test
    fun `kyricsLineFromText creates single syllable line`() {
        val line = kyricsLineFromText("Hello World", start = 0, end = 1000)

        assertThat(line.syllables).hasSize(1)
        assertThat(line.syllables[0].content).isEqualTo("Hello World")
        assertThat(line.start).isEqualTo(0)
        assertThat(line.end).isEqualTo(1000)
    }

    @Test
    fun `kyricsLineFromWords splits on whitespace`() {
        val line = kyricsLineFromWords("Hello World Test", start = 0, end = 3000)

        assertThat(line.syllables).hasSize(3)
        assertThat(line.syllables[0].content).isEqualTo("Hello ")
        assertThat(line.syllables[1].content).isEqualTo("World ")
        assertThat(line.syllables[2].content).isEqualTo("Test")
    }

    @Test
    fun `kyricsLineFromWords distributes timing evenly`() {
        val line = kyricsLineFromWords("A B C", start = 0, end = 3000)

        // 3 words, 3000ms total = 1000ms each
        assertThat(line.syllables[0].start).isEqualTo(0)
        assertThat(line.syllables[0].end).isEqualTo(1000)
        assertThat(line.syllables[1].start).isEqualTo(1000)
        assertThat(line.syllables[1].end).isEqualTo(2000)
        assertThat(line.syllables[2].start).isEqualTo(2000)
        assertThat(line.syllables[2].end).isEqualTo(3000)
    }

    @Test
    fun `kyricsAccompaniment creates accompaniment line`() {
        val line = kyricsAccompaniment("(background)", start = 0, end = 1000)

        assertThat(line.isAccompaniment).isTrue()
        assertThat(line.getContent()).isEqualTo("(background)")
    }

    // ==================== Integration Tests ====================

    @Test
    fun `can create complete lyrics using only com_kyrics imports`() {
        // This test verifies that a complete use case works with only com.kyrics imports
        val config =
            kyricsConfig {
                colors {
                    playing = androidx.compose.ui.graphics.Color.Cyan
                    played = androidx.compose.ui.graphics.Color.Gray
                }
                viewer {
                    type = ViewerType.CAROUSEL_3D
                }
            }

        val lyrics: List<SyncedLine> =
            kyricsLyrics {
                line(start = 0, end = 2000) {
                    syllable("When ", duration = 200)
                    syllable("the ", duration = 200)
                    syllable("sun ", duration = 400)
                    syllable("goes ", duration = 400)
                    syllable("down", duration = 800)
                }
                line(start = 2500, end = 4500) {
                    syllable("And ", duration = 200)
                    syllable("the ", duration = 200)
                    syllable("stars ", duration = 400)
                    syllable("come ", duration = 400)
                    syllable("out", duration = 800)
                }
                accompaniment(start = 4500, end = 5500) {
                    syllable("(ooh ooh)", duration = 1000)
                }
            }

        assertThat(config.layout.viewerConfig.type).isEqualTo(ViewerType.CAROUSEL_3D)
        assertThat(lyrics).hasSize(3)
        assertThat(lyrics[0].getContent()).isEqualTo("When the sun goes down")
        assertThat(lyrics[1].getContent()).isEqualTo("And the stars come out")
        assertThat((lyrics[2] as KyricsLine).isAccompaniment).isTrue()
    }

    @Test
    fun `KyricsPresets are accessible`() {
        assertThat(KyricsPresets.Classic).isNotNull()
        assertThat(KyricsPresets.Neon).isNotNull()
        assertThat(KyricsPresets.Rainbow).isNotNull()
        assertThat(KyricsPresets.Fire).isNotNull()
        assertThat(KyricsPresets.Ocean).isNotNull()
        assertThat(KyricsPresets.Retro).isNotNull()
        assertThat(KyricsPresets.Minimal).isNotNull()
        assertThat(KyricsPresets.Elegant).isNotNull()
        assertThat(KyricsPresets.Party).isNotNull()
        assertThat(KyricsPresets.Matrix).isNotNull()
        assertThat(KyricsPresets.all).isNotEmpty()
    }

    @Test
    fun `all ViewerType values are accessible`() {
        val viewerTypes =
            listOf(
                ViewerType.CENTER_FOCUSED,
                ViewerType.SMOOTH_SCROLL,
                ViewerType.STACKED,
                ViewerType.HORIZONTAL_PAGED,
                ViewerType.WAVE_FLOW,
                ViewerType.SPIRAL,
                ViewerType.CAROUSEL_3D,
                ViewerType.SPLIT_DUAL,
                ViewerType.ELASTIC_BOUNCE,
                ViewerType.FADE_THROUGH,
                ViewerType.RADIAL_BURST,
                ViewerType.FLIP_CARD,
            )

        assertThat(viewerTypes).hasSize(12)
        viewerTypes.forEach { type ->
            assertThat(type).isNotNull()
        }
    }

    // ==================== Edge Cases ====================

    @Test
    fun `kyricsLineFromWords handles empty string`() {
        val line = kyricsLineFromWords("", start = 0, end = 1000)
        assertThat(line.syllables).isEmpty()
    }

    @Test
    fun `kyricsLineFromWords handles single word`() {
        val line = kyricsLineFromWords("Hello", start = 0, end = 1000)
        assertThat(line.syllables).hasSize(1)
        assertThat(line.syllables[0].content).isEqualTo("Hello")
    }

    @Test
    fun `kyricsLyrics handles empty builder`() {
        val lyrics = kyricsLyrics { }
        assertThat(lyrics).isEmpty()
    }

    @Test
    fun `kyricsLine handles empty syllables`() {
        val line = kyricsLine(start = 0, end = 1000) { }
        assertThat(line.syllables).isEmpty()
        assertThat(line.getContent()).isEmpty()
    }
}
