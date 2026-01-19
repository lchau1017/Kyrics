package com.kyrics.demo.domain.model

import com.kyrics.models.KyricsLine

/**
 * Domain model representing lyrics data.
 */
data class LyricsData(
    val lines: List<KyricsLine>,
    val totalDurationMs: Long,
)
