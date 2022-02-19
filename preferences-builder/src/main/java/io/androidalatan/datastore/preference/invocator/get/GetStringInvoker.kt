package io.androidalatan.datastore.preference.invocator.get

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import java.lang.reflect.Method

class GetStringInvoker(
    sharedPreferences: SharedPreferences,
    method: Method,
    valueObserver: ValueObserver,
    private val annotate: GetString,
    defaultGetAdapter: GetAdapter,
    getAdapters: List<GetAdapter>
) : GetInvoker<String>(sharedPreferences, valueObserver, method, defaultGetAdapter, getAdapters) {

    override val key by lazy { annotate.name }
    override val defaultValue by lazy { if (!annotate.disable) annotate.defaultValue else annotate.disableValue }

    override fun getValueFromSharedPreference(sharedPreferences: SharedPreferences): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun acceptableType(returnType: Class<*>): Boolean {
        return returnType == String::class.java
    }
}