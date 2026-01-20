package com.kyrics.parser.ttml

import com.kyrics.models.KyricsLine
import com.kyrics.models.kyricsLine
import com.kyrics.parser.LyricsFormat
import com.kyrics.parser.LyricsMetadata
import com.kyrics.parser.LyricsParser
import com.kyrics.parser.ParseResult
import com.kyrics.parser.xml.SimpleXmlParser
import com.kyrics.parser.xml.XmlElement

/**
 * Parser for TTML (Timed Text Markup Language) lyrics.
 *
 * Supports the following TTML features:
 * - Syllable-level timing via `<span begin="..." end="...">` elements
 * - Background/accompaniment vocals via `ttm:role="x-bg"` attribute
 * - Multiple time formats: milliseconds (100ms), seconds (1.5s), and clock time (00:01:30.500)
 *
 * Example TTML structure:
 * ```xml
 * <tt xmlns="http://www.w3.org/ns/ttml" xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
 *   <body>
 *     <div>
 *       <p begin="0ms" end="5000ms">
 *         <span begin="0ms" end="500ms">Hello </span>
 *         <span begin="500ms" end="1000ms">World</span>
 *         <span ttm:role="x-bg" begin="2000ms" end="3000ms">
 *           <span begin="2000ms" end="2500ms">(ooh)</span>
 *         </span>
 *       </p>
 *     </div>
 *   </body>
 * </tt>
 * ```
 */
class TtmlParser : LyricsParser {

    override val supportedFormat: LyricsFormat = LyricsFormat.TTML

    override fun canParse(content: String): Boolean = SimpleXmlParser.isTtml(content)

    override fun parse(content: String): ParseResult {
        return try {
            val xmlParser = SimpleXmlParser(content)
            val lines = mutableListOf<KyricsLine>()

            // Find all <p> elements (paragraphs/lines)
            val paragraphs = xmlParser.findElements("p")

            for (paragraph in paragraphs) {
                parseParagraph(paragraph, xmlParser)?.let { parsedLines ->
                    lines.addAll(parsedLines)
                }
            }

            ParseResult.Success(
                lines = lines.sortedBy { it.start },
                metadata = LyricsMetadata()
            )
        } catch (e: Exception) {
            ParseResult.Failure(
                error = "Failed to parse TTML: ${e.message}",
                lineNumber = null
            )
        }
    }

    /**
     * Parses a single <p> element into one or more KyricsLines.
     * Returns multiple lines if there are both main vocals and background vocals.
     */
    private fun parseParagraph(
        paragraph: XmlElement,
        xmlParser: SimpleXmlParser
    ): List<KyricsLine>? {
        val timing = paragraph.getTiming() ?: return null

        val mainSyllables = mutableListOf<Syllable>()
        val bgSyllables = mutableListOf<Syllable>()
        var bgTiming: Timing? = null

        // Find all <span> elements within this paragraph
        val spans = xmlParser.findChildElements(paragraph.innerXml, "span")

        for (span in spans) {
            when {
                span.isBackgroundVocal() -> {
                    // This is a background vocal container
                    bgTiming = span.getTiming()
                    // Find nested spans within the background vocal
                    val nestedSpans = xmlParser.findChildElements(span.innerXml, "span")
                    for (nestedSpan in nestedSpans) {
                        nestedSpan.toSyllable()?.let { bgSyllables.add(it) }
                    }
                    // If no nested spans, treat the whole bg span as a syllable
                    if (nestedSpans.isEmpty() && span.textContent.isNotBlank()) {
                        span.toSyllable()?.let { bgSyllables.add(it) }
                    }
                }
                else -> {
                    span.toSyllable()?.let { mainSyllables.add(it) }
                }
            }
        }

        val results = mutableListOf<KyricsLine>()

        // Create main line
        mainSyllables.toLine(timing.start, timing.end, isAccompaniment = false)?.let {
            results.add(it)
        }

        // Create background line if present
        if (bgSyllables.isNotEmpty()) {
            val bgStart = bgTiming?.start ?: bgSyllables.first().start
            val bgEnd = bgTiming?.end ?: bgSyllables.last().end
            bgSyllables.toLine(bgStart, bgEnd, isAccompaniment = true)?.let {
                results.add(it)
            }
        }

        return results.takeIf { it.isNotEmpty() }
    }

    private fun XmlElement.isBackgroundVocal(): Boolean {
        return getAttributeWithNamespace("ttm", "role") == "x-bg" ||
                getAttribute("role") == "x-bg"
    }

    private fun XmlElement.getTiming(): Timing? {
        val begin = getAttribute("begin") ?: return null
        val end = getAttribute("end") ?: return null
        return Timing(parseTime(begin), parseTime(end))
    }

    private fun XmlElement.toSyllable(): Syllable? {
        val timing = getTiming() ?: return null
        val text = textContent
        return if (text.isNotEmpty()) Syllable(text, timing.start, timing.end) else null
    }

    private fun List<Syllable>.toLine(
        start: Int,
        end: Int,
        isAccompaniment: Boolean
    ): KyricsLine? {
        if (isEmpty()) return null
        return kyricsLine(start = start, end = end) {
            alignment("center")
            if (isAccompaniment) accompaniment()
            forEachIndexed { index, syllable ->
                val text = if (index == lastIndex) syllable.text.trimEnd() else syllable.text
                syllable(text, start = syllable.start, end = syllable.end)
            }
        }
    }
}

internal data class Timing(val start: Int, val end: Int)

internal data class Syllable(val text: String, val start: Int, val end: Int)
