package io.androidalatan.datastore.preference.adapter.api

import android.content.SharedPreferences
import java.lang.reflect.Type

interface SetAdapter {
    fun acceptable(returnType: Type): Boolean
    fun adapt(
        returnType: Type,
        sharedPreferences: SharedPreferences,
        valueObserver: ValueObserver,
        key: String,
        value: Any,
        updateValue: (SharedPreferences.Editor) -> Unit
    ): Any

    fun interface Factory {
        fun create(): SetAdapter
    }
}