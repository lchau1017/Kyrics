package com.kyrics.parser

import com.kyrics.models.KyricsLine

/**
 * Result of parsing a lyrics file.
 */
sealed class ParseResult {
    /**
     * Successful parse result containing lyrics lines.
     *
     * @property lines Parsed lyrics lines with timing information
     * @property durationMs Total duration in milliseconds, parsed from the TTML body `dur` attribute. Null if not present.
     */
    data class Success(
        val lines: List<KyricsLine>,
        val durationMs: Long? = null,
    ) : ParseResult()

    /**
     * Failed parse result with error information.
     *
     * @property error Description of what went wrong
     * @property lineNumber Optional line number where the error occurred
     */
    data class Failure(
        val error: String,
        val lineNumber: Int? = null,
    ) : ParseResult()
}
