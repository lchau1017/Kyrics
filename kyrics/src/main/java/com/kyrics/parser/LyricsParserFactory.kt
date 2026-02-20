package com.kyrics.parser

import com.kyrics.parser.ttml.TtmlParser
import com.kyrics.parser.xml.SimpleXmlParser

/**
 * Factory for parsing lyrics with automatic format detection.
 *
 * Currently supports TTML format only. The factory detects the format
 * from content and delegates to the appropriate parser.
 *
 * Example usage:
 * ```kotlin
 * val result = LyricsParserFactory.parse(fileContent)
 * when (result) {
 *     is ParseResult.Success -> { /* use result.lines */ }
 *     is ParseResult.Failure -> { /* handle result.error */ }
 * }
 * ```
 */
object LyricsParserFactory {
    /**
     * Detects the lyrics format from content.
     *
     * @param content The file content to analyze
     * @return The detected [LyricsFormat] or [LyricsFormat.UNKNOWN]
     */
    fun detectFormat(content: String): LyricsFormat =
        when {
            SimpleXmlParser.isTtml(content) -> LyricsFormat.TTML
            else -> LyricsFormat.UNKNOWN
        }

    /**
     * Parses content with automatic format detection.
     *
     * @param content The file content to parse
     * @return [ParseResult.Success] with parsed lyrics or [ParseResult.Failure] with error
     */
    fun parse(content: String): ParseResult {
        if (detectFormat(content) == LyricsFormat.UNKNOWN) {
            return ParseResult.Failure("Unable to detect lyrics format. Only TTML is supported.")
        }
        return TtmlParser().parse(content)
    }
}
