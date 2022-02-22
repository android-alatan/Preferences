package io.androidalatan.datastore.preference.adapter.flow

import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class FlowGetAdapter(
    private val valueObserver: ValueObserver,
    private val coroutineContext: CoroutineContext
) : GetAdapter {
    override fun acceptable(returnType: Class<*>): Boolean {
        return when (returnType) {
            Flow::class.java -> true
            else -> false
        }
    }

    override fun <T> adapt(returnType: Class<*>, key: String, defaultValue: T): Any? {
        return valueObserver.asFlow(key, defaultValue)
            .flowOn(coroutineContext)
    }

    private fun <T> ValueObserver.asFlow(key: String, defaultValue: T): Flow<T> {
        return callbackFlow {
            val callback = ValueObserver.Callback {
                trySend(it as T)
            }
            registerCallback(key, callback = callback)
            getValue(key) ?: defaultValue?.let { trySend(defaultValue) }
            awaitClose {
                unregisterCallback(key, callback)
            }
        }
    }
}