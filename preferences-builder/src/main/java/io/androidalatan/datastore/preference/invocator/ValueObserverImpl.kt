package io.androidalatan.datastore.preference.invocator

import androidx.annotation.VisibleForTesting
import io.androidalatan.datastore.preference.adapter.api.ValueObserver

class ValueObserverImpl : ValueObserver {
    @VisibleForTesting internal val map = hashMapOf<String, Any>()
    @VisibleForTesting internal val defaultMap = hashMapOf<String, Any>()

    private val lock = Any()
    @VisibleForTesting internal val callbacks = hashMapOf<String, MutableList<ValueObserver.Callback>>()

    override fun registerCallback(key: String, callback: ValueObserver.Callback) {
        synchronized(lock) {
            val callbacksOfKey = if (!callbacks.containsKey(key)) {
                mutableListOf<ValueObserver.Callback>().apply {
                    callbacks[key] = this
                }
            } else {
                callbacks[key]!!
            }
            callbacksOfKey.add(callback)

            getValue(key)?.let {
                callback.onUpdate(it)
            }
        }
    }

    override fun unregisterCallback(key: String, callback: ValueObserver.Callback) {
        synchronized(lock) {
            callbacks[key]?.remove(callback)
        }
    }

    override fun getValue(key: String): Any? {
        return map[key] ?: defaultMap[key]
    }

    override fun putDefaultValue(key: String, defaultValue: Any) {
        defaultMap[key] = defaultValue
    }

    override fun updateValue(key: String, newValue: Any?) {
        newValue?.let {
            if (newValue != map[key]) {
                map[key] = newValue

                callbacks.filter { it.key == key }
                    .flatMap { it.value }
                    .forEach { callback ->
                        callback.onUpdate(newValue)
                    }

            }
        } ?: kotlin.run {
            map.remove(key)
            callbacks.filter { it.key == key }
                .flatMap { it.value }
                .forEach { callback ->
                    defaultMap[key]?.let {
                        callback.onUpdate(it)
                    }
                }
        }
    }

    override fun clear() {
        map.clear()
        callbacks.keys.forEach { callbackKey ->
            callbacks[callbackKey]?.forEach { callback ->
                defaultMap[callbackKey]?.let { defaultValue -> callback.onUpdate(defaultValue) }
            }
        }
    }
}