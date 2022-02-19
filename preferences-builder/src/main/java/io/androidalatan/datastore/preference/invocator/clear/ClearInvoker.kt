package io.androidalatan.datastore.preference.invocator.clear

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.invocator.Invoker
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import java.lang.reflect.Method

class ClearInvoker(
    private val sharedPreferences: SharedPreferences,
    private val method: Method,
    private val valueObserver: ValueObserver,
    private val defaultClearAdapter: ClearAdapter,
    private val clearAdapters: List<ClearAdapter>
) : Invoker {
    override fun execute(args: Array<Any>?): Any? {
        return (clearAdapters.firstOrNull { it.acceptable(method.returnType) } ?: defaultClearAdapter)
            .adapt(method.returnType, sharedPreferences, valueObserver)
    }
}