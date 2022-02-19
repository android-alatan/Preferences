package io.androidalatan.datastore.preference.invocator

import com.squareup.moshi.Moshi
import io.androidalatan.datastore.preference.adapter.flow.FlowClearAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowSetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxClearAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxSetAdapter
import io.androidalatan.datastore.preference.annotations.getter.GetBoolean
import io.androidalatan.datastore.preference.annotations.getter.GetFloat
import io.androidalatan.datastore.preference.annotations.getter.GetInt
import io.androidalatan.datastore.preference.annotations.getter.GetLong
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.annotations.setter.Clear
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.clear.ClearInvoker
import io.androidalatan.datastore.preference.invocator.clear.adapter.DefaultClearAdapter
import io.androidalatan.datastore.preference.invocator.get.GetBooleanInvoker
import io.androidalatan.datastore.preference.invocator.get.GetFloatInvoker
import io.androidalatan.datastore.preference.invocator.get.GetIntInvoker
import io.androidalatan.datastore.preference.invocator.get.GetLongInvoker
import io.androidalatan.datastore.preference.invocator.get.GetStringInvoker
import io.androidalatan.datastore.preference.invocator.get.adapter.DefaultGetAdapter
import io.androidalatan.datastore.preference.invocator.get.mock.MockPersonAdapter
import io.androidalatan.datastore.preference.invocator.set.SetInvoker
import io.androidalatan.datastore.preference.invocator.set.adapter.DefaultSetAdapter
import io.androidalatan.jsonparser.JsonParserImpl
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.reflect.Method
import kotlin.reflect.KClass

class InvokerTest {

    private val sharedPreferences = InMemoryPreference()
    private val valueObserver = ValueObserverImpl()
    private val jsonParser = JsonParserImpl(
        Moshi.Builder()
            .add(MockPersonAdapter())
            .build()
    )
    private val defaultSetAdapter = DefaultSetAdapter()
    private val setAdapters = listOf(FlowSetAdapter(Dispatchers.Unconfined), RxSetAdapter(Schedulers.trampoline()))
    private val defaultGetAdapter = DefaultGetAdapter(valueObserver)
    private val getAdapters =
        listOf(FlowGetAdapter(valueObserver, Dispatchers.Unconfined), RxGetAdapter(valueObserver, Schedulers.trampoline()))
    private val defaultClearAdapter = DefaultClearAdapter()
    private val clearAdapters = listOf(FlowClearAdapter(Dispatchers.Unconfined), RxClearAdapter(Schedulers.trampoline()))

    @Test
    fun `create set`() {

        val methodInfo = method(Set::class)
        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is SetInvoker)
    }

    @Test
    fun `create getInt`() {
        val methodInfo = method(GetInt::class)

        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is GetIntInvoker)
    }

    @Test
    fun `create getLong`() {
        val methodInfo = method(GetLong::class)

        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is GetLongInvoker)
    }

    @Test
    fun `create getFloat`() {
        val methodInfo = method(GetFloat::class)

        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is GetFloatInvoker)
    }

    @Test
    fun `create getBoolean`() {
        val methodInfo = method(GetBoolean::class)

        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is GetBooleanInvoker)
    }

    @Test
    fun `create getString`() {
        val methodInfo = method(GetString::class)

        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is GetStringInvoker)
    }

    @Test
    fun `create clear`() {
        val methodInfo = method(Clear::class)

        val invoker = Invoker.create(
            sharedPreferences,
            jsonParser,
            valueObserver,
            methodInfo.first,
            methodInfo.second,
            defaultSetAdapter, setAdapters,
            defaultGetAdapter, getAdapters,
            defaultClearAdapter, clearAdapters
        )
        Assertions.assertNotNull(invoker)
        Assertions.assertTrue(invoker is ClearInvoker)
    }

    private fun method(kClass: KClass<out Annotation>): Pair<Method, Annotation> {
        val method = InvokePrefTest::class.java.methods.first { method ->
            method.annotations.map { annotation -> annotation.annotationClass }
                .contains(kClass)
        }
        return method to method.getAnnotation(kClass.java)
    }
}

interface InvokePrefTest {
    @Set("set")
    fun set(title: String): Observable<Boolean>

    @GetInt("int")
    fun getInt(): Observable<Int>

    @GetLong("long")
    fun getLong(): Observable<Long>

    @GetBoolean("boolean")
    fun getBoolean(): Observable<Boolean>

    @GetFloat("float")
    fun getFloat(): Observable<Float>

    @GetString("string")
    fun getString(): Observable<String>

    @Clear
    fun clear()
}