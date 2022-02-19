package io.androidalatan.datastore.preference.invocator.get

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.annotations.getter.GetBoolean
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import java.lang.reflect.Method

class GetBooleanInvoker(
    sharedPreferences: SharedPreferences,
    method: Method,
    valueObserver: ValueObserver,
    private val annotate: GetBoolean,
    defaultGetAdapter: GetAdapter,
    getAdapters: List<GetAdapter>
) : GetInvoker<Boolean>(sharedPreferences, valueObserver, method, defaultGetAdapter, getAdapters) {

    override val key: String by lazy { annotate.name }
    override val defaultValue: Boolean by lazy { if (!annotate.disable) annotate.defaultValue else annotate.disableValue }
    override fun acceptableType(returnType: Class<*>): Boolean {
        return returnType == java.lang.Boolean.TYPE
    }

    override fun getValueFromSharedPreference(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}