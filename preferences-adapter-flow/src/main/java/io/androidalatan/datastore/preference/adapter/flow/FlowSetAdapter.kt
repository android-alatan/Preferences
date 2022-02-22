package io.androidalatan.datastore.preference.adapter.flow

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

class FlowSetAdapter(private val coroutineContext: CoroutineContext) : SetAdapter {
    override fun acceptable(returnType: Type): Boolean {
        return returnType == Flow::class.java
    }

    override fun adapt(
        returnType: Type,
        sharedPreferences: SharedPreferences,
        valueObserver: ValueObserver,
        key: String,
        value: Any,
        updateValue: (SharedPreferences.Editor) -> Unit
    ): Any {
        return callbackFlow {
            val listener = registerForOnetime(sharedPreferences) {
                valueObserver.updateValue(key, value)
                trySend(true)
                close()
            }

            updateValue(sharedPreferences.edit())
            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.flowOn(coroutineContext)
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