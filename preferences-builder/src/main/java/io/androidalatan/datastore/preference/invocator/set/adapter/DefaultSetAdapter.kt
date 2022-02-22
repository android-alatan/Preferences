package io.androidalatan.datastore.preference.invocator.set.adapter

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import java.lang.reflect.Type

internal class DefaultSetAdapter : SetAdapter {
    override fun acceptable(returnType: Type): Boolean = when (returnType) {
        Boolean::class.java,
        Unit::class.java,
        Void::class.java,
        Void.TYPE -> true
        else -> false
    }

    override fun adapt(
        returnType: Type,
        sharedPreferences: SharedPreferences,
        valueObserver: ValueObserver,
        key: String,
        value: Any,
        updateValue: (SharedPreferences.Editor) -> Unit
    ): Any {
        return when (returnType) {
            Boolean::class.java -> {
                registerForOnetime(sharedPreferences) { valueObserver.updateValue(key, value) }
                updateValue(sharedPreferences.edit())
                true
            }
            else -> {
                registerForOnetime(sharedPreferences) { valueObserver.updateValue(key, value) }
                updateValue(sharedPreferences.edit())
            }
        }
    }

    private fun registerForOnetime(
        sharedPreferences: SharedPreferences,
        notified: () -> Unit
    ): SharedPreferences.OnSharedPreferenceChangeListener {
        val onSharedPreferenceChangeListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
                notified.invoke()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        return onSharedPreferenceChangeListener
    }
}