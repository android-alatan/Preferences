package io.androidalatan.datastore.preference.adapter.api

interface ValueObserver {
    fun interface Callback {
        fun onUpdate(newValue: Any)
    }

    fun registerCallback(key: String, callback: Callback)
    fun unregisterCallback(key: String, callback: Callback)
    fun getValue(key: String): Any?
    fun putDefaultValue(key: String, defaultValue: Any)
    fun updateValue(key: String, newValue: Any?)
    fun clear()
}