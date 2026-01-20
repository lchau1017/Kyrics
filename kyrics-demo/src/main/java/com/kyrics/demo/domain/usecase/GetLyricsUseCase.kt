package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.datasource.LyricsDataSource
import com.kyrics.demo.domain.model.LyricsData
import com.kyrics.demo.domain.model.LyricsSource
import javax.inject.Inject

/**
 * Use case for retrieving lyrics data.
 */
class GetLyricsUseCase
    @Inject
    constructor(
        private val lyricsDataSource: LyricsDataSource,
    ) {
        suspend operator fun invoke(source: LyricsSource): LyricsData =
            LyricsData(
                lines = lyricsDataSource.getLyrics(source),
                totalDurationMs = lyricsDataSource.getTotalDurationMs(),
            )
    }
