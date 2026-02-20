package com.kyrics.demo.domain.repository

import com.kyrics.models.KyricsLine

/**
 * Repository interface for providing lyrics data.
 * This abstraction allows the domain layer to access lyrics data
 * without knowing about the data source implementation details.
 */
interface LyricsRepository {
    /**
     * Get the lyrics to display.
     */
    suspend fun getLyrics(): List<KyricsLine>

    /**
     * Get the total duration of the lyrics in milliseconds.
     */
    fun getTotalDurationMs(): Long
}
