package com.kyrics.demo.data.datasource

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class DemoLyricsDataSourceTest {
    private lateinit var context: Context
    private lateinit var assetManager: AssetManager
    private lateinit var dataSource: DemoLyricsDataSource

    private val sampleTtml =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <tt xmlns="http://www.w3.org/ns/ttml">
            <body>
                <div>
                    <p begin="0ms" end="5000ms">
                        <span begin="0ms" end="2500ms">Hello </span>
                        <span begin="2500ms" end="5000ms">World</span>
                    </p>
                    <p begin="5000ms" end="10000ms">
                        <span begin="5000ms" end="7500ms">Test </span>
                        <span begin="7500ms" end="10000ms">Line</span>
                    </p>
                </div>
            </body>
        </tt>
        """.trimIndent()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0

        context = mockk()
        assetManager = mockk()
        every { context.assets } returns assetManager
        dataSource = DemoLyricsDataSource(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getLyrics returns parsed lines`() =
        runTest {
            every { assetManager.open("golden-hour.ttml") } returns
                ByteArrayInputStream(sampleTtml.toByteArray())

            val lyrics = dataSource.getLyrics()

            assertThat(lyrics).hasSize(2)
        }

    @Test
    fun `getLyrics returns empty list when file not found`() =
        runTest {
            every { assetManager.open(any()) } throws java.io.IOException("File not found")

            val lyrics = dataSource.getLyrics()

            assertThat(lyrics).isEmpty()
        }

    @Test
    fun `getLyrics returns empty list when parsing fails`() =
        runTest {
            every { assetManager.open("golden-hour.ttml") } returns
                ByteArrayInputStream("invalid content".toByteArray())

            val lyrics = dataSource.getLyrics()

            assertThat(lyrics).isEmpty()
        }

    @Test
    fun `getTotalDurationMs returns correct duration`() {
        assertThat(dataSource.getTotalDurationMs()).isEqualTo(DemoLyricsDataSource.TOTAL_DURATION_MS)
    }

    @Test
    fun `getLyrics returns lines with valid timing`() =
        runTest {
            every { assetManager.open("golden-hour.ttml") } returns
                ByteArrayInputStream(sampleTtml.toByteArray())

            val lyrics = dataSource.getLyrics()

            lyrics.forEach { line ->
                assertThat(line.start).isAtLeast(0)
                assertThat(line.end).isGreaterThan(line.start)
            }
        }

    @Test
    fun `getLyrics returns lines with syllables`() =
        runTest {
            every { assetManager.open("golden-hour.ttml") } returns
                ByteArrayInputStream(sampleTtml.toByteArray())

            val lyrics = dataSource.getLyrics()

            lyrics.forEach { line ->
                assertThat(line.syllables).isNotEmpty()
            }
        }

    @Test
    fun `getLyrics returns lines in chronological order`() =
        runTest {
            every { assetManager.open("golden-hour.ttml") } returns
                ByteArrayInputStream(sampleTtml.toByteArray())

            val lyrics = dataSource.getLyrics()

            for (i in 0 until lyrics.size - 1) {
                assertThat(lyrics[i].end).isAtMost(lyrics[i + 1].start)
            }
        }
}
