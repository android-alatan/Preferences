package io.androidalatan.datastore.preference.adapter.flow

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FlowGetAdapterTest {

    @Test
    fun acceptable() {
        FlowGetAdapter(ValueObserverImpl(), Dispatchers.Unconfined).apply {
            Assertions.assertTrue(acceptable(Flow::class.java))
            Assertions.assertFalse(acceptable(Boolean::class.java))
        }

    }

    @Test
    fun adapt() {
        val valueObserver = ValueObserverImpl()
        val key = "key-1"
        val defaultValue = "defaultValue"
        (FlowGetAdapter(valueObserver, Dispatchers.Unconfined)
            .adapt(Flow::class.java, key, defaultValue) as Flow<String>)
            .turbine { flowTurbine ->
                Assertions.assertEquals(flowTurbine.awaitItem(), defaultValue)

                val newValue = "newValue"
                valueObserver.updateValue(key, newValue)

                Assertions.assertEquals(flowTurbine.awaitItem(), newValue)

                flowTurbine.expectNoEvents()
                flowTurbine.cancelAndIgnoreRemainingEvents()
            }
    }
}