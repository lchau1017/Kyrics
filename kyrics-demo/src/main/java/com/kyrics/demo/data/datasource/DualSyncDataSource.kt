package com.kyrics.demo.data.datasource

import com.kyrics.models.KyricsLine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supported languages for the DualSync demo.
 */
enum class DemoLanguage(
    val displayName: String,
) {
    ENGLISH("English"),
    CHINESE("Chinese"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    FRENCH("French"),
    SPANISH("Spanish"),
    GERMAN("German"),
    PORTUGUESE("Portuguese"),
    ITALIAN("Italian"),
}

/**
 * Provides multi-language transcript data for the DualSync demo.
 * All tracks share the same 6-line timing (~30 seconds).
 */
@Singleton
class DualSyncDataSource
    @Inject
    constructor() {
        fun getTrack(language: DemoLanguage): List<KyricsLine> =
            when (language) {
                DemoLanguage.ENGLISH -> DemoLyricsEnglish.track()
                DemoLanguage.CHINESE -> DemoLyricsCjk.chineseTrack()
                DemoLanguage.JAPANESE -> DemoLyricsCjk.japaneseTrack()
                DemoLanguage.KOREAN -> DemoLyricsCjk.koreanTrack()
                DemoLanguage.FRENCH -> DemoLyricsEuropean.frenchTrack()
                DemoLanguage.SPANISH -> DemoLyricsEuropean.spanishTrack()
                DemoLanguage.GERMAN -> DemoLyricsEuropean.germanTrack()
                DemoLanguage.PORTUGUESE -> DemoLyricsEuropean.portugueseTrack()
                DemoLanguage.ITALIAN -> DemoLyricsEuropean.italianTrack()
            }

        fun getTotalDurationMs(): Long = TOTAL_DURATION_MS

        companion object {
            const val TOTAL_DURATION_MS = 31_000L
        }
    }
