package com.kyrics.dualsync.model

import com.kyrics.models.KyricsLine

/**
 * Two independent timed transcripts mapped to the same audio playback position.
 * Each track is independently timed — they are NOT word-mapped to each other.
 *
 * @param primary First language track (e.g. English)
 * @param secondary Second language track (e.g. Chinese)
 */
data class DualTrackLyrics(
    val primary: List<KyricsLine>,
    val secondary: List<KyricsLine>,
)
