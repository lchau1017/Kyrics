package com.kyrics.parser

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for LyricsParserFactory.
 */
class LyricsParserFactoryTest {

    // ==================== Format Detection Tests ====================

    @Test
    fun `detectFormat returns TTML for XML content`() {
        val ttmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml"></tt>
        """.trimIndent()

        assertThat(LyricsParserFactory.detectFormat(ttmlContent)).isEqualTo(LyricsFormat.TTML)
    }

    @Test
    fun `detectFormat returns TTML for content starting with tt tag`() {
        val ttmlContent = """<tt xmlns="http://www.w3.org/ns/ttml"></tt>"""

        assertThat(LyricsParserFactory.detectFormat(ttmlContent)).isEqualTo(LyricsFormat.TTML)
    }

    @Test
    fun `detectFormat returns LRC for simple LRC content`() {
        val lrcContent = """
            [00:12.00]First line
            [00:17.20]Second line
        """.trimIndent()

        assertThat(LyricsParserFactory.detectFormat(lrcContent)).isEqualTo(LyricsFormat.LRC)
    }

    @Test
    fun `detectFormat returns ENHANCED_LRC for enhanced LRC content`() {
        val enhancedLrcContent = "[00:12.00]<00:12.00>First <00:13.50>line"

        assertThat(LyricsParserFactory.detectFormat(enhancedLrcContent)).isEqualTo(LyricsFormat.ENHANCED_LRC)
    }

    @Test
    fun `detectFormat returns UNKNOWN for unrecognized content`() {
        val unknownContent = "Just some plain text"

        assertThat(LyricsParserFactory.detectFormat(unknownContent)).isEqualTo(LyricsFormat.UNKNOWN)
    }

    // ==================== Extension Detection Tests ====================

    @Test
    fun `detectFormatFromExtension returns TTML for ttml extension`() {
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.ttml")).isEqualTo(LyricsFormat.TTML)
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.TTML")).isEqualTo(LyricsFormat.TTML)
    }

    @Test
    fun `detectFormatFromExtension returns TTML for xml extension`() {
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.xml")).isEqualTo(LyricsFormat.TTML)
    }

    @Test
    fun `detectFormatFromExtension returns LRC for lrc extension`() {
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.lrc")).isEqualTo(LyricsFormat.LRC)
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.LRC")).isEqualTo(LyricsFormat.LRC)
    }

    @Test
    fun `detectFormatFromExtension returns null for unknown extension`() {
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.txt")).isNull()
        assertThat(LyricsParserFactory.detectFormatFromExtension("lyrics.mp3")).isNull()
    }

    @Test
    fun `detectFormatFromExtension handles full path`() {
        assertThat(LyricsParserFactory.detectFormatFromExtension("/path/to/lyrics.lrc")).isEqualTo(LyricsFormat.LRC)
    }

    // ==================== Parser Creation Tests ====================

    @Test
    fun `createParser returns TtmlParser for TTML format`() {
        val parser = LyricsParserFactory.createParser(LyricsFormat.TTML)
        assertThat(parser.supportedFormat).isEqualTo(LyricsFormat.TTML)
    }

    @Test
    fun `createParser returns LrcParser for LRC format`() {
        val parser = LyricsParserFactory.createParser(LyricsFormat.LRC)
        assertThat(parser.supportedFormat).isIn(listOf(LyricsFormat.LRC, LyricsFormat.ENHANCED_LRC))
    }

    @Test
    fun `createParser returns LrcParser for ENHANCED_LRC format`() {
        val parser = LyricsParserFactory.createParser(LyricsFormat.ENHANCED_LRC)
        assertThat(parser.supportedFormat).isIn(listOf(LyricsFormat.LRC, LyricsFormat.ENHANCED_LRC))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createParser throws for UNKNOWN format`() {
        LyricsParserFactory.createParser(LyricsFormat.UNKNOWN)
    }

    // ==================== Parse Tests ====================

    @Test
    fun `parse auto-detects and parses TTML`() {
        val ttmlContent = """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p begin="0ms" end="2000ms">
                    <span begin="0ms" end="1000ms">Test</span>
                  </p>
                </div>
              </body>
            </tt>
        """.trimIndent()

        val result = LyricsParserFactory.parse(ttmlContent)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
        val success = result as ParseResult.Success
        assertThat(success.lines).hasSize(1)
    }

    @Test
    fun `parse auto-detects and parses LRC`() {
        val lrcContent = """
            [00:12.00]First line
            [00:17.20]Second line
        """.trimIndent()

        val result = LyricsParserFactory.parse(lrcContent)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
        val success = result as ParseResult.Success
        assertThat(success.lines).hasSize(2)
    }

    @Test
    fun `parse returns Failure for unknown format`() {
        val unknownContent = "Plain text without timestamps"

        val result = LyricsParserFactory.parse(unknownContent)

        assertThat(result).isInstanceOf(ParseResult.Failure::class.java)
    }

    @Test
    fun `parse with format uses specified parser`() {
        val lrcContent = "[00:12.00]Test line"

        val result = LyricsParserFactory.parse(lrcContent, LyricsFormat.LRC)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
    }

    @Test
    fun `parse with UNKNOWN format falls back to auto-detection`() {
        val lrcContent = "[00:12.00]Test line"

        val result = LyricsParserFactory.parse(lrcContent, LyricsFormat.UNKNOWN)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
    }

    // ==================== parseFile Tests ====================

    @Test
    fun `parseFile uses extension hint`() {
        val lrcContent = "[00:12.00]Test line"

        val result = LyricsParserFactory.parseFile(lrcContent, "song.lrc")

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
    }

    @Test
    fun `parseFile falls back to content detection for unknown extension`() {
        val lrcContent = "[00:12.00]Test line"

        val result = LyricsParserFactory.parseFile(lrcContent, "song.txt")

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
    }

    // ==================== getAllParsers Tests ====================

    @Test
    fun `getAllParsers returns non-empty list`() {
        val parsers = LyricsParserFactory.getAllParsers()

        assertThat(parsers).isNotEmpty()
    }

    @Test
    fun `getAllParsers includes TTML parser`() {
        val parsers = LyricsParserFactory.getAllParsers()

        assertThat(parsers.any { it.supportedFormat == LyricsFormat.TTML }).isTrue()
    }

    @Test
    fun `getAllParsers includes LRC parser`() {
        val parsers = LyricsParserFactory.getAllParsers()

        assertThat(parsers.any { it.supportedFormat == LyricsFormat.LRC || it.supportedFormat == LyricsFormat.ENHANCED_LRC }).isTrue()
    }
}
