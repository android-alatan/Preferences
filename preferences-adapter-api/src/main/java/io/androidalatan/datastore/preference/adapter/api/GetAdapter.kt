package io.androidalatan.datastore.preference.adapter.api

interface GetAdapter {
    fun acceptable(returnType: Class<*>): Boolean

    fun <T> adapt(returnType: Class<*>, key: String, defaultValue: T): Any?

    fun interface Factory {
        fun create(valueObserver: ValueObserver): GetAdapter
    }
}