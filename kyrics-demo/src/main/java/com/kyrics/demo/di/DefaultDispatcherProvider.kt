package com.kyrics.demo.di

import com.kyrics.demo.domain.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [DispatcherProvider] using standard Android dispatchers.
 */
@Singleton
class DefaultDispatcherProvider
    @Inject
    constructor() : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val default: CoroutineDispatcher = Dispatchers.Default
    }
