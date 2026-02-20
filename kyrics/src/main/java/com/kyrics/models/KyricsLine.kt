package com.kyrics.models

/**
 * Implementation of SyncedLine for karaoke content with syllable-level timing.
 */
data class KyricsLine(
    val syllables: List<KyricsSyllable>,
    override val start: Int,
    override val end: Int,
    val isAccompaniment: Boolean = false,
    val alignment: String = "center",
) : SyncedLine {
    override fun getContent(): String = syllables.joinToString("") { it.content }
}
