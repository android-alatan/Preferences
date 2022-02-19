package io.androidalatan.datastore.preference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocation.PreferencesInvocationHandler
import io.androidalatan.jsonparser.api.JsonParser
import java.lang.reflect.Proxy

class PreferencesBuilder(
    private val application: Application? = null,
) {

    internal var name = ""
    internal var mode = Context.MODE_PRIVATE
    internal var jsonParser: JsonParser? = null

    fun name(name: String): PreferencesBuilder {
        this.name = name
        return this
    }

    fun mode(mode: Int): PreferencesBuilder {
        this.mode = mode
        return this
    }

    fun jsonParser(jsonParser: JsonParser): PreferencesBuilder {
        this.jsonParser = jsonParser
        return this
    }

    fun <T> create(clazz: Class<T>): T {
        if (!clazz.isInterface) {
            throw IllegalArgumentException("${clazz.simpleName} should be interface")
        }

        val preference: Lazy<SharedPreferences> = if (application != null) {
            val actualName = name.takeIf { it.isNotEmpty() } ?: clazz.canonicalName
            lazy { application.getSharedPreferences(actualName, mode) }
        } else {
            // in case of test
            lazy { InMemoryPreference() }
        }

        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            clazz.classLoader, arrayOf(clazz),
            PreferencesInvocationHandler(
                preference,
                jsonParser,
                PreferencesBuilderInitializer.setAdapterFactories,
                PreferencesBuilderInitializer.getAdapterFactories,
                PreferencesBuilderInitializer.clearAdapterFactories
            )
        ) as T
    }
}