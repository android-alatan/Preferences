package io.androidalatan.datastore.preference.invocator.get.adapter

import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver

internal class DefaultGetAdapter(
    private val valueObserver: ValueObserver
) : GetAdapter {
    override fun acceptable(returnType: Class<*>): Boolean = when (returnType) {
        Unit::class.java,
        Void::class.java,
        Void.TYPE -> false
        else -> true
    }

    override fun <T> adapt(returnType: Class<*>, key: String, defaultValue: T): Any? {
        return valueObserver.getValue(key) ?: defaultValue
    }
}