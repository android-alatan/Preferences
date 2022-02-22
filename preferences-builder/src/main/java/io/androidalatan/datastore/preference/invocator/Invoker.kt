package io.androidalatan.datastore.preference.invocator

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.androidalatan.datastore.preference.annotations.getter.GetBoolean
import io.androidalatan.datastore.preference.annotations.getter.GetFloat
import io.androidalatan.datastore.preference.annotations.getter.GetInt
import io.androidalatan.datastore.preference.annotations.getter.GetLong
import io.androidalatan.datastore.preference.annotations.getter.GetObject
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.annotations.setter.Clear
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.invocator.clear.ClearInvoker
import io.androidalatan.datastore.preference.invocator.get.GetInvoker
import io.androidalatan.datastore.preference.invocator.set.SetInvoker
import io.androidalatan.jsonparser.api.JsonParser
import java.lang.reflect.Method

interface Invoker {

    fun execute(args: Array<Any>?): Any?

    companion object {
        fun create(
            sharedPreferences: SharedPreferences,
            jsonParser: JsonParser?,
            valueObserver: ValueObserver,
            method: Method,
            annotation: Annotation,
            defaultSetAdapter: SetAdapter,
            setAdapters: List<SetAdapter>,
            defaultGetAdapter: GetAdapter,
            getAdapters: List<GetAdapter>,
            defaultClearAdapter: ClearAdapter,
            clearAdapters: List<ClearAdapter>
        ): Invoker {

            return when (annotation) {
                is Set -> SetInvoker(sharedPreferences, method, jsonParser, annotation, valueObserver, defaultSetAdapter, setAdapters)
                is GetBoolean -> GetInvoker.boolean(sharedPreferences, method, valueObserver, annotation, defaultGetAdapter, getAdapters)
                is GetInt -> GetInvoker.int(sharedPreferences, method, valueObserver, annotation, defaultGetAdapter, getAdapters)
                is GetLong -> GetInvoker.long(sharedPreferences, method, valueObserver, annotation, defaultGetAdapter, getAdapters)
                is GetFloat -> GetInvoker.float(sharedPreferences, method, valueObserver, annotation, defaultGetAdapter, getAdapters)
                is GetString -> GetInvoker.string(sharedPreferences, method, valueObserver, annotation, defaultGetAdapter, getAdapters)
                is GetObject -> {
                    requireNotNull(jsonParser) { "Json Parser should be not null" }
                    GetInvoker.obj(sharedPreferences, method, jsonParser, valueObserver, annotation, defaultGetAdapter, getAdapters)
                }
                is Clear -> ClearInvoker(sharedPreferences, method, valueObserver, defaultClearAdapter, clearAdapters)
                else -> throw IllegalAccessException("${method.name} has wrong declaration with ${annotation.annotationClass}")
            }
        }
    }
}