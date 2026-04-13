package com.kyrics.demo.data.datasource

import com.kyrics.dualsync.model.DualTrackLyrics
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides hardcoded dual-language transcript data for the DualSync demo.
 * English primary with word-level timestamps, Chinese secondary with line-level timing.
 * Total duration: ~30 seconds.
 */
@Singleton
class DualSyncDataSource
    @Inject
    constructor() {
        fun getDualTrackLyrics(): DualTrackLyrics =
            DualTrackLyrics(
                primary = englishTrack(),
                secondary = chineseTrack(),
            )

        fun getTotalDurationMs(): Long = TOTAL_DURATION_MS

        private fun englishTrack(): List<KyricsLine> = englishFirstHalf() + englishSecondHalf()

        private fun englishFirstHalf(): List<KyricsLine> =
            listOf(
                KyricsLine(
                    syllables =
                        listOf(
                            KyricsSyllable("The ", 0, 800),
                            KyricsSyllable("sun ", 800, 1600),
                            KyricsSyllable("is ", 1600, 2200),
                            KyricsSyllable("setting ", 2200, 3500),
                            KyricsSyllable("slowly", 3500, 5000),
                        ),
                    start = 0,
                    end = 5000,
                ),
                KyricsLine(
                    syllables =
                        listOf(
                            KyricsSyllable("Colors ", 5500, 6800),
                            KyricsSyllable("paint ", 6800, 8000),
                            KyricsSyllable("the ", 8000, 8600),
                            KyricsSyllable("sky", 8600, 10_000),
                        ),
                    start = 5500,
                    end = 10_000,
                ),
                KyricsLine(
                    syllables =
                        listOf(
                            KyricsSyllable("Golden ", 10_500, 12_000),
                            KyricsSyllable("light ", 12_000, 13_200),
                            KyricsSyllable("across ", 13_200, 14_500),
                            KyricsSyllable("the ", 14_500, 15_000),
                            KyricsSyllable("water", 15_000, 16_000),
                        ),
                    start = 10_500,
                    end = 16_000,
                ),
            )

        private fun englishSecondHalf(): List<KyricsLine> =
            listOf(
                KyricsLine(
                    syllables =
                        listOf(
                            KyricsSyllable("Birds ", 16_500, 17_800),
                            KyricsSyllable("fly ", 17_800, 18_800),
                            KyricsSyllable("home ", 18_800, 19_800),
                            KyricsSyllable("tonight", 19_800, 21_000),
                        ),
                    start = 16_500,
                    end = 21_000,
                ),
                KyricsLine(
                    syllables =
                        listOf(
                            KyricsSyllable("Stars ", 21_500, 22_800),
                            KyricsSyllable("will ", 22_800, 23_500),
                            KyricsSyllable("shine ", 23_500, 24_800),
                            KyricsSyllable("so ", 24_800, 25_300),
                            KyricsSyllable("bright", 25_300, 26_000),
                        ),
                    start = 21_500,
                    end = 26_000,
                ),
                KyricsLine(
                    syllables =
                        listOf(
                            KyricsSyllable("Until ", 26_500, 27_800),
                            KyricsSyllable("the ", 27_800, 28_300),
                            KyricsSyllable("morning ", 28_300, 29_300),
                            KyricsSyllable("light", 29_300, 30_000),
                        ),
                    start = 26_500,
                    end = 30_000,
                ),
            )

        @Suppress("MaxLineLength")
        private fun chineseTrack(): List<KyricsLine> =
            listOf(
                // Line 1: matching English line 1 timing
                KyricsLine(
                    syllables = listOf(KyricsSyllable("\u592A\u9633\u6B63\u5728\u6162\u6162\u843D\u4E0B", 0, 5000)),
                    start = 0,
                    end = 5000,
                ),
                // Line 2
                KyricsLine(
                    syllables = listOf(KyricsSyllable("\u8272\u5F69\u6E32\u67D3\u4E86\u5929\u7A7A", 5500, 10_000)),
                    start = 5500,
                    end = 10_000,
                ),
                // Line 3
                KyricsLine(
                    syllables = listOf(KyricsSyllable("\u91D1\u8272\u7684\u5149\u6D12\u8FC7\u6C34\u9762", 10_500, 16_000)),
                    start = 10_500,
                    end = 16_000,
                ),
                // Line 4
                KyricsLine(
                    syllables = listOf(KyricsSyllable("\u9E1F\u513F\u4ECA\u665A\u98DE\u56DE\u5BB6", 16_500, 21_000)),
                    start = 16_500,
                    end = 21_000,
                ),
                // Line 5
                KyricsLine(
                    syllables = listOf(KyricsSyllable("\u661F\u661F\u5C06\u4F1A\u95EA\u8000\u5149\u8292", 21_500, 26_000)),
                    start = 21_500,
                    end = 26_000,
                ),
                // Line 6
                KyricsLine(
                    syllables = listOf(KyricsSyllable("\u76F4\u5230\u6668\u5149\u964D\u4E34", 26_500, 30_000)),
                    start = 26_500,
                    end = 30_000,
                ),
            )

        companion object {
            const val TOTAL_DURATION_MS = 31_000L
        }
    }
