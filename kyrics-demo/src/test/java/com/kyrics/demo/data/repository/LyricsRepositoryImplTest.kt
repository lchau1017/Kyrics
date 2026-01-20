package com.kyrics.demo.data.repository

import com.google.common.truth.Truth.assertThat
import com.kyrics.demo.data.datasource.DemoLyricsDataSource
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.models.KyricsLine
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LyricsRepositoryImplTest {
    private lateinit var dataSource: DemoLyricsDataSource
    private lateinit var repository: LyricsRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk()
        repository = LyricsRepositoryImpl(dataSource)
    }

    @Test
    fun `getLyrics delegates to data source`() =
        runTest {
            val expectedLines = listOf<KyricsLine>(mockk())
            coEvery { dataSource.getLyrics(LyricsSource.TTML) } returns expectedLines

            val result = repository.getLyrics(LyricsSource.TTML)

            assertThat(result).isEqualTo(expectedLines)
            coVerify { dataSource.getLyrics(LyricsSource.TTML) }
        }

    @Test
    fun `getLyrics passes correct source to data source`() =
        runTest {
            coEvery { dataSource.getLyrics(any()) } returns emptyList()

            repository.getLyrics(LyricsSource.LRC)

            coVerify { dataSource.getLyrics(LyricsSource.LRC) }
        }

    @Test
    fun `getTotalDurationMs delegates to data source`() {
        val expectedDuration = 172_830L
        every { dataSource.getTotalDurationMs() } returns expectedDuration

        val result = repository.getTotalDurationMs()

        assertThat(result).isEqualTo(expectedDuration)
    }
}
