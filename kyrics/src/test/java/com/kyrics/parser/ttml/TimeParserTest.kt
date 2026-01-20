package com.kyrics.parser.ttml

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for TimeParser.
 * Tests parsing of various TTML time format strings.
 */
class TimeParserTest {
    @Test
    fun `parseTime handles milliseconds format`() {
        assertThat(parseTime("100ms")).isEqualTo(100)
        assertThat(parseTime("0ms")).isEqualTo(0)
        assertThat(parseTime("5000ms")).isEqualTo(5000)
    }

    @Test
    fun `parseTime handles seconds format`() {
        assertThat(parseTime("1s")).isEqualTo(1000)
        assertThat(parseTime("1.5s")).isEqualTo(1500)
        assertThat(parseTime("0.5s")).isEqualTo(500)
        assertThat(parseTime("10.25s")).isEqualTo(10_250)
    }

    @Test
    fun `parseTime handles mm colon ss dot xxx format`() {
        assertThat(parseTime("00:00.000")).isEqualTo(0)
        assertThat(parseTime("00:01.000")).isEqualTo(1000)
        assertThat(parseTime("00:01.500")).isEqualTo(1500)
        assertThat(parseTime("01:30.500")).isEqualTo(90_500)
        assertThat(parseTime("02:00.000")).isEqualTo(120_000)
    }

    @Test
    fun `parseTime handles hh colon mm colon ss dot xxx format`() {
        assertThat(parseTime("00:00:00.000")).isEqualTo(0)
        assertThat(parseTime("00:00:01.000")).isEqualTo(1000)
        assertThat(parseTime("00:01:00.000")).isEqualTo(60_000)
        assertThat(parseTime("01:00:00.000")).isEqualTo(3_600_000)
        assertThat(parseTime("00:01:30.500")).isEqualTo(90_500)
        assertThat(parseTime("01:30:45.250")).isEqualTo(5_445_250)
    }

    @Test
    fun `parseTime handles plain integer format`() {
        assertThat(parseTime("1000")).isEqualTo(1000)
        assertThat(parseTime("0")).isEqualTo(0)
        assertThat(parseTime("500")).isEqualTo(500)
    }

    @Test
    fun `parseTime handles single digit fraction`() {
        assertThat(parseTime("00:01.5")).isEqualTo(1500)
    }

    @Test
    fun `parseTime handles two digit fraction`() {
        assertThat(parseTime("00:01.50")).isEqualTo(1500)
        assertThat(parseTime("00:01.05")).isEqualTo(1050)
    }

    @Test
    fun `parseTime handles three digit fraction`() {
        assertThat(parseTime("00:01.500")).isEqualTo(1500)
        assertThat(parseTime("00:01.050")).isEqualTo(1050)
        assertThat(parseTime("00:01.005")).isEqualTo(1005)
    }

    @Test
    fun `parseTime returns 0 for invalid format`() {
        assertThat(parseTime("invalid")).isEqualTo(0)
        assertThat(parseTime("")).isEqualTo(0)
        assertThat(parseTime("abc")).isEqualTo(0)
    }

    @Test
    fun `parseTime handles edge cases`() {
        assertThat(parseTime("59:59.999")).isEqualTo(3_599_999)
        assertThat(parseTime("00:00.001")).isEqualTo(1)
    }
}
