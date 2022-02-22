package io.androidalatan.datastore.preference.invocator.get

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.annotations.getter.GetFloat
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

class GetFloatInvokerTest {
    private val pref = InMemoryPreference()
    private val valueObserver = ValueObserverImpl()

    private lateinit var invoker: GetFloatInvoker

    @Test
    fun `execute Observable`() {
        val value = 312f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleObservable" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline())))

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Observable<*>)
        val testObserver = (executed as Observable<Float>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNotComplete()

        val newValue = 34f
        valueObserver.updateValue(GetFloatPrefTest.KEY_TITLE, newValue)

        testObserver.assertValueCount(2)
            .assertValueAt(1, newValue)
            .assertNotComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `execute Single`() {
        val value = 312f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleSingle" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline())))

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Single<*>)
        (executed as Single<Float>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Maybe`() {
        val value = 312f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleMaybe" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline())))

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Maybe<*>)
        (executed as Maybe<Float>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertComplete()
            .dispose()
    }

    @Test
    fun `execute Flow`() {
        val value = 1f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleFlow" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), listOf(FlowGetAdapter(valueObserver, Dispatchers.Unconfined)))

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Flow<*>)

        (executed as Flow<Float>)
            .turbine {
                Assertions.assertEquals(1f, it.awaitItem())

                val newValue = 2f
                valueObserver.updateValue(GetFloatPrefTest.KEY_TITLE, newValue)
                Assertions.assertEquals(2f, it.awaitItem())
            }
    }

    @Test
    fun `execute Float`() {
        val value = 312f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleFloat" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), emptyList())

        val executed = invoker.execute(emptyArray())

        Assertions.assertTrue(executed is Float)
        Assertions.assertEquals(value, executed as Float)
    }

    @Test
    fun `execute void`() {
        val value = 312f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleVoid" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), emptyList())

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }

    @Test
    fun `execute Completable`() {
        val value = 312f
        pref.values[GetFloatPrefTest.KEY_TITLE] = value

        val method = GetFloatPrefTest::class.java.methods.first { it.name == "getTitleCompletable" }
        val annotation = method.getAnnotation(GetFloat::class.java)
        invoker = GetFloatInvoker(pref, method, valueObserver, annotation,
                                  DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline())))

        Assertions.assertThrows(IllegalStateException::class.java) {
            invoker.execute(emptyArray())
        }
    }
}

interface GetFloatPrefTest {
    @GetFloat(KEY_TITLE)
    fun getTitleObservable(): Observable<Float>

    @GetFloat(KEY_TITLE)
    fun getTitleSingle(): Single<Float>

    @GetFloat(KEY_TITLE)
    fun getTitleMaybe(): Maybe<Float>

    @GetFloat(KEY_TITLE)
    fun getTitleFlow(): Flow<Float>

    @GetFloat(KEY_TITLE)
    fun getTitleFloat(): Float

    // for error case
    @GetFloat(KEY_TITLE)
    fun getTitleVoid()

    // for error case
    @GetFloat(KEY_TITLE)
    fun getTitleCompletable(): Completable

    @Set(KEY_TITLE)
    fun setTitle(value: Float): Boolean

    companion object {
        const val KEY_TITLE = "title"
    }
}