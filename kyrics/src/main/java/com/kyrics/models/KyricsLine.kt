package com.kyrics.models

/**
 * A karaoke line with syllable-level timing for synchronized highlighting.
 */
data class KyricsLine(
    val syllables: List<KyricsSyllable>,
    val start: Int,
    val end: Int,
    val isAccompaniment: Boolean = false,
    val alignment: String = "center",
) {
    fun getContent(): String = syllables.joinToString("") { it.content }
}
