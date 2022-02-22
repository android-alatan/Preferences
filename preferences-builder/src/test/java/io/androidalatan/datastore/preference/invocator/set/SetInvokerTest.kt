package io.androidalatan.datastore.preference.invocator.set

import com.squareup.moshi.Moshi
import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowSetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxSetAdapter
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.androidalatan.datastore.preference.invocator.get.mock.MockPersonAdapter
import io.androidalatan.datastore.preference.invocator.get.mock.TestPersonObj
import io.androidalatan.datastore.preference.invocator.set.adapter.DefaultSetAdapter
import io.androidalatan.jsonparser.JsonParserImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("UNCHECKED_CAST")
class SetInvokerTest {
    private val pref = InMemoryPreference()
    private lateinit var invoker: SetInvoker
    private val valueObserver = ValueObserverImpl()

    private val jsonParser = JsonParserImpl(
        Moshi.Builder()
            .add(MockPersonAdapter())
            .build()
    )

    @Test
    fun `execute Observable`() {

        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleObservable" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(RxSetAdapter(Schedulers.trampoline()))
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Observable<*>)
        (executed as Observable<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .dispose()

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute Single`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleSingle" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(RxSetAdapter(Schedulers.trampoline()))
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Single<*>)
        (executed as Single<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertComplete()
            .dispose()

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute Maybe`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleMaybe" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(RxSetAdapter(Schedulers.trampoline()))
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Maybe<*>)
        (executed as Maybe<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .dispose()

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute Completable`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleCompletable" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(RxSetAdapter(Schedulers.trampoline()))
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Completable)
        (executed as Completable)
            .test()
            .assertNoValues()
            .assertComplete()
            .dispose()

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute Flow`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleFlow" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(FlowSetAdapter(Dispatchers.Unconfined))
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Flow<*>)
        (executed as Flow<Boolean>)
            .turbine { flowTurbine ->
                Assertions.assertTrue(flowTurbine.awaitItem())

                flowTurbine.awaitComplete()
            }

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute Boolean`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleBoolean" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(DefaultSetAdapter())
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Boolean)
        Assertions.assertTrue(executed as Boolean)

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute Void`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setTitleVoid" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(DefaultSetAdapter())
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val value = "name"
        val executed = invoker.execute(arrayOf(value))

        Assertions.assertTrue(executed is Unit)

        Assertions.assertEquals(value, pref.getString(SetPrefTest.KEY_TITLE, value))
        Assertions.assertEquals(value, valueObserver.getValue(SetPrefTest.KEY_TITLE))
    }

    @Test
    fun `execute object single`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setPerson" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(RxSetAdapter(Schedulers.trampoline()))
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val person = TestPersonObj(VALUE_NAME)
        val executed = invoker.execute(arrayOf(person))

        Assertions.assertTrue(executed is Single<*>)
        (executed as Single<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertComplete()
            .assertNoErrors()
            .dispose()

        Assertions.assertEquals(person, jsonParser.fromJson(pref.getString(SetPrefTest.KEY_PERSON, ""), TestPersonObj::class.java))
        Assertions.assertEquals(person, valueObserver.getValue(SetPrefTest.KEY_PERSON))
    }

    @Test
    fun `execute object raw`() {
        val method = SetPrefTest::class.java.methods.first { it.name == "setPersonSync" }
        val annotation = method.annotations.first { it is Set } as Set
        invoker = SetInvoker(
            pref, method, jsonParser, annotation, valueObserver,
            DefaultSetAdapter(), listOf(DefaultSetAdapter())
        )

        Assertions.assertEquals("", pref.getString(SetPrefTest.KEY_TITLE, ""))

        val person = TestPersonObj(VALUE_NAME)
        val executed = invoker.execute(arrayOf(person))

        Assertions.assertTrue(executed is Unit)

        Assertions.assertEquals(person, jsonParser.fromJson(pref.getString(SetPrefTest.KEY_PERSON, ""), TestPersonObj::class.java))
        Assertions.assertEquals(person, valueObserver.getValue(SetPrefTest.KEY_PERSON))
    }

    companion object {
        private const val VALUE_NAME = "test-name-1"
    }
}

@Suppress("unused")
interface SetPrefTest {
    @Set(KEY_TITLE)
    fun setTitleObservable(title: String): Observable<Boolean>

    @Set(KEY_TITLE)
    fun setTitleSingle(title: String): Single<Boolean>

    @Set(KEY_TITLE)
    fun setTitleMaybe(title: String): Maybe<Boolean>

    @Set(KEY_TITLE)
    fun setTitleCompletable(title: String): Completable

    @Set(KEY_TITLE)
    fun setTitleBoolean(title: String): Boolean

    @Set(KEY_TITLE)
    fun setTitleFlow(title: String): Flow<Boolean>

    @Set(KEY_TITLE)
    fun setTitleVoid(title: String)

    @Set(KEY_PERSON)
    fun setPerson(person: TestPersonObj): Single<Boolean>

    @Set(KEY_PERSON)
    fun setPersonSync(person: TestPersonObj)

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_PERSON = "aud8"
    }
}