package com.kyrics.demo.data.datasource

import com.kyrics.demo.domain.datasource.LyricsDataSource
import com.kyrics.models.KyricsLine
import com.kyrics.models.kyricsLyrics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides demo lyrics for testing and showcasing the library features.
 * Uses the Kyrics DSL for clean, readable lyrics definition.
 */
@Singleton
class DemoLyricsDataSource
    @Inject
    constructor() : LyricsDataSource {
        @Suppress("LongMethod")
        override fun getLyrics(): List<KyricsLine> =
            kyricsLyrics {
                // First verse
                line(start = 0, end = 2000) {
                    syllable("When ", start = 0, end = 200)
                    syllable("the ", start = 200, end = 400)
                    syllable("sun ", start = 400, end = 800)
                    syllable("goes ", start = 800, end = 1200)
                    syllable("down", start = 1200, end = 2000)
                }
                line(start = 2000, end = 4000) {
                    syllable("And ", start = 2000, end = 2200)
                    syllable("the ", start = 2200, end = 2400)
                    syllable("stars ", start = 2400, end = 2800)
                    syllable("come ", start = 2800, end = 3200)
                    syllable("out", start = 3200, end = 4000)
                }
                line(start = 4000, end = 6000) {
                    syllable("I'll ", start = 4000, end = 4200)
                    syllable("be ", start = 4200, end = 4400)
                    syllable("dream", start = 4400, end = 4800)
                    syllable("ing ", start = 4800, end = 5200)
                    syllable("of ", start = 5200, end = 5400)
                    syllable("you", start = 5400, end = 6000)
                }

                // Chorus
                line(start = 6500, end = 8500) {
                    syllable("Dance ", start = 6500, end = 6900)
                    syllable("with ", start = 6900, end = 7100)
                    syllable("me ", start = 7100, end = 7500)
                    syllable("to", start = 7500, end = 7700)
                    syllable("night", start = 7700, end = 8500)
                }
                line(start = 8_500, end = 10_500) {
                    syllable("Un", start = 8_500, end = 8_700)
                    syllable("der ", start = 8_700, end = 8_900)
                    syllable("the ", start = 8_900, end = 9_100)
                    syllable("moon", start = 9_100, end = 9_500)
                    syllable("light", start = 9_500, end = 10_500)
                }
                line(start = 10_500, end = 12_500) {
                    syllable("Hold ", start = 10_500, end = 10_900)
                    syllable("me ", start = 10_900, end = 11_300)
                    syllable("close", start = 11_300, end = 11_700)
                    syllable("ly", start = 11_700, end = 12_500)
                }

                // Bridge
                line(start = 13_000, end = 15_500) {
                    syllable("Eve", start = 13_000, end = 13_200)
                    syllable("ry ", start = 13_200, end = 13_400)
                    syllable("mo", start = 13_400, end = 13_600)
                    syllable("ment ", start = 13_600, end = 14_000)
                    syllable("feels ", start = 14_000, end = 14_400)
                    syllable("so ", start = 14_400, end = 14_600)
                    syllable("right", start = 14_600, end = 15_500)
                }
                line(start = 15_500, end = 17_500) {
                    syllable("With ", start = 15_500, end = 15_700)
                    syllable("you ", start = 15_700, end = 16_100)
                    syllable("by ", start = 16_100, end = 16_300)
                    syllable("my ", start = 16_300, end = 16_500)
                    syllable("side", start = 16_500, end = 17_500)
                }

                // Outro
                line(start = 18_000, end = 20_000) {
                    syllable("For", start = 18_000, end = 18_200)
                    syllable("ev", start = 18_200, end = 18_400)
                    syllable("er ", start = 18_400, end = 18_800)
                    syllable("and ", start = 18_800, end = 19_000)
                    syllable("al", start = 19_000, end = 19_200)
                    syllable("ways", start = 19_200, end = 20_000)
                }
            }

        override fun getTotalDurationMs(): Long = TOTAL_DURATION_MS

        companion object {
            const val TOTAL_DURATION_MS = 20_000L
        }
    }
