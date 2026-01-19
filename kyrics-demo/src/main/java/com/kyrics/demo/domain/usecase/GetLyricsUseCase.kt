package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.datasource.LyricsDataSource
import com.kyrics.demo.domain.model.LyricsData
import javax.inject.Inject

/**
 * Use case for retrieving lyrics data.
 */
class GetLyricsUseCase
    @Inject
    constructor(
        private val lyricsDataSource: LyricsDataSource,
    ) {
        operator fun invoke(): LyricsData =
            LyricsData(
                lines = lyricsDataSource.getLyrics(),
                totalDurationMs = lyricsDataSource.getTotalDurationMs(),
            )
    }
