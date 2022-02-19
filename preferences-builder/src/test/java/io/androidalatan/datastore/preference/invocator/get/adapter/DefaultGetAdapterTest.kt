package io.androidalatan.datastore.preference.invocator.get.adapter

import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DefaultGetAdapterTest {

    private val valueObserver = ValueObserverImpl()
    private val getAdapter = DefaultGetAdapter(valueObserver)

    @Test
    fun acceptable() {
        Assertions.assertTrue(getAdapter.acceptable(Boolean::class.java))
        Assertions.assertTrue(getAdapter.acceptable(String::class.java))

        Assertions.assertFalse(getAdapter.acceptable(Unit::class.java))
        Assertions.assertFalse(getAdapter.acceptable(Void::class.java))
        Assertions.assertFalse(getAdapter.acceptable(Void.TYPE))
    }

    @Test
    fun adapt() {
        val key = "key-1"
        valueObserver.updateValue(key, false)
        Assertions.assertFalse(getAdapter.adapt(Boolean::class.java, key, true) as Boolean)
    }
}