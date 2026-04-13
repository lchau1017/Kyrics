package com.kyrics.demo.data.datasource

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

internal object DemoLyricsEnglish {
    fun track(): List<KyricsLine> = firstHalf() + secondHalf()

    private fun firstHalf(): List<KyricsLine> =
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

    private fun secondHalf(): List<KyricsLine> =
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
}
