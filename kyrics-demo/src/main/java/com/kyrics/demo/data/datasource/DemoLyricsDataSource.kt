package com.kyrics.demo.data.datasource

import android.content.Context
import android.util.Log
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.models.KyricsLine
import com.kyrics.parseLyrics
import com.kyrics.parser.ParseResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source that provides demo lyrics by loading from asset files
 * and parsing with Kyrics library. Supports TTML and LRC formats.
 *
 * This is an internal data layer implementation. The domain layer
 * should access lyrics through [com.kyrics.demo.domain.repository.LyricsRepository].
 */
@Singleton
class DemoLyricsDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        suspend fun getLyrics(source: LyricsSource): List<KyricsLine> {
            val fileName = "golden-hour.${source.extension}"
            return try {
                val content =
                    context.assets
                        .open(fileName)
                        .bufferedReader()
                        .use { it.readText() }
                when (val result = parseLyrics(content)) {
                    is ParseResult.Success -> {
                        result.lines
                    }
                    is ParseResult.Failure -> {
                        Log.w(TAG, "Failed to parse lyrics: ${result.error}")
                        emptyList()
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to load lyrics file: $fileName", e)
                emptyList()
            }
        }

        fun getTotalDurationMs(): Long = TOTAL_DURATION_MS

        companion object {
            private const val TAG = "DemoLyricsDataSource"

            // Golden hour duration: 2:52 = 172 seconds = 172,000 ms
            const val TOTAL_DURATION_MS = 172_830L
        }
    }
