package com.kyrics.demo.domain.datasource

import com.kyrics.models.KyricsLine

/**
 * Interface for providing lyrics data.
 * This abstraction allows for easy testing and swapping implementations.
 */
interface LyricsDataSource {
    /**
     * Get the lyrics to display.
     */
    fun getLyrics(): List<KyricsLine>

    /**
     * Get the total duration of the lyrics in milliseconds.
     */
    fun getTotalDurationMs(): Long
}
