package com.kyrics.demo.di

import com.kyrics.demo.data.datasource.DemoLyricsDataSource
import com.kyrics.demo.data.repository.DemoSettingsRepositoryImpl
import com.kyrics.demo.domain.datasource.LyricsDataSource
import com.kyrics.demo.domain.dispatcher.DispatcherProvider
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoModule {
    @Binds
    @Singleton
    abstract fun bindDemoSettingsRepository(impl: DemoSettingsRepositoryImpl): DemoSettingsRepository

    @Binds
    @Singleton
    abstract fun bindLyricsDataSource(impl: DemoLyricsDataSource): LyricsDataSource

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
