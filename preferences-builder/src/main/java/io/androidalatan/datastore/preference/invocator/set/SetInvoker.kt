package io.androidalatan.datastore.preference.invocator.set

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.invocator.Invoker
import io.androidalatan.jsonparser.api.JsonParser
import java.lang.reflect.Method

class SetInvoker(
    private val sharedPreferences: SharedPreferences,
    private val method: Method,
    private val jsonParser: JsonParser?,
    private val annotate: Set,
    private val valueObserver: ValueObserver,
    private val defaultSetAdapter: SetAdapter,
    private val setAdapters: List<SetAdapter>
) : Invoker {

    private val key: String by lazy { annotate.name }

    override fun execute(args: Array<Any>?): Any? {

        val argument = args!![0]
        return (setAdapters.firstOrNull { it.acceptable(method.returnType) } ?: defaultSetAdapter)
            .adapt(method.returnType, sharedPreferences, valueObserver, key, argument) { editor ->
                updateValue(
                    argument,
                    editor,
                    jsonParser
                )
            }

    }

    private fun updateValue(
        argument: Any,
        editor: SharedPreferences.Editor,
        jsonParser: JsonParser?
    ) {
        when (argument) {
            is String -> {
                editor.putString(key, argument)
            }
            is Long -> {
                editor.putLong(key, argument)
            }
            is Int -> {
                editor.putInt(key, argument)
            }
            is Float -> {
                editor.putFloat(key, argument)
            }
            is Boolean -> {
                editor.putBoolean(key, argument)
            }
            is Array<*> -> {
                @Suppress("UNCHECKED_CAST")
                val stringSet = argument.filter { it is String }
                    .map { it }
                    .toSet() as kotlin.collections.Set<String>
                editor.putStringSet(key, stringSet)
            }
            else -> {
                requireNotNull(jsonParser) {
                    "It only accepts Primitive type or ${argument::class.java.simpleName} should support JsonParser"
                }
                editor.putString(key, jsonParser.toJson(argument))
            }
        }.apply()
    }
}