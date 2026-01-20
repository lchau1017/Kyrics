package com.kyrics.parser

import com.kyrics.parser.lrc.LrcParser
import com.kyrics.parser.ttml.TtmlParser
import com.kyrics.parser.xml.SimpleXmlParser

/**
 * Factory for creating and using lyrics parsers.
 *
 * Supports automatic format detection and parsing.
 *
 * Example usage:
 * ```kotlin
 * // Auto-detect format and parse
 * val result = LyricsParserFactory.parse(fileContent)
 *
 * // Parse with known format
 * val result = LyricsParserFactory.parse(fileContent, LyricsFormat.TTML)
 *
 * // Use file extension hint
 * val result = LyricsParserFactory.parseFile(fileContent, "lyrics.lrc")
 * ```
 */
object LyricsParserFactory {

    private val parsers: List<LyricsParser> = listOf(
        TtmlParser(),
        LrcParser()
    )

    /**
     * Detects the lyrics format from content.
     *
     * @param content The file content to analyze
     * @return The detected [LyricsFormat] or [LyricsFormat.UNKNOWN]
     */
    fun detectFormat(content: String): LyricsFormat {
        return when {
            SimpleXmlParser.isTtml(content) -> LyricsFormat.TTML
            LrcParser.isEnhancedLrc(content) -> LyricsFormat.ENHANCED_LRC
            LrcParser.isLrc(content) -> LyricsFormat.LRC
            else -> LyricsFormat.UNKNOWN
        }
    }

    /**
     * Detects the format from file extension.
     *
     * @param filename The filename or path
     * @return The detected [LyricsFormat] or null if unknown
     */
    fun detectFormatFromExtension(filename: String): LyricsFormat? {
        val extension = filename.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "ttml", "xml" -> LyricsFormat.TTML
            "lrc" -> LyricsFormat.LRC // Could be simple or enhanced
            else -> null
        }
    }

    /**
     * Creates a parser for the specified format.
     *
     * @param format The lyrics format
     * @return A [LyricsParser] for the format
     * @throws IllegalArgumentException if format is [LyricsFormat.UNKNOWN]
     */
    fun createParser(format: LyricsFormat): LyricsParser {
        return when (format) {
            LyricsFormat.TTML -> TtmlParser()
            LyricsFormat.LRC, LyricsFormat.ENHANCED_LRC -> LrcParser()
            LyricsFormat.UNKNOWN -> throw IllegalArgumentException("Cannot create parser for unknown format")
        }
    }

    /**
     * Parses content with automatic format detection.
     *
     * @param content The file content to parse
     * @return [ParseResult.Success] with parsed lyrics or [ParseResult.Failure] with error
     */
    fun parse(content: String): ParseResult {
        val format = detectFormat(content)
        if (format == LyricsFormat.UNKNOWN) {
            return ParseResult.Failure("Unable to detect lyrics format")
        }
        return createParser(format).parse(content)
    }

    /**
     * Parses content with a specified format.
     *
     * @param content The file content to parse
     * @param format The format to use
     * @return [ParseResult.Success] with parsed lyrics or [ParseResult.Failure] with error
     */
    fun parse(content: String, format: LyricsFormat): ParseResult {
        if (format == LyricsFormat.UNKNOWN) {
            return parse(content) // Fall back to auto-detection
        }
        return createParser(format).parse(content)
    }

    /**
     * Parses content using filename extension as a hint.
     * Falls back to content detection if extension is unknown.
     *
     * @param content The file content to parse
     * @param filename The filename (used for extension hint)
     * @return [ParseResult.Success] with parsed lyrics or [ParseResult.Failure] with error
     */
    fun parseFile(content: String, filename: String): ParseResult {
        val formatFromExtension = detectFormatFromExtension(filename)
        return if (formatFromExtension != null) {
            parse(content, formatFromExtension)
        } else {
            parse(content)
        }
    }

    /**
     * Gets all registered parsers.
     */
    fun getAllParsers(): List<LyricsParser> = parsers.toList()
}
