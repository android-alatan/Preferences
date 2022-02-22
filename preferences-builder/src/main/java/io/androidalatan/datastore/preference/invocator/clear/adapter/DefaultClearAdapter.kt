package io.androidalatan.datastore.preference.invocator.clear.adapter

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver

class DefaultClearAdapter : ClearAdapter {
    override fun adapt(returnType: Class<*>, sharedPreferences: SharedPreferences, valueObserver: ValueObserver): Any? {
        sharedPreferences.edit()
            .clear()
            .apply()
        valueObserver.clear()

        return when (returnType) {
            Boolean::class.java -> true
            else -> Unit
        }
    }

    override fun acceptable(returnType: Class<*>): Boolean {
        return when (returnType) {
            Boolean::class.java,
            Unit::class.java,
            Void::class.java,
            Void.TYPE -> true
            else -> false
        }
    }
}