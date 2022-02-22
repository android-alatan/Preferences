package io.androidalatan.datastore.preference.invocator.get

import com.squareup.moshi.Moshi
import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.annotations.getter.DefaultObject
import io.androidalatan.datastore.preference.annotations.getter.GetObject
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.androidalatan.datastore.preference.invocator.get.adapter.DefaultGetAdapter
import io.androidalatan.datastore.preference.invocator.get.mock.MockPersonAdapter
import io.androidalatan.datastore.preference.invocator.get.mock.TestPersonObj
import io.androidalatan.jsonparser.JsonParserImpl
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
class GetObjectInvokerTest {

    private val pref = InMemoryPreference()
    private val valueObserver = ValueObserverImpl()
    private val jsonParser = JsonParserImpl(
        Moshi.Builder()
            .add(MockPersonAdapter())
            .build()
    )

    @Test
    fun `execute observable`() {
        val method = TestGetObjectPref::class.java.methods.first { it.name == "getPersonObservable" }

        val invoker = GetObjectInvoker(
            pref,
            method,
            jsonParser,
            valueObserver,
            method.getAnnotation(GetObject::class.java),
            DefaultGetAdapter(valueObserver), listOf(RxGetAdapter(valueObserver, Schedulers.trampoline()))
        )

        val execute = invoker.execute(emptyArray())
        Assertions.assertTrue(execute is Observable<*>)
        val testObserver = (execute as Observable<TestPersonObj>)
            .test()
            .assertNoValues()
            .assertNoErrors()
            .assertNotComplete()

        valueObserver.updateValue(TestGetObjectPref.KEY_PERSON, TestPersonObj(MOCK_NAME_1))

        testObserver.assertValueCount(1)
            .assertValue(TestPersonObj(MOCK_NAME_1))
            .assertNoErrors()
            .assertNotComplete()
            .dispose()
    }

    @Test
    fun `execute flow`() {
        val method = TestGetObjectPref::class.java.methods.first { it.name == "getPersonFlow" }

        val invoker = GetObjectInvoker(
            pref,
            method,
            jsonParser,
            valueObserver,
            method.getAnnotation(GetObject::class.java),
            DefaultGetAdapter(valueObserver), listOf(FlowGetAdapter(valueObserver, Dispatchers.Unconfined))
        )

        val execute = invoker.execute(emptyArray())
        Assertions.assertTrue(execute is Flow<*>)
        (execute as Flow<TestPersonObj>)
            .turbine {
                it.expectNoEvents()

                valueObserver.updateValue(TestGetObjectPref.KEY_PERSON, TestPersonObj(MOCK_NAME_1))
                Assertions.assertEquals(TestPersonObj(MOCK_NAME_1), it.awaitItem())
            }
    }

    @Test
    fun `execute raw return`() {
        val method = TestGetObjectPref::class.java.methods.first { it.name == "getPersonSync" }

        val invoker = GetObjectInvoker(
            pref,
            method,
            jsonParser,
            valueObserver,
            method.getAnnotation(GetObject::class.java),
            DefaultGetAdapter(valueObserver), emptyList()
        )

        Assertions.assertNull(invoker.execute(emptyArray()))

        valueObserver.updateValue(TestGetObjectPref.KEY_PERSON, TestPersonObj(MOCK_NAME_1))

        val result = invoker.execute(emptyArray())
        Assertions.assertNotNull(result)
        Assertions.assertTrue(result is TestPersonObj)
        Assertions.assertEquals(MOCK_NAME_1, (result as TestPersonObj).name)
    }

    @Test
    fun `execute raw return with default`() {
        val method = TestGetObjectPref::class.java.methods.first { it.name == "getPersonSync2" }

        val invoker = GetObjectInvoker(
            pref,
            method,
            jsonParser,
            valueObserver,
            method.getAnnotation(GetObject::class.java),
            DefaultGetAdapter(valueObserver), emptyList()
        )

        val result1 = invoker.execute(arrayOf(TestPersonObj(MOCK_NAME_1)))
        Assertions.assertNotNull(result1)
        Assertions.assertTrue(result1 is TestPersonObj)
        Assertions.assertEquals(MOCK_NAME_1, (result1 as TestPersonObj).name)

        valueObserver.updateValue(TestGetObjectPref.KEY_PERSON, TestPersonObj(MOCK_NAME_2))

        val result2 = invoker.execute(arrayOf(TestPersonObj(MOCK_NAME_2)))
        Assertions.assertNotNull(result2)
        Assertions.assertTrue(result2 is TestPersonObj)
        Assertions.assertEquals(MOCK_NAME_2, (result2 as TestPersonObj).name)
    }

    interface TestGetObjectPref {
        @GetObject(KEY_PERSON)
        fun getPersonObservable(): Observable<TestPersonObj>

        @GetObject(KEY_PERSON)
        fun getPersonFlow(): Flow<TestPersonObj>

        @GetObject(KEY_PERSON)
        fun getPersonSync(): TestPersonObj?

        @GetObject(KEY_PERSON)
        fun getPersonSync2(@DefaultObject defaultValue: TestPersonObj): TestPersonObj

        companion object {
            internal const val KEY_PERSON = "13wqw"
        }
    }

    companion object {
        private const val MOCK_NAME_1 = "test-name-1"
        private const val MOCK_NAME_2 = "test-name-2"
    }
}