package com.kyrics.testdata

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Test data fixtures for unit tests.
 * Provides consistent test data across all tests.
 */
object TestData {
    /**
     * Creates a list of simple lines for testing.
     * Each line is a single syllable with 2 seconds duration and 500ms gaps.
     */
    fun createSimpleLines(): List<KyricsLine> =
        listOf(
            KyricsLine(listOf(KyricsSyllable("Hello world", 0, 2000)), 0, 2000),
            KyricsLine(listOf(KyricsSyllable("This is a test", 2500, 4500)), 2500, 4500),
            KyricsLine(listOf(KyricsSyllable("Karaoke lyrics", 5000, 7000)), 5000, 7000),
            KyricsLine(listOf(KyricsSyllable("Are fun to sing", 7500, 9500)), 7500, 9500),
            KyricsLine(listOf(KyricsSyllable("The end", 10_000, 12_000)), 10_000, 12_000),
        )

    /**
     * Creates a KyricsLine with syllable-level timing.
     *
     * "Hel-lo World" with timing:
     * - "Hel" : 0ms - 300ms
     * - "lo " : 300ms - 600ms
     * - "World" : 600ms - 1000ms
     */
    fun createKyricsLineWithSyllables(): KyricsLine =
        KyricsLine(
            syllables =
                listOf(
                    KyricsSyllable(content = "Hel", start = 0, end = 300),
                    KyricsSyllable(content = "lo ", start = 300, end = 600),
                    KyricsSyllable(content = "World", start = 600, end = 1000),
                ),
            start = 0,
            end = 1000,
        )

    /**
     * Creates a list of KyricsLines with syllable timing for testing.
     */
    fun createKyricsLines(): List<KyricsLine> =
        listOf(
            KyricsLine(
                syllables =
                    listOf(
                        KyricsSyllable("Hel", 0, 300),
                        KyricsSyllable("lo ", 300, 600),
                        KyricsSyllable("World", 600, 1000),
                    ),
                start = 0,
                end = 1000,
            ),
            KyricsLine(
                syllables =
                    listOf(
                        KyricsSyllable("Test", 1500, 1800),
                        KyricsSyllable("ing", 1800, 2000),
                    ),
                start = 1500,
                end = 2000,
            ),
            KyricsLine(
                syllables =
                    listOf(
                        KyricsSyllable("Ka", 2500, 2700),
                        KyricsSyllable("ra", 2700, 2900),
                        KyricsSyllable("o", 2900, 3000),
                        KyricsSyllable("ke", 3000, 3200),
                    ),
                start = 2500,
                end = 3200,
            ),
        )

    /**
     * Creates an accompaniment line (background vocals).
     */
    fun createAccompanimentLine(): KyricsLine =
        KyricsLine(
            syllables =
                listOf(
                    KyricsSyllable("(ooh)", 0, 500),
                ),
            start = 0,
            end = 500,
            isAccompaniment = true,
        )

    /**
     * Test time points for various scenarios
     */
    object TimePoints {
        // For createSimpleLines()
        const val BEFORE_ALL_LINES = -500 // Before any line starts
        const val LINE_0_PLAYING = 1000 // Middle of line 0
        const val BETWEEN_LINE_0_AND_1 = 2250 // Gap between lines
        const val LINE_1_PLAYING = 3500 // Middle of line 1
        const val LINE_2_START = 5000 // Exactly at line 2 start
        const val LINE_2_END = 7000 // Exactly at line 2 end
        const val LINE_4_PLAYING = 11_000 // Last line playing
        const val AFTER_ALL_LINES = 15_000 // After all lines
    }
}
