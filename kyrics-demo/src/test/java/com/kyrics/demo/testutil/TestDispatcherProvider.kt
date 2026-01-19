package com.kyrics.demo.testutil

import com.kyrics.demo.domain.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

/**
 * Test implementation of [DispatcherProvider] that uses [StandardTestDispatcher]
 * for all dispatchers, allowing tests to control coroutine execution.
 */
class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
