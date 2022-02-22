package io.androidalatan.datastore.preference.inmemory

import android.content.SharedPreferences

class InMemoryEditor(private val callback: (String, Any?, Boolean) -> Unit) : SharedPreferences.Editor {
    internal val targets = hashMapOf<String, Any?>()
    internal var cleared = false
    override fun putString(key: String, value: String?): SharedPreferences.Editor {
        targets[key] = value ?: ""
        return this
    }

    override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor {
        targets[key] = values ?: emptySet<String>()
        return this
    }

    override fun putInt(key: String, value: Int): SharedPreferences.Editor {
        targets[key] = value
        return this
    }

    override fun putLong(key: String, value: Long): SharedPreferences.Editor {
        targets[key] = value
        return this
    }

    override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
        targets[key] = value
        return this
    }

    override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
        targets[key] = value
        return this
    }

    override fun remove(key: String): SharedPreferences.Editor {
        targets[key] = null
        return this
    }

    override fun clear(): SharedPreferences.Editor {
        cleared = true
        return this
    }

    override fun commit(): Boolean {
        if (cleared) {
            callback.invoke("", null, true)
        }
        targets.forEach { entry: Map.Entry<String, Any?> ->
            callback.invoke(entry.key, entry.value, false)
        }
        targets.clear()
        return true
    }

    override fun apply() {
        commit()
    }
}