package io.androidalatan.datastore.preference.invocator.get

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.annotations.getter.GetBoolean
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

class GetBooleanInvokerTest {
    private val pref = InMemoryPreference()
    private val valueObserver = ValueObserverImpl()

    private lateinit var invoker: GetBooleanInvoker

    @Test
    fun `execute Observable`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleObservable" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Observable<*>)
        val testObserver = (executed as Observable<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNotComplete()

        val newValue = false
        valueObserver.updateValue(GetBooleanPrefTest.KEY_TITLE, newValue)

        testObserver.assertValueCount(2)
            .assertValueAt(1, newValue)
            .assertNotComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `execute Single`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleSingle" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Single<*>)
        (executed as Single<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Maybe`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleMaybe" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Maybe<*>)
        (executed as Maybe<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Flow`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleFlow" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(FlowGetAdapter(valueObserver, Dispatchers.Unconfined))
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Flow<*>)

        (executed as Flow<Boolean>)
            .turbine {
                Assertions.assertEquals(true, it.awaitItem())

                val newValue = false
                valueObserver.updateValue(GetBooleanPrefTest.KEY_TITLE, newValue)
                Assertions.assertEquals(false, it.awaitItem())
            }
    }

    @Test
    fun `execute Boolean`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleBoolean" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), emptyList()
        )

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Boolean)
        Assertions.assertEquals(value, executed as Boolean)
    }

    @Test
    fun `execute void`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleVoid" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), emptyList()
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }

    @Test
    fun `execute Completable`() {
        val value = true
        pref.values[GetBooleanPrefTest.KEY_TITLE] = value

        val method = GetBooleanPrefTest::class.java.methods.first { it.name == "getTitleCompletable" }
        val annotation = method.getAnnotation(GetBoolean::class.java)
        invoker = GetBooleanInvoker(
            pref, method, valueObserver, annotation,
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }
}

interface GetBooleanPrefTest {
    @GetBoolean(KEY_TITLE)
    fun getTitleObservable(): Observable<Boolean>

    @GetBoolean(KEY_TITLE)
    fun getTitleSingle(): Single<Boolean>

    @GetBoolean(KEY_TITLE)
    fun getTitleMaybe(): Maybe<Boolean>

    @GetBoolean(KEY_TITLE)
    fun getTitleBoolean(): Boolean

    @GetBoolean(KEY_TITLE)
    fun getTitleFlow(): Flow<Boolean>

    // for error case
    @GetBoolean(KEY_TITLE)
    fun getTitleVoid()

    // for error case
    @GetBoolean(KEY_TITLE)
    fun getTitleCompletable(): Completable

    @Set(KEY_TITLE)
    fun setTitle(value: Boolean): Boolean

    companion object {
        const val KEY_TITLE = "title"
    }
}