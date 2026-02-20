package com.kyrics.parser

/**
 * Interface for parsing lyrics files into [ParseResult].
 *
 * Implementations handle specific formats (e.g., TTML)
 * and convert them to a common [com.kyrics.models.KyricsLine] format.
 */
interface LyricsParser {
    /**
     * The format this parser handles.
     */
    val supportedFormat: LyricsFormat

    /**
     * Parses the given content string into lyrics.
     *
     * @param content The raw file content to parse
     * @return [ParseResult.Success] with parsed lines or [ParseResult.Failure] with error details
     */
    fun parse(content: String): ParseResult
}
