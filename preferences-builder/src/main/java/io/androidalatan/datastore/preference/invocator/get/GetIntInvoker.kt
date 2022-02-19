package io.androidalatan.datastore.preference.invocator.get

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.annotations.getter.GetInt
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import java.lang.reflect.Method

class GetIntInvoker(
    sharedPreferences: SharedPreferences,
    method: Method,
    valueObserver: ValueObserver,
    private val annotate: GetInt,
    defaultGetAdapter: GetAdapter,
    getAdapters: List<GetAdapter>
) : GetInvoker<Int>(sharedPreferences, valueObserver, method, defaultGetAdapter, getAdapters) {

    override val key by lazy { annotate.name }
    override val defaultValue by lazy { if (!annotate.disable) annotate.defaultValue else annotate.disableValue }

    override fun getValueFromSharedPreference(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun acceptableType(returnType: Class<*>): Boolean {
        return returnType == Integer.TYPE
    }
}