package com.kyrics.demo.data.repository

import com.kyrics.demo.data.datasource.DemoLyricsDataSource
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.demo.domain.repository.LyricsRepository
import com.kyrics.models.KyricsLine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [LyricsRepository] that delegates to [DemoLyricsDataSource].
 */
@Singleton
class LyricsRepositoryImpl
    @Inject
    constructor(
        private val dataSource: DemoLyricsDataSource,
    ) : LyricsRepository {
        override suspend fun getLyrics(source: LyricsSource): List<KyricsLine> = dataSource.getLyrics(source)

        override fun getTotalDurationMs(): Long = dataSource.getTotalDurationMs()
    }
