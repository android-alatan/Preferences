package io.androidalatan.datastore.preference.inmemory

import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InMemoryPreferenceTest {

    private val preference = InMemoryPreference()

    @Test
    fun getAll() {
        Assertions.assertEquals(emptyMap<String, Any>(), preference.all)

        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2
        preference.values[key2] = value2

        Assertions.assertEquals(mapOf<String, Any>(key1 to value1, key2 to value2), preference.all)
    }

    @Test
    fun getString() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2
        preference.values[key2] = value2

        Assertions.assertEquals(value1, preference.getString(key1, ""))
        Assertions.assertEquals("", preference.getString(key2, ""))
    }

    @Test
    fun getStringSet() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2
        preference.values[key2] = value2
        val key3 = "KEY_3"
        val value3 = setOf("hello", "world")
        preference.values[key3] = value3

        Assertions.assertEquals(value3, preference.getStringSet(key3, hashSetOf()))
        Assertions.assertEquals(emptySet<String>(), preference.getStringSet(key1, hashSetOf()))
    }

    @Test
    fun getInt() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2
        preference.values[key2] = value2
        val key3 = "KEY_3"
        val value3 = setOf("hello", "world")
        preference.values[key3] = value3

        Assertions.assertEquals(value2, preference.getInt(key2, 0))
        val defValue = 1
        Assertions.assertEquals(defValue, preference.getInt(key1, defValue))

    }

    @Test
    fun getLong() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2L
        preference.values[key2] = value2
        val key3 = "KEY_3"
        val value3 = setOf("hello", "world")
        preference.values[key3] = value3

        Assertions.assertEquals(value2, preference.getLong(key2, 0))
        val defValue = 3L
        Assertions.assertEquals(defValue, preference.getLong(key1, defValue))

    }

    @Test
    fun getFloat() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2f
        preference.values[key2] = value2
        val key3 = "KEY_3"
        val value3 = setOf("hello", "world")
        preference.values[key3] = value3

        Assertions.assertEquals(value2, preference.getFloat(key2, 0f))
        val defValue = 3f
        Assertions.assertEquals(defValue, preference.getFloat(key1, defValue))

    }

    @Test
    fun getBoolean() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = false
        preference.values[key2] = value2
        val key3 = "KEY_3"
        val value3 = setOf("hello", "world")
        preference.values[key3] = value3

        Assertions.assertEquals(value2, preference.getBoolean(key2, false))
        val defValue = true
        Assertions.assertEquals(defValue, preference.getBoolean(key1, defValue))
    }

    @Test
    fun contains() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"
        preference.values[key1] = value1
        val key2 = "KEY_2"
        val value2 = 2
        preference.values[key2] = value2
        val key3 = "KEY_3"
        val value3 = setOf("hello", "world")
        preference.values[key3] = value3

        Assertions.assertTrue(preference.contains(key1))
        Assertions.assertTrue(preference.contains(key2))
        Assertions.assertTrue(preference.contains(key3))
        Assertions.assertFalse(preference.contains("KEY_4"))
    }

    @Test
    fun edit() {
        Assertions.assertTrue(preference.edit() is InMemoryEditor)

        val key1 = "KEY_1"
        val key2 = "KEY_2"
        val key3 = "KEY_3"
        val value1 = true
        val value2 = "VALUE_2"
        val value3 = 3L
        preference.edit()
            .putBoolean(key1, value1)
            .putString(key2, value2)
            .putLong(key3, value3)
            .commit()

        Assertions.assertEquals(value1, preference.getBoolean(key1, false))
        Assertions.assertEquals(value2, preference.getString(key2, ""))
        Assertions.assertEquals(value3, preference.getLong(key3, 0L))
    }

    @Test
    fun `registerOnSharedPreferenceChangeListener and unregisterOnSharedPreferenceChangeListener`() {
        val key1 = "KEY_1"
        var changed = false
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == key1) {
                changed = true
            }
        }
        preference.registerOnSharedPreferenceChangeListener(listener)
        Assertions.assertEquals(1, preference.callbacks.size)
        Assertions.assertTrue(preference.callbacks.contains(listener))

        preference.edit()
            .putInt(key1, 3)
            .commit()
        Assertions.assertTrue(changed)

        preference.unregisterOnSharedPreferenceChangeListener(listener)
        Assertions.assertEquals(0, preference.callbacks.size)
    }

}