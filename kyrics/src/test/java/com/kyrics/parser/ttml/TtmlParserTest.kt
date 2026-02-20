package com.kyrics.parser.ttml

import com.google.common.truth.Truth.assertThat
import com.kyrics.parser.LyricsFormat
import com.kyrics.parser.ParseResult
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TtmlParser.
 */
class TtmlParserTest {
    private lateinit var parser: TtmlParser

    @Before
    fun setup() {
        parser = TtmlParser()
    }

    @Test
    fun `supportedFormat returns TTML`() {
        assertThat(parser.supportedFormat).isEqualTo(LyricsFormat.TTML)
    }

    @Test
    fun `parse returns Success for valid TTML`() {
        val ttmlContent =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p begin="0ms" end="2000ms">
                    <span begin="0ms" end="500ms">Hello </span>
                    <span begin="500ms" end="1000ms">World</span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
        val success = result as ParseResult.Success
        assertThat(success.lines).hasSize(1)
        assertThat(success.lines[0].syllables).hasSize(2)
        assertThat(success.lines[0].syllables[0].content).isEqualTo("Hello ")
        assertThat(success.lines[0].syllables[1].content).isEqualTo("World")
    }

    @Test
    fun `parse extracts timing correctly`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p begin="1000ms" end="3000ms">
                    <span begin="1000ms" end="2000ms">Test</span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines[0].start).isEqualTo(1000)
        assertThat(result.lines[0].end).isEqualTo(3000)
        assertThat(result.lines[0].syllables[0].start).isEqualTo(1000)
        assertThat(result.lines[0].syllables[0].end).isEqualTo(2000)
    }

    @Test
    fun `parse handles clock time format`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p begin="00:01.500" end="00:03.000">
                    <span begin="00:01.500" end="00:02.500">Test</span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines[0].start).isEqualTo(1500)
        assertThat(result.lines[0].end).isEqualTo(3000)
    }

    @Test
    fun `parse handles multiple paragraphs`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p begin="0ms" end="2000ms">
                    <span begin="0ms" end="1000ms">Line one</span>
                  </p>
                  <p begin="3000ms" end="5000ms">
                    <span begin="3000ms" end="4000ms">Line two</span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines).hasSize(2)
        assertThat(result.lines[0].getContent()).isEqualTo("Line one")
        assertThat(result.lines[1].getContent()).isEqualTo("Line two")
    }

    @Test
    fun `parse sorts lines by start time`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p begin="5000ms" end="7000ms">
                    <span begin="5000ms" end="6000ms">Second</span>
                  </p>
                  <p begin="0ms" end="2000ms">
                    <span begin="0ms" end="1000ms">First</span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines[0].getContent()).isEqualTo("First")
        assertThat(result.lines[1].getContent()).isEqualTo("Second")
    }

    @Test
    fun `parse handles background vocals`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
              <body>
                <div>
                  <p begin="0ms" end="5000ms">
                    <span begin="0ms" end="1000ms">Main </span>
                    <span begin="1000ms" end="2000ms">vocal</span>
                    <span ttm:role="x-bg" begin="2000ms" end="4000ms">
                      <span begin="2000ms" end="3000ms">(ooh)</span>
                    </span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines).hasSize(2)

        // Main line
        val mainLine = result.lines.find { !it.isAccompaniment }
        assertThat(mainLine).isNotNull()
        assertThat(mainLine!!.getContent()).isEqualTo("Main vocal")

        // Background line
        val bgLine = result.lines.find { it.isAccompaniment }
        assertThat(bgLine).isNotNull()
        assertThat(bgLine!!.getContent()).isEqualTo("(ooh)")
    }

    @Test
    fun `parse returns empty lines for empty body`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines).isEmpty()
    }

    @Test
    fun `parse skips paragraphs without timing`() {
        val ttmlContent =
            """
            <tt xmlns="http://www.w3.org/ns/ttml">
              <body>
                <div>
                  <p>
                    <span>No timing</span>
                  </p>
                  <p begin="0ms" end="2000ms">
                    <span begin="0ms" end="1000ms">With timing</span>
                  </p>
                </div>
              </body>
            </tt>
            """.trimIndent()

        val result = parser.parse(ttmlContent) as ParseResult.Success

        assertThat(result.lines).hasSize(1)
        assertThat(result.lines[0].getContent()).isEqualTo("With timing")
    }
}
