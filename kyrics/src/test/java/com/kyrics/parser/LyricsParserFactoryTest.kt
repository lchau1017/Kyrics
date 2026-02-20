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
        val ttmlContent =
            """
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
    fun `detectFormat returns UNKNOWN for unrecognized content`() {
        val unknownContent = "Just some plain text"

        assertThat(LyricsParserFactory.detectFormat(unknownContent)).isEqualTo(LyricsFormat.UNKNOWN)
    }

    @Test
    fun `detectFormat returns UNKNOWN for LRC content`() {
        val lrcContent =
            """
            [00:12.00]First line
            [00:17.20]Second line
            """.trimIndent()

        assertThat(LyricsParserFactory.detectFormat(lrcContent)).isEqualTo(LyricsFormat.UNKNOWN)
    }

    // ==================== Parse Tests ====================

    @Test
    fun `parse auto-detects and parses TTML`() {
        val ttmlContent =
            """
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
    fun `parse returns Failure for unknown format`() {
        val unknownContent = "Plain text without timestamps"

        val result = LyricsParserFactory.parse(unknownContent)

        assertThat(result).isInstanceOf(ParseResult.Failure::class.java)
    }

    @Test
    fun `parse returns Failure for LRC content`() {
        val lrcContent = "[00:12.00]Test line"

        val result = LyricsParserFactory.parse(lrcContent)

        assertThat(result).isInstanceOf(ParseResult.Failure::class.java)
    }
}
