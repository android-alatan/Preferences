package io.androidalatan.datastore.preference.invocator.set.adapter

import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DefaultSetAdapterTest {

    private val adapter = DefaultSetAdapter()

    @Test
    fun isAcceptable() {
        Assertions.assertTrue(adapter.acceptable(Boolean::class.java))
        Assertions.assertFalse(adapter.acceptable(String::class.java))
    }

    @Test
    fun `convert boolean`() {
        val sharedPreferences = InMemoryPreference()
        val key = "key-1"
        val value = "value-1"
        val result = adapter.adapt(
            Boolean::class.java,
            sharedPreferences,
            ValueObserverImpl(),
            key,
            value
        ) { editor ->
            editor.putString(key, value)
                .apply()
        }
        Assertions.assertTrue(result is Boolean)
        Assertions.assertTrue(result as Boolean)
    }

    @Test
    fun `convert etc`() {
        val sharedPreferences = InMemoryPreference()
        val key = "key-1"
        val value = "value-1"
        val result = adapter.adapt(
            String::class.java,
            sharedPreferences,
            ValueObserverImpl(),
            key,
            value
        ) { editor ->
            editor.putString(key, value)
                .apply()
        }
        Assertions.assertTrue(result is Unit)
        Assertions.assertEquals(result, Unit)
    }
}