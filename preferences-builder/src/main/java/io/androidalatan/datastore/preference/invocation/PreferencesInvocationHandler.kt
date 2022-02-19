package io.androidalatan.datastore.preference.invocation

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.annotations.getter.GetBoolean
import io.androidalatan.datastore.preference.annotations.getter.GetFloat
import io.androidalatan.datastore.preference.annotations.getter.GetInt
import io.androidalatan.datastore.preference.annotations.getter.GetLong
import io.androidalatan.datastore.preference.annotations.getter.GetObject
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.annotations.setter.Clear
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.invocator.Invoker
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.androidalatan.datastore.preference.invocator.clear.adapter.DefaultClearAdapter
import io.androidalatan.datastore.preference.invocator.get.adapter.DefaultGetAdapter
import io.androidalatan.datastore.preference.invocator.set.adapter.DefaultSetAdapter
import io.androidalatan.jsonparser.api.JsonParser
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class PreferencesInvocationHandler(
    preference: Lazy<SharedPreferences>,
    private val jsonParser: JsonParser?,
    setAdapterFactories: List<SetAdapter.Factory>,
    getAdapterFactories: List<GetAdapter.Factory>,
    clearAdapterFactories: List<ClearAdapter.Factory>
) : InvocationHandler {

    private val sharedPreferences by preference

    private val methodMap = hashMapOf<Method, Invoker>()
    private val valueObserver = ValueObserverImpl()
    private val defaultSetAdapter by lazy { DefaultSetAdapter() }
    private val defaultGetAdapter by lazy { DefaultGetAdapter(valueObserver) }
    private val defaultClearAdapter by lazy { DefaultClearAdapter() }
    private val setAdapters: List<SetAdapter> by lazy { setAdapterFactories.map { it.create() } }
    private val getAdapters: List<GetAdapter> by lazy { getAdapterFactories.map { it.create(valueObserver) } }
    private val clearAdapters: List<ClearAdapter> by lazy {clearAdapterFactories.map { it.create() }  }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {

        if (args?.size ?: 0 > 1) {
            throw IllegalArgumentException("${method.name} has ${args!!.size} arguments. but we don't support 1 more argument yet")
        }

        return methodMap.getOrPut(method) {
            val annotations = method.annotations
            annotations.firstOrNull() {
                PREDEFINED_ANNOTATIONS.contains(it.annotationClass)
            }
                ?.let {
                    Invoker.create(
                        sharedPreferences,
                        jsonParser,
                        valueObserver,
                        method,
                        it,
                        defaultSetAdapter,
                        setAdapters,
                        defaultGetAdapter,
                        getAdapters,
                        defaultClearAdapter,
                        clearAdapters
                    )
                } ?: throw IllegalArgumentException("${method.name} isn't allowed to invoke")
        }
            .execute(args)
    }

    companion object {
        val PREDEFINED_ANNOTATIONS = arrayOf(
            Set::class, GetBoolean::class, GetFloat::class, GetInt::class, GetLong::class, GetString::class, Clear::class, GetObject::class
        )
    }
}