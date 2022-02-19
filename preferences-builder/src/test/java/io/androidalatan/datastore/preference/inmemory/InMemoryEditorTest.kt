package io.androidalatan.datastore.preference.inmemory

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InMemoryEditorTest {

    private var cleared = false
    private val commitedValues = hashMapOf<String, Any?>()
    private val editor = InMemoryEditor { key, value, cleared ->
        if (cleared) {
            this.cleared = true
        } else {
            commitedValues[key] = value
        }
    }

    @Test
    fun putString() {
        val key = "KEY_1"
        val value = "VALUE_1"
        editor.putString(key, value)
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(value, editor.targets[key])
    }

    @Test
    fun putStringSet() {
        val key = "KEY_1"
        val value = "VALUE_1"
        editor.putStringSet(key, hashSetOf(value))
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(setOf(value), editor.targets[key])
    }

    @Test
    fun putInt() {
        val key = "KEY_1"
        val value = 1
        editor.putInt(key, value)
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(value, editor.targets[key])
    }

    @Test
    fun putLong() {
        val key = "KEY_1"
        val value = 1L
        editor.putLong(key, value)
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(value, editor.targets[key])
    }

    @Test
    fun putFloat() {
        val key = "KEY_1"
        val value = 1f
        editor.putFloat(key, value)
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(value, editor.targets[key])
    }

    @Test
    fun putBoolean() {
        val key = "KEY_1"
        val value = true
        editor.putBoolean(key, value)
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(value, editor.targets[key])
    }

    @Test
    fun remove() {
        val key = "KEY_1"
        editor.remove(key)
        Assertions.assertTrue(editor.targets.containsKey(key))
        Assertions.assertEquals(null, editor.targets[key])
    }

    @Test
    fun clear() {
        editor.clear()
        Assertions.assertTrue(editor.cleared)
    }

    @Test
    fun commit() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"

        val key2 = "KEY_2"
        val value2 = 2

        val key3 = "KEY_3"
        val value3 = 3L

        val key4 = "KEY_4"
        val value4 = 4f

        val key5 = "KEY_5"
        val value5 = true

        val committed = editor.putString(key1, value1)
            .putInt(key2, value2)
            .putLong(key3, value3)
            .putFloat(key4, value4)
            .putBoolean(key5, value5)
            .commit()

        Assertions.assertTrue(committed)
        Assertions.assertEquals(mapOf(
            key1 to value1,
            key2 to value2,
            key3 to value3,
            key4 to value4,
            key5 to value5,
        ), commitedValues)
    }

    @Test
    fun `commit with clear`() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"

        val key2 = "KEY_2"
        val value2 = 2

        val key3 = "KEY_3"
        val value3 = 3L

        val key4 = "KEY_4"
        val value4 = 4f

        val key5 = "KEY_5"
        val value5 = true

        val committed = editor.putString(key1, value1)
            .putInt(key2, value2)
            .putLong(key3, value3)
            .putFloat(key4, value4)
            .putBoolean(key5, value5)
            .clear()
            .commit()

        Assertions.assertTrue(committed)
        Assertions.assertTrue(cleared)
        Assertions.assertEquals(mapOf(
            key1 to value1,
            key2 to value2,
            key3 to value3,
            key4 to value4,
            key5 to value5,
        ), commitedValues)
    }

    @Test
    fun apply() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"

        val key2 = "KEY_2"
        val value2 = 2

        val key3 = "KEY_3"
        val value3 = 3L

        val key4 = "KEY_4"
        val value4 = 4f

        val key5 = "KEY_5"
        val value5 = true

        val committed = editor.putString(key1, value1)
            .putInt(key2, value2)
            .putLong(key3, value3)
            .putFloat(key4, value4)
            .putBoolean(key5, value5)
            .commit()

        Assertions.assertTrue(committed)
        Assertions.assertEquals(mapOf(
            key1 to value1,
            key2 to value2,
            key3 to value3,
            key4 to value4,
            key5 to value5,
        ), commitedValues)

    }

    @Test
    fun `apply with clear`() {
        val key1 = "KEY_1"
        val value1 = "VALUE_1"

        val key2 = "KEY_2"
        val value2 = 2

        val key3 = "KEY_3"
        val value3 = 3L

        val key4 = "KEY_4"
        val value4 = 4f

        val key5 = "KEY_5"
        val value5 = true

        val committed = editor.putString(key1, value1)
            .putInt(key2, value2)
            .putLong(key3, value3)
            .putFloat(key4, value4)
            .putBoolean(key5, value5)
            .clear()
            .commit()

        Assertions.assertTrue(committed)
        Assertions.assertTrue(cleared)
        Assertions.assertEquals(mapOf(
            key1 to value1,
            key2 to value2,
            key3 to value3,
            key4 to value4,
            key5 to value5,
        ), commitedValues)

    }

}