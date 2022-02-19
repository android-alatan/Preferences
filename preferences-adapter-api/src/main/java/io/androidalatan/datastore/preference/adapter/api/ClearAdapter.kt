package io.androidalatan.datastore.preference.adapter.api

import android.content.SharedPreferences

interface ClearAdapter {
    fun acceptable(returnType: Class<*>): Boolean
    fun adapt(returnType: Class<*>, sharedPreferences: SharedPreferences, valueObserver: ValueObserver): Any?

    fun interface Factory {
        fun create(): ClearAdapter
    }
}