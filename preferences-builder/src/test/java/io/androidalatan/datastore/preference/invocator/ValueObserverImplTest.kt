package io.androidalatan.datastore.preference.invocator

import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ValueObserverImplTest {

    private val observer = ValueObserverImpl()

    @Test
    fun `getValue and update`() {
        val key = KEY_1
        Assertions.assertNull(observer.getValue(key))

        val value = VALUE_1
        observer.updateValue(key, value)
        Assertions.assertEquals(value, observer.getValue(key))
    }

    @Test
    fun putDefaultValue() {
        val key = KEY_1
        val value = VALUE_1
        Assertions.assertNull(observer.defaultMap[key])
        observer.putDefaultValue(key, value)
        Assertions.assertEquals(observer.defaultMap[key], value)
    }

    @Test
    fun `registerCallback update unregisterCallback`() {
        val key = KEY_1
        val value = VALUE_1
        val defaultValue = "${VALUE_1}-1"
        observer.map[key] = value
        observer.defaultMap[key] = defaultValue
        var latestValue: Any? = null
        val callback = ValueObserver.Callback { newValue ->
            latestValue = newValue
        }

        Assertions.assertNull(observer.callbacks[key])

        observer.registerCallback(key, callback)
        Assertions.assertNotNull(observer.callbacks[key])
        Assertions.assertTrue(observer.callbacks[key]!!.isNotEmpty())
        Assertions.assertEquals(latestValue, value)

        val newValue = "${VALUE_1}2"
        observer.updateValue(key, newValue)
        Assertions.assertEquals(latestValue, newValue)

        observer.unregisterCallback(key, callback)
        Assertions.assertTrue(observer.callbacks[key]!!.isEmpty())
    }

    @Test
    fun clear() {
        val key = KEY_1
        val value = VALUE_1
        val defaultValue = "${VALUE_1}-1"
        observer.map[key] = value
        observer.defaultMap[key] = defaultValue
        var latestValue: Any? = null
        observer.registerCallback(key) { newValue ->
            latestValue = newValue
        }
        Assertions.assertEquals(latestValue, value)
        observer.clear()
        Assertions.assertEquals(latestValue, defaultValue)
        Assertions.assertNull(observer.map[key])
        Assertions.assertEquals(observer.defaultMap[key], defaultValue)
    }

    companion object {
        const val KEY_1 = "key-1"
        const val KEY_2 = "key-2"
        const val VALUE_1 = "value-1"
        const val VALUE_2 = 12341
    }
}