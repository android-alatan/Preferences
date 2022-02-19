package io.androidalatan.datastore.preference.adapter.flow

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.CoroutineContext

class FlowClearAdapter(private val coroutineContext: CoroutineContext) : ClearAdapter {
    override fun acceptable(returnType: Class<*>): Boolean {
        return returnType == Flow::class.java
    }

    override fun adapt(returnType: Class<*>, sharedPreferences: SharedPreferences, valueObserver: ValueObserver): Any? {
        return callbackFlow {

            val listener = registerForOnetime(sharedPreferences) {
                valueObserver.clear()
                trySend(true)
                close()
            }
            sharedPreferences.edit()
                .clear()
                .apply()
            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.flowOn(coroutineContext)
    }

    private fun registerForOnetime(
        sharedPreferences: SharedPreferences,
        notified: (SharedPreferences) -> Unit = {}
    ): SharedPreferences.OnSharedPreferenceChangeListener {
        val onSharedPreferenceChangeListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
                if (sharedPreferences.all.isEmpty()) {
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
                    notified.invoke(sharedPreferences)
                }

            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        return onSharedPreferenceChangeListener
    }

}