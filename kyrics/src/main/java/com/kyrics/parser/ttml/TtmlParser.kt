package com.kyrics.parser.ttml

import com.kyrics.models.KyricsLine
import com.kyrics.models.kyricsLine
import com.kyrics.parser.LyricsFormat
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

    override fun parse(content: String): ParseResult =
        try {
            val xmlParser = SimpleXmlParser(content)
            val lines =
                xmlParser
                    .findElements("p")
                    .flatMap { parseParagraph(it, xmlParser) }
                    .sortedBy { it.start }
            val durationMs =
                xmlParser
                    .findElements("body")
                    .firstOrNull()
                    ?.getAttribute("dur")
                    ?.let { parseTime(it).toLong() }
            ParseResult.Success(lines = lines, durationMs = durationMs)
        } catch (e: IllegalArgumentException) {
            ParseResult.Failure(error = "Invalid TTML content: ${e.message}")
        } catch (e: NumberFormatException) {
            ParseResult.Failure(error = "Invalid time format in TTML: ${e.message}")
        }

    /**
     * Parses a single <p> element into one or more KyricsLines.
     * Returns multiple lines if there are both main vocals and background vocals.
     */
    private fun parseParagraph(
        paragraph: XmlElement,
        xmlParser: SimpleXmlParser,
    ): List<KyricsLine> {
        val timing = paragraph.getTiming() ?: return emptyList()
        val spans = xmlParser.findChildElements(paragraph.innerXml, "span")

        val mainLine = buildMainLine(spans, timing)
        val bgLine = buildBackgroundLine(spans, xmlParser, timing)

        return listOfNotNull(mainLine, bgLine)
    }

    /**
     * Builds the main vocal line from non-background spans.
     */
    private fun buildMainLine(
        spans: List<XmlElement>,
        timing: Timing,
    ): KyricsLine? {
        val syllables =
            spans
                .filterNot { it.isBackgroundVocal() }
                .mapNotNull { it.toSyllable() }
        return syllables.toLine(timing.start, timing.end, isAccompaniment = false)
    }

    /**
     * Builds the background vocal line from spans marked with ttm:role="x-bg".
     * Background vocals may contain nested spans for syllable-level timing.
     */
    private fun buildBackgroundLine(
        spans: List<XmlElement>,
        xmlParser: SimpleXmlParser,
        parentTiming: Timing,
    ): KyricsLine? {
        val bgSpan = spans.find { it.isBackgroundVocal() } ?: return null
        val bgTiming = bgSpan.getTiming() ?: parentTiming

        val nestedSpans = xmlParser.findChildElements(bgSpan.innerXml, "span")
        val syllables =
            if (nestedSpans.isNotEmpty()) {
                nestedSpans.mapNotNull { it.toSyllable() }
            } else if (bgSpan.textContent.isNotBlank()) {
                listOfNotNull(bgSpan.toSyllable())
            } else {
                emptyList()
            }

        return syllables.toLine(bgTiming.start, bgTiming.end, isAccompaniment = true)
    }

    private fun XmlElement.isBackgroundVocal(): Boolean =
        getAttributeWithNamespace("ttm", "role") == "x-bg" ||
            getAttribute("role") == "x-bg"

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
        isAccompaniment: Boolean,
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

internal data class Timing(
    val start: Int,
    val end: Int,
)

internal data class Syllable(
    val text: String,
    val start: Int,
    val end: Int,
)
