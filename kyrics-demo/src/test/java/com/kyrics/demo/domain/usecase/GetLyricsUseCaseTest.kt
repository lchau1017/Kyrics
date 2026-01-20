package com.kyrics.demo.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.demo.domain.repository.LyricsRepository
import com.kyrics.models.KyricsLine
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetLyricsUseCaseTest {
    private lateinit var lyricsRepository: LyricsRepository
    private lateinit var useCase: GetLyricsUseCase

    @Before
    fun setup() {
        lyricsRepository = mockk()
        useCase = GetLyricsUseCase(lyricsRepository)
    }

    @Test
    fun `invoke returns lyrics data from repository`() =
        runTest {
            val expectedLines = listOf<KyricsLine>(mockk())
            val expectedDuration = 172_830L
            coEvery { lyricsRepository.getLyrics(LyricsSource.TTML) } returns expectedLines
            every { lyricsRepository.getTotalDurationMs() } returns expectedDuration

            val result = useCase(LyricsSource.TTML)

            assertThat(result.lines).isEqualTo(expectedLines)
            assertThat(result.totalDurationMs).isEqualTo(expectedDuration)
        }

    @Test
    fun `invoke calls repository with correct source`() =
        runTest {
            coEvery { lyricsRepository.getLyrics(any()) } returns emptyList()
            every { lyricsRepository.getTotalDurationMs() } returns 0L

            useCase(LyricsSource.LRC)

            coVerify { lyricsRepository.getLyrics(LyricsSource.LRC) }
        }

    @Test
    fun `invoke returns empty list when repository returns empty`() =
        runTest {
            coEvery { lyricsRepository.getLyrics(any()) } returns emptyList()
            every { lyricsRepository.getTotalDurationMs() } returns 0L

            val result = useCase(LyricsSource.TTML)

            assertThat(result.lines).isEmpty()
        }
}
