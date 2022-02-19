package io.androidalatan.datastore.preference.invocator.get

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.androidalatan.datastore.preference.invocator.get.adapter.DefaultGetAdapter
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetStringInvokerTest {
    private val pref = InMemoryPreference()
    private val valueObserver = ValueObserverImpl()

    private lateinit var invoker: GetStringInvoker

    @Test
    fun `execute Observable`() {
        val value = "hello"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleObservable" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Observable<*>)
        val testObserver = (executed as Observable<String>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNotComplete()

        val newValue = "world"
        valueObserver.updateValue(GetStringPrefTest.KEY_TITLE, newValue)

        testObserver.assertValueCount(2)
            .assertValueAt(1, newValue)
            .assertNotComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `execute Single`() {
        val value = "hello"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleSingle" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Single<*>)
        (executed as Single<String>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Maybe`() {
        val value = "hello"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleMaybe" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Maybe<*>)
        (executed as Maybe<String>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Flow`() {
        val value = "true"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleFlow" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(FlowGetAdapter(valueObserver, Dispatchers.Unconfined))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Flow<*>)

        (executed as Flow<String>)
            .turbine {
                Assertions.assertEquals("true", it.awaitItem())

                val newValue = "false"
                valueObserver.updateValue(GetBooleanPrefTest.KEY_TITLE, newValue)
                Assertions.assertEquals("false", it.awaitItem())

            }

    }

    @Test
    fun `execute String`() {
        val value = "hello"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleString" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), emptyList()
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is String)
        Assertions.assertEquals(value, executed as String)
    }

    @Test
    fun `execute void`() {
        val value = "hello"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleVoid" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), emptyList()
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }

    @Test
    fun `execute Completable`() {
        val value = "hello"
        pref.values[GetStringPrefTest.KEY_TITLE] = value

        val method = GetStringPrefTest::class.java.methods.first { it.name == "getTitleCompletable" }
        val annotation = method.getAnnotation(GetString::class.java)
        invoker = GetStringInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }

}

interface GetStringPrefTest {
    @GetString(KEY_TITLE)
    fun getTitleObservable(): Observable<String>

    @GetString(KEY_TITLE)
    fun getTitleSingle(): Single<String>

    @GetString(KEY_TITLE)
    fun getTitleMaybe(): Maybe<String>

    @GetString(KEY_TITLE)
    fun getTitleFlow(): Flow<String>

    @GetString(KEY_TITLE)
    fun getTitleString(): String

    // for error case
    @GetString(KEY_TITLE)
    fun getTitleVoid()

    // for error case
    @GetString(KEY_TITLE)
    fun getTitleCompletable(): Completable

    @Set(KEY_TITLE)
    fun setTitle(value: String): Boolean

    companion object {
        const val KEY_TITLE = "title"
    }
}