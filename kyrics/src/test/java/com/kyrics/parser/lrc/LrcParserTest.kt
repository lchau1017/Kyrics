package com.kyrics.parser.lrc

import com.google.common.truth.Truth.assertThat
import com.kyrics.parser.ParseResult
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LrcParser.
 * Tests parsing of both simple and enhanced LRC formats.
 */
class LrcParserTest {
    private lateinit var parser: LrcParser

    @Before
    fun setup() {
        parser = LrcParser()
    }

    // ==================== Format Detection Tests ====================

    @Test
    fun `canParse returns true for simple LRC content`() {
        val lrcContent =
            """
            [00:12.00]First line
            [00:17.20]Second line
            """.trimIndent()
        assertThat(parser.canParse(lrcContent)).isTrue()
    }

    @Test
    fun `canParse returns true for enhanced LRC content`() {
        val lrcContent =
            """
            [00:12.00]<00:12.00>First <00:13.50>line
            """.trimIndent()
        assertThat(parser.canParse(lrcContent)).isTrue()
    }

    @Test
    fun `canParse returns false for TTML content`() {
        val ttmlContent =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml"></tt>
            """.trimIndent()
        assertThat(parser.canParse(ttmlContent)).isFalse()
    }

    @Test
    fun `canParse returns false for plain text`() {
        val plainText = "Just some plain text without timestamps"
        assertThat(parser.canParse(plainText)).isFalse()
    }

    @Test
    fun `isLrc detects LRC format`() {
        assertThat(LrcParser.isLrc("[00:12.00]Line")).isTrue()
        assertThat(LrcParser.isLrc("[01:30.50]Another line")).isTrue()
        assertThat(LrcParser.isLrc("Plain text")).isFalse()
    }

    @Test
    fun `isEnhancedLrc detects enhanced format`() {
        assertThat(LrcParser.isEnhancedLrc("[00:12.00]<00:12.00>Word")).isTrue()
        assertThat(LrcParser.isEnhancedLrc("[00:12.00]Simple line")).isFalse()
    }

    // ==================== Simple LRC Parsing Tests ====================

    @Test
    fun `parse returns Success for simple LRC`() {
        val lrcContent =
            """
            [00:12.00]First line
            [00:17.20]Second line
            """.trimIndent()

        val result = parser.parse(lrcContent)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
        val success = result as ParseResult.Success
        assertThat(success.lines).hasSize(2)
    }

    @Test
    fun `parse extracts timing correctly for simple LRC`() {
        val lrcContent = "[01:30.50]Test line"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines).hasSize(1)
        assertThat(result.lines[0].start).isEqualTo(90_500) // 1*60*1000 + 30*1000 + 500
    }

    @Test
    fun `parse handles mm colon ss colon xx format`() {
        val lrcContent = "[01:30:50]Test line"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines[0].start).isEqualTo(90_500)
    }

    @Test
    fun `parse splits simple LRC lines into word-based syllables`() {
        val lrcContent = "[00:12.00]Hello World"

        val result = parser.parse(lrcContent) as ParseResult.Success

        // Simple LRC lines are split into words for better karaoke display
        assertThat(result.lines[0].syllables).hasSize(2)
        assertThat(result.lines[0].syllables[0].content).isEqualTo("Hello")
        assertThat(result.lines[0].syllables[1].content).isEqualTo("World")
        // getContent() joins syllables without separator
        assertThat(result.lines[0].getContent()).isEqualTo("HelloWorld")
    }

    @Test
    fun `parse sorts lines by start time`() {
        val lrcContent =
            """
            [00:30.00]Third
            [00:10.00]First
            [00:20.00]Second
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines[0].getContent()).isEqualTo("First")
        assertThat(result.lines[1].getContent()).isEqualTo("Second")
        assertThat(result.lines[2].getContent()).isEqualTo("Third")
    }

    @Test
    fun `parse calculates end times from next line`() {
        val lrcContent =
            """
            [00:10.00]First
            [00:20.00]Second
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        // First line ends when second begins
        assertThat(result.lines[0].end).isAtLeast(result.lines[0].start)
    }

    // ==================== Metadata Parsing Tests ====================

    @Test
    fun `parse extracts title metadata`() {
        val lrcContent =
            """
            [ti:Song Title]
            [00:12.00]Lyrics
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.metadata.title).isEqualTo("Song Title")
    }

    @Test
    fun `parse extracts artist metadata`() {
        val lrcContent =
            """
            [ar:Artist Name]
            [00:12.00]Lyrics
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.metadata.artist).isEqualTo("Artist Name")
    }

    @Test
    fun `parse extracts album metadata`() {
        val lrcContent =
            """
            [al:Album Name]
            [00:12.00]Lyrics
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.metadata.album).isEqualTo("Album Name")
    }

    @Test
    fun `parse extracts offset metadata`() {
        val lrcContent =
            """
            [offset:500]
            [00:12.00]Lyrics
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.metadata.offset).isEqualTo(500)
    }

    @Test
    fun `parse applies offset to timing`() {
        val lrcContent =
            """
            [offset:1000]
            [00:10.00]Lyrics
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        // 10 seconds + 1 second offset = 11000ms
        assertThat(result.lines[0].start).isEqualTo(11_000)
    }

    @Test
    fun `parse extracts all metadata together`() {
        val lrcContent =
            """
            [ti:My Song]
            [ar:My Artist]
            [al:My Album]
            [offset:100]
            [00:12.00]Lyrics
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.metadata.title).isEqualTo("My Song")
        assertThat(result.metadata.artist).isEqualTo("My Artist")
        assertThat(result.metadata.album).isEqualTo("My Album")
        assertThat(result.metadata.offset).isEqualTo(100)
    }

    // ==================== Enhanced LRC Parsing Tests ====================

    @Test
    fun `parse returns Success for enhanced LRC`() {
        val lrcContent = "[00:12.00]<00:12.00>First <00:13.50>line <00:14.20>here"

        val result = parser.parse(lrcContent)

        assertThat(result).isInstanceOf(ParseResult.Success::class.java)
    }

    @Test
    fun `parse creates syllables for each word in enhanced LRC`() {
        val lrcContent = "[00:12.00]<00:12.00>First <00:13.50>line <00:14.20>here"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines).hasSize(1)
        assertThat(result.lines[0].syllables).hasSize(3)
    }

    @Test
    fun `parse extracts word timing in enhanced LRC`() {
        val lrcContent = "[00:12.00]<00:12.00>First <00:13.50>second"

        val result = parser.parse(lrcContent) as ParseResult.Success

        val syllables = result.lines[0].syllables
        assertThat(syllables[0].content).isEqualTo("First ")
        assertThat(syllables[0].start).isEqualTo(12_000)
        assertThat(syllables[1].content).isEqualTo("second")
        assertThat(syllables[1].start).isEqualTo(13_500)
    }

    @Test
    fun `parse calculates word end times from next word`() {
        val lrcContent = "[00:12.00]<00:12.00>First <00:13.50>second"

        val result = parser.parse(lrcContent) as ParseResult.Success

        val syllables = result.lines[0].syllables
        // First word ends when second begins
        assertThat(syllables[0].end).isEqualTo(13_500)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `parse handles empty lines`() {
        val lrcContent =
            """
            [00:12.00]First

            [00:17.00]Second
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines).hasSize(2)
    }

    @Test
    fun `parse skips lines with empty content`() {
        val lrcContent =
            """
            [00:12.00]
            [00:17.00]Actual content
            """.trimIndent()

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines).hasSize(1)
        // getContent() joins syllables without separator
        assertThat(result.lines[0].getContent()).isEqualTo("Actualcontent")
    }

    @Test
    fun `parse handles content-only file gracefully`() {
        val lrcContent = "No timestamps here"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines).isEmpty()
    }

    @Test
    fun `parse handles single digit minutes`() {
        val lrcContent = "[0:12.00]Test"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.lines[0].start).isEqualTo(12_000)
    }

    // ==================== Warnings Tests ====================

    @Test
    fun `parse adds warning for simple LRC format`() {
        val lrcContent = "[00:12.00]Simple line without word timing"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.hasWarnings).isTrue()
        assertThat(result.warnings).hasSize(1)
        assertThat(result.warnings[0]).contains("Simple LRC")
    }

    @Test
    fun `parse has no warnings for enhanced LRC format`() {
        val lrcContent = "[00:12.00]<00:12.00>Word <00:13.00>level <00:14.00>timing"

        val result = parser.parse(lrcContent) as ParseResult.Success

        assertThat(result.hasWarnings).isFalse()
        assertThat(result.warnings).isEmpty()
    }
}
