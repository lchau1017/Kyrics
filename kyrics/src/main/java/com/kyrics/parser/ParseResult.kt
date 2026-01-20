package com.kyrics.parser

import com.kyrics.models.KyricsLine

/**
 * Result of parsing a lyrics file.
 */
sealed class ParseResult {
    /**
     * Successful parse result containing lyrics lines and optional metadata.
     *
     * @property lines Parsed lyrics lines with timing information
     * @property metadata Optional metadata extracted from the file (title, artist, etc.)
     * @property warnings Optional warnings about the parsed content (e.g., format limitations)
     */
    data class Success(
        val lines: List<KyricsLine>,
        val metadata: LyricsMetadata = LyricsMetadata(),
        val warnings: List<String> = emptyList()
    ) : ParseResult() {
        /** Returns true if there are any warnings */
        val hasWarnings: Boolean get() = warnings.isNotEmpty()
    }

    /**
     * Failed parse result with error information.
     *
     * @property error Description of what went wrong
     * @property lineNumber Optional line number where the error occurred
     */
    data class Failure(
        val error: String,
        val lineNumber: Int? = null
    ) : ParseResult()
}

/**
 * Metadata extracted from lyrics files.
 *
 * @property title Song title
 * @property artist Artist name
 * @property album Album name
 * @property duration Song duration in milliseconds
 * @property offset Time offset in milliseconds (for LRC files)
 */
data class LyricsMetadata(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val duration: Int? = null,
    val offset: Int? = null
)
