package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.model.LyricsData
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.demo.domain.repository.LyricsRepository
import javax.inject.Inject

/**
 * Use case for retrieving lyrics data.
 */
class GetLyricsUseCase
    @Inject
    constructor(
        private val lyricsRepository: LyricsRepository,
    ) {
        suspend operator fun invoke(source: LyricsSource): LyricsData =
            LyricsData(
                lines = lyricsRepository.getLyrics(source),
                totalDurationMs = lyricsRepository.getTotalDurationMs(),
            )
    }
