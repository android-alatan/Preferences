package io.androidalatan.datastore.preference.invocator.clear.adapter

import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DefaultClearAdapterTest {

    @Test
    fun `adapt for boolean`() {
        val retValue = clearAdapter.adapt(Boolean::class.java, InMemoryPreference(), ValueObserverImpl())
        Assertions.assertEquals(retValue, true)
    }

    @Test
    fun `adapt for Unit`() {
        val retValue = clearAdapter.adapt(Unit::class.java, InMemoryPreference(), ValueObserverImpl())
        Assertions.assertEquals(retValue, Unit)
    }

    private val clearAdapter = DefaultClearAdapter()

    @Test
    fun acceptable() {
        Assertions.assertFalse(clearAdapter.acceptable(String::class.java))
        Assertions.assertTrue(clearAdapter.acceptable(Boolean::class.java))
        Assertions.assertTrue(clearAdapter.acceptable(Unit::class.java))
        Assertions.assertTrue(clearAdapter.acceptable(Void::class.java))
        Assertions.assertTrue(clearAdapter.acceptable(Void.TYPE))
    }
}