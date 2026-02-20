package com.kyrics

import com.google.common.truth.Truth.assertThat
import com.kyrics.config.ViewerType
import com.kyrics.config.kyricsConfig
import com.kyrics.dsl.KyricsLineFactory
import com.kyrics.dsl.kyricsAccompaniment
import com.kyrics.dsl.kyricsLine
import com.kyrics.dsl.kyricsLineFromText
import com.kyrics.dsl.kyricsLineFromWords
import com.kyrics.dsl.kyricsLyrics
import com.kyrics.models.KyricsLine
import org.junit.Test

/**
 * Tests for the Kyrics public API.
 *
 * Verifies DSL functions, configuration, presets, and factory functions
 * work correctly for library consumers.
 */
class KyricsPublicApiTest {
    // ==================== DSL Function Tests ====================

    @Test
    fun `kyricsConfig DSL function creates valid config`() {
        val config =
            kyricsConfig {
                colors {
                    playing = androidx.compose.ui.graphics.Color.Yellow
                }
            }

        assertThat(config).isNotNull()
        assertThat(config.visual.playingTextColor).isEqualTo(androidx.compose.ui.graphics.Color.Yellow)
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

    @Test
    fun `KyricsLineFactory creates lines`() {
        val line = KyricsLineFactory.fromText("Test", 0, 1000)
        assertThat(line).isNotNull()
    }

    // ==================== Integration Tests ====================

    @Test
    fun `can create complete lyrics`() {
        val config =
            kyricsConfig {
                colors {
                    playing = androidx.compose.ui.graphics.Color.Cyan
                    played = androidx.compose.ui.graphics.Color.Gray
                }
                viewer {
                    type = ViewerType.FADE_THROUGH
                }
            }

        val lyrics: List<KyricsLine> =
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

        assertThat(config.layout.viewerConfig.type).isEqualTo(ViewerType.FADE_THROUGH)
        assertThat(lyrics).hasSize(3)
        assertThat(lyrics[0].getContent()).isEqualTo("When the sun goes down")
        assertThat(lyrics[1].getContent()).isEqualTo("And the stars come out")
        assertThat(lyrics[2].isAccompaniment).isTrue()
    }

    @Test
    fun `KyricsPresets are accessible`() {
        assertThat(KyricsPresets.Classic).isNotNull()
        assertThat(KyricsPresets.Neon).isNotNull()
        assertThat(KyricsPresets.Minimal).isNotNull()
        assertThat(KyricsPresets.all).hasSize(3)
    }

    @Test
    fun `all ViewerType values are accessible`() {
        val viewerTypes =
            listOf(
                ViewerType.CENTER_FOCUSED,
                ViewerType.SMOOTH_SCROLL,
                ViewerType.FADE_THROUGH,
            )

        assertThat(viewerTypes).hasSize(3)
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
