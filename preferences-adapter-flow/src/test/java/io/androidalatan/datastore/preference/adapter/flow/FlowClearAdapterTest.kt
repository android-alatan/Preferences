package io.androidalatan.datastore.preference.adapter.flow

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FlowClearAdapterTest {

    @Test
    fun acceptable() {
        FlowClearAdapter(Dispatchers.Unconfined).apply {
            Assertions.assertFalse(acceptable(Boolean::class.java))
            Assertions.assertTrue(acceptable(Flow::class.java))
        }
    }

    @Test
    fun adapt() {
        val sharedPreferences = InMemoryPreference()
        sharedPreferences.edit()
            .putBoolean("key-1", true)
            .apply()
        (FlowClearAdapter(Dispatchers.Unconfined)
            .adapt(Flow::class.java, sharedPreferences, ValueObserverImpl()) as Flow<Boolean>)
            .turbine { flowTurbine ->
                Assertions.assertTrue(flowTurbine.awaitItem())
                flowTurbine.awaitComplete()
            }
    }
}