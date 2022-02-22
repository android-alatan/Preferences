package io.androidalatan.datastore.preference.invocator.get

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.androidalatan.datastore.preference.annotations.getter.GetBoolean
import io.androidalatan.datastore.preference.annotations.getter.GetFloat
import io.androidalatan.datastore.preference.annotations.getter.GetInt
import io.androidalatan.datastore.preference.annotations.getter.GetLong
import io.androidalatan.datastore.preference.annotations.getter.GetObject
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.invocator.Invoker
import io.androidalatan.jsonparser.api.JsonParser
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

abstract class GetInvoker<T : Any>(
    private val sharedPreferences: SharedPreferences,
    private val valueObserver: ValueObserver,
    private val method: Method,
    private val defaultGetAdapter: GetAdapter,
    private val getAdapters: List<GetAdapter>,
) : Invoker {

    abstract val key: String
    abstract val defaultValue: T

    @Throws(IllegalStateException::class)
    override fun execute(args: Array<Any>?): Any? {

        initDefaultValueIfNoExists()

        val returnType = method.returnType

        return getAdapters.firstOrNull { it.acceptable(returnType) }
            ?.let {
                verifyParameterType(method)
                it.adapt(returnType, key, defaultValue)
            } ?: kotlin.run {

            if (defaultGetAdapter.acceptable(returnType)) {
                return defaultGetAdapter.adapt(returnType, key, defaultValue)
            }

            val simpleName = defaultValue::class.java.simpleName
            throw IllegalStateException(
                """${method.name} has different return type : $returnType. 
                    |we only allow $simpleName, 
                    |Single<$simpleName>, 
                    |Maybe<$simpleName>, 
                    |Observable<$simpleName> 
                    |Flow<$simpleName>
                    |for Get$simpleName""".trimMargin()
            )
        }
    }

    abstract fun acceptableType(returnType: Class<*>): Boolean

    private fun initDefaultValueIfNoExists() {
        valueObserver.getValue(key) ?: getValueFromSharedPreference(sharedPreferences)
            .apply {
                valueObserver.putDefaultValue(key, defaultValue)
                valueObserver.updateValue(key, this)
            }
    }

    abstract fun getValueFromSharedPreference(sharedPreferences: SharedPreferences): T

    private fun verifyParameterType(method: Method) {
        if ((method.genericReturnType as ParameterizedType)
                .actualTypeArguments[0] != defaultValue::class.java) {
            val simpleName = defaultValue::class.java.simpleName
            throw IllegalStateException(
                """${method.name} has different return Generic type : ${method.returnType}. 
                |we only allow $simpleName, 
                |Single<$simpleName>, 
                |Maybe<$simpleName>, 
                |Observable<$simpleName> 
                |for Get$simpleName""".trimMargin()
            )
        }
    }

    companion object {
        fun int(
            sharedPreferences: SharedPreferences,
            method: Method,
            valueObserver: ValueObserver,
            annotate: GetInt,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>
        ): GetInvoker<Int> {
            return GetIntInvoker(sharedPreferences, method, valueObserver, annotate, defaultGetAdapter, getAdapters)
        }

        fun boolean(
            sharedPreferences: SharedPreferences,
            method: Method,
            valueObserver: ValueObserver,
            annotate: GetBoolean,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>
        ): GetInvoker<Boolean> {
            return GetBooleanInvoker(sharedPreferences, method, valueObserver, annotate, defaultGetAdapter, getAdapters)
        }

        fun long(
            sharedPreferences: SharedPreferences,
            method: Method,
            valueObserver: ValueObserver,
            annotate: GetLong,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>
        ): GetInvoker<Long> {
            return GetLongInvoker(sharedPreferences, method, valueObserver, annotate, defaultGetAdapter, getAdapters)
        }

        fun float(
            sharedPreferences: SharedPreferences,
            method: Method,
            valueObserver: ValueObserver,
            annotate: GetFloat,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>
        ): GetInvoker<Float> {
            return GetFloatInvoker(sharedPreferences, method, valueObserver, annotate, defaultGetAdapter, getAdapters)
        }

        fun string(
            sharedPreferences: SharedPreferences,
            method: Method,
            valueObserver: ValueObserver,
            annotate: GetString,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>
        ): GetInvoker<String> {
            return GetStringInvoker(sharedPreferences, method, valueObserver, annotate, defaultGetAdapter, getAdapters)
        }

        fun obj(
            sharedPreferences: SharedPreferences,
            method: Method,
            jsonParser: JsonParser,
            valueObserver: ValueObserver,
            annotate: GetObject,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>
        ): Invoker {
            return GetObjectInvoker(sharedPreferences, method, jsonParser, valueObserver, annotate, defaultGetAdapter, getAdapters)
        }
    }
}