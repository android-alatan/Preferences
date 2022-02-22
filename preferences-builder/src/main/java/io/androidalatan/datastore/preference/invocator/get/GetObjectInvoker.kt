package io.androidalatan.datastore.preference.invocator.get

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.annotations.getter.DefaultObject
import io.androidalatan.datastore.preference.annotations.getter.GetObject
import io.androidalatan.datastore.preference.invocator.Invoker
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.androidalatan.jsonparser.api.JsonParser
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

class GetObjectInvoker(
    private val sharedPreferences: SharedPreferences,
    private val method: Method,
    private val jsonParser: JsonParser,
    private val valueObserver: ValueObserver,
    private val annotate: GetObject,
    private val defaultGetAdapter: GetAdapter,
    private val getAdapters: List<GetAdapter>
) : Invoker {

    private val key by lazy { annotate.name }

    override fun execute(args: Array<Any>?): Any? {
        val type = method.returnType
        val rawType = getRealRawType(method)

        initDefaultValueIfNoExists(rawType) {
            val index = method.parameterAnnotations.indexOfFirst { annotations ->
                annotations.any { annotation -> annotation.annotationClass == DefaultObject::class }
            }
            if (index >= 0) {
                args?.get(index)
            } else {
                null
            }
        }

        return (getAdapters.firstOrNull { it.acceptable(type) } ?: defaultGetAdapter)
            .adapt(type, key, valueObserver.getValue(key))
    }

    private fun initDefaultValueIfNoExists(rawType: Class<*>, defaultValueInvoker: () -> Any?) {
        valueObserver.getValue(key) ?: getValueFromSharedPreference(sharedPreferences)
            .let { savedValue ->
                val defaultValue = defaultValueInvoker()
                if (defaultValue != null) {
                    valueObserver.putDefaultValue(key, defaultValue)
                }

                if (savedValue.isNotEmpty()) {
                    try {
                        valueObserver.updateValue(key, jsonParser.fromJson(savedValue, rawType))
                    } catch (t: Throwable) {
                        throw IllegalStateException("$key is wrong json string")
                    }
                } else {
                    valueObserver.updateValue(key, null)
                }
            }
    }

    private fun getValueFromSharedPreference(sharedPreferences: SharedPreferences): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    private fun getRealRawType(method: Method): Class<*> {

        return if (method.genericReturnType is ParameterizedType) {
            (method.genericReturnType as ParameterizedType)
                .actualTypeArguments[0]
        } else {
            method.returnType
        }.let { jsonParser.getRawType(it) }
    }
}