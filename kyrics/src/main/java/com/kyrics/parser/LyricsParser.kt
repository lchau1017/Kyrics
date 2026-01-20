package com.kyrics.parser

/**
 * Interface for parsing lyrics files into [ParseResult].
 *
 * Implementations should handle specific formats (TTML, LRC, etc.)
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

    /**
     * Checks if this parser can handle the given content.
     *
     * @param content The raw file content to check
     * @return true if this parser can parse the content
     */
    fun canParse(content: String): Boolean
}
