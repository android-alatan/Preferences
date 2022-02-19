package io.androidalatan.datastore.preference.inmemory

import android.content.SharedPreferences

class InMemoryPreference : SharedPreferences {

    internal val values = hashMapOf<String, Any>()
    internal val callbacks = mutableListOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getAll(): MutableMap<String, *> {
        return values
    }

    override fun getString(key: String, defValue: String?): String {
        return values[key] as? String ?: defValue ?: ""
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String> {
        @Suppress("UNCHECKED_CAST")
        return values[key] as? MutableSet<String> ?: defValues ?: hashSetOf()
    }

    override fun getInt(key: String, defValue: Int): Int {
        return values[key] as? Int ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        return values[key] as? Long ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return values[key] as? Float ?: defValue
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return values[key] as? Boolean ?: defValue
    }

    override fun contains(key: String): Boolean {
        return values.containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return InMemoryEditor { key, value, cleared ->
            if (cleared) {
                val keySet = HashSet(values.keys)
                values.clear()
                keySet.forEach { deletedKey ->
                    val arrayList = ArrayList(callbacks)
                    arrayList.forEach { it.onSharedPreferenceChanged(this, deletedKey) }
                }
            } else {
                value?.let {
                    values[key] = it
                } ?: kotlin.run { values.remove(key) }
                val arrayList = ArrayList(callbacks)
                arrayList.forEach { it.onSharedPreferenceChanged(this, key) }
            }

        }
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        callbacks.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        callbacks.remove(listener)
    }
}