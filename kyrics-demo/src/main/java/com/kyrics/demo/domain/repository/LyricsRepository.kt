package com.kyrics.demo.domain.repository

import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.models.KyricsLine

/**
 * Repository interface for providing lyrics data.
 * This abstraction allows the domain layer to access lyrics data
 * without knowing about the data source implementation details.
 */
interface LyricsRepository {
    /**
     * Get the lyrics to display for the given source.
     */
    suspend fun getLyrics(source: LyricsSource): List<KyricsLine>

    /**
     * Get the total duration of the lyrics in milliseconds.
     */
    fun getTotalDurationMs(): Long
}
