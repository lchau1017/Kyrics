package com.kyrics.demo.di

import com.kyrics.demo.data.repository.DemoSettingsRepositoryImpl
import com.kyrics.demo.data.repository.LyricsRepositoryImpl
import com.kyrics.demo.domain.dispatcher.DispatcherProvider
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import com.kyrics.demo.domain.repository.LyricsRepository
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
    abstract fun bindLyricsRepository(impl: LyricsRepositoryImpl): LyricsRepository

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
