package com.kyrics.demo.data.datasource

import com.google.common.truth.Truth.assertThat
import com.kyrics.demo.domain.datasource.LyricsDataSource
import org.junit.Before
import org.junit.Test

class DemoLyricsDataSourceTest {
    private lateinit var dataSource: LyricsDataSource

    @Before
    fun setup() {
        dataSource = DemoLyricsDataSource()
    }

    @Test
    fun `getLyrics returns non-empty list`() {
        val lyrics = dataSource.getLyrics()

        assertThat(lyrics).isNotEmpty()
    }

    @Test
    fun `getLyrics returns correct number of lines`() {
        val lyrics = dataSource.getLyrics()

        // We have 9 lines in our demo lyrics
        assertThat(lyrics).hasSize(9)
    }

    @Test
    fun `each line has valid start and end times`() {
        val lyrics = dataSource.getLyrics()

        lyrics.forEach { line ->
            assertThat(line.start).isAtLeast(0)
            assertThat(line.end).isGreaterThan(line.start)
        }
    }

    @Test
    fun `each line has syllables`() {
        val lyrics = dataSource.getLyrics()

        lyrics.forEach { line ->
            assertThat(line.syllables).isNotEmpty()
        }
    }

    @Test
    fun `lines are in chronological order`() {
        val lyrics = dataSource.getLyrics()

        for (i in 0 until lyrics.size - 1) {
            assertThat(lyrics[i].end).isAtMost(lyrics[i + 1].start)
        }
    }

    @Test
    fun `syllables within line are in order`() {
        val lyrics = dataSource.getLyrics()

        lyrics.forEach { line ->
            for (i in 0 until line.syllables.size - 1) {
                assertThat(line.syllables[i].end).isAtMost(line.syllables[i + 1].start)
            }
        }
    }

    @Test
    fun `total duration is correct`() {
        val lyrics = dataSource.getLyrics()
        val lastLineEnd = lyrics.last().end

        assertThat(dataSource.getTotalDurationMs()).isEqualTo(lastLineEnd)
    }

    @Test
    fun `first line starts at time zero`() {
        val lyrics = dataSource.getLyrics()

        assertThat(lyrics.first().start).isEqualTo(0)
    }

    @Test
    fun `syllable text is not empty`() {
        val lyrics = dataSource.getLyrics()

        lyrics.forEach { line ->
            line.syllables.forEach { syllable ->
                assertThat(syllable.content).isNotEmpty()
            }
        }
    }
}
