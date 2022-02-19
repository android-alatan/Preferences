package io.androidalatan.coroutine.test

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@ExperimentalCoroutinesApi
inline fun <T> flowTest(flow: Flow<T>, crossinline body: suspend TestCoroutineScope.(FlowTurbine<T>) -> Unit) {
    val scope = TestCoroutineScope()
    scope.launch {
        flow.test {
            scope.body(this)
        }
    }

    scope.cleanupTestCoroutines()
}

@OptIn(ExperimentalTime::class)
@ExperimentalCoroutinesApi
fun <T> Flow<T>.turbine(body: suspend TestCoroutineScope.(FlowTurbine<T>) -> Unit) = flowTest(this, body)