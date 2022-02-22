package io.androidalatan.datastore.preference.invocator.get

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.annotations.getter.GetLong
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

class GetLongInvokerTest {

    private val pref = InMemoryPreference()
    private val valueObserver = ValueObserverImpl()

    private lateinit var invoker: GetLongInvoker

    @Test
    fun `execute Observable`() {
        val value = 312L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleObservable" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Observable<*>)
        val testObserver = (executed as Observable<Long>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNotComplete()

        val newValue = 34L
        valueObserver.updateValue(GetLongPrefTest.KEY_TITLE, newValue)

        testObserver.assertValueCount(2)
            .assertValueAt(1, newValue)
            .assertNotComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `execute Single`() {
        val value = 312L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleSingle" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Single<*>)
        (executed as Single<Long>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Maybe`() {
        val value = 312L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleMaybe" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Maybe<*>)
        (executed as Maybe<Long>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Flow`() {
        val value = 1L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleFlow" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(FlowGetAdapter(valueObserver, Dispatchers.Unconfined))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Flow<*>)

        (executed as Flow<Long>)
            .turbine {
                Assertions.assertEquals(1L, it.awaitItem())

                val newValue = 2L
                valueObserver.updateValue(GetBooleanPrefTest.KEY_TITLE, newValue)
                Assertions.assertEquals(2L, it.awaitItem())
            }
    }

    @Test
    fun `execute Long`() {
        val value = 312L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleLong" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), emptyList()
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Long)
        Assertions.assertEquals(value, executed as Long)
    }

    @Test
    fun `execute void`() {
        val value = 312L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleVoid" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), emptyList()
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }

    @Test
    fun `execute Completable`() {
        val value = 312L
        pref.values[GetLongPrefTest.KEY_TITLE] = value

        val method = GetLongPrefTest::class.java.methods.first { it.name == "getTitleCompletable" }
        val annotation = method.getAnnotation(GetLong::class.java)
        invoker = GetLongInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }
}

interface GetLongPrefTest {
    @GetLong(KEY_TITLE)
    fun getTitleObservable(): Observable<Long>

    @GetLong(KEY_TITLE)
    fun getTitleSingle(): Single<Long>

    @GetLong(KEY_TITLE)
    fun getTitleMaybe(): Maybe<Long>

    @GetLong(KEY_TITLE)
    fun getTitleFlow(): Flow<Long>

    @GetLong(KEY_TITLE)
    fun getTitleLong(): Long

    // for error case
    @GetLong(KEY_TITLE)
    fun getTitleVoid()

    // for error case
    @GetLong(KEY_TITLE)
    fun getTitleCompletable(): Completable

    @Set(KEY_TITLE)
    fun setTitle(value: Int): Boolean

    companion object {
        const val KEY_TITLE = "title"
    }
}