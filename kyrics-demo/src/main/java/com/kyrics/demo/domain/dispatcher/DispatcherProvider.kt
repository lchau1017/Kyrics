package com.kyrics.demo.domain.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface for providing coroutine dispatchers.
 * This abstraction allows for easy testing by swapping dispatchers in tests.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}
