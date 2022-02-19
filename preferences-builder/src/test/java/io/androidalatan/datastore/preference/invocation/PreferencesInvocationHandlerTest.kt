package io.androidalatan.datastore.preference.invocation

import com.squareup.moshi.Moshi
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowClearAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowSetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxClearAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxSetAdapter
import io.androidalatan.datastore.preference.annotations.getter.DefaultObject
import io.androidalatan.datastore.preference.annotations.getter.GetObject
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.annotations.setter.Clear
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.get.mock.MockPersonAdapter
import io.androidalatan.datastore.preference.invocator.get.mock.TestPersonObj
import io.androidalatan.jsonparser.JsonParserImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy

class PreferencesInvocationHandlerTest {

    private val preference = InMemoryPreference()
    private val jsonParser = JsonParserImpl(
        Moshi.Builder()
            .add(MockPersonAdapter())
            .build()
    )

    @Test
    fun invokeTest() {
        val clazz = PrefTest::class.java
        val prefTest = Proxy.newProxyInstance(
            clazz.classLoader, arrayOf(clazz), PreferencesInvocationHandler(
                lazy { preference },
                jsonParser,
                listOf(
                    SetAdapter.Factory { RxSetAdapter(Schedulers.trampoline()) },
                    SetAdapter.Factory { FlowSetAdapter(Dispatchers.Unconfined) }),
                listOf(
                    GetAdapter.Factory { RxGetAdapter(it, Schedulers.trampoline()) },
                    GetAdapter.Factory { FlowGetAdapter(it, Dispatchers.Unconfined) }),
                listOf(
                    ClearAdapter.Factory { RxClearAdapter(Schedulers.trampoline()) },
                    ClearAdapter.Factory { FlowClearAdapter(Dispatchers.Unconfined) })
            )
        ) as PrefTest

        val testObserver = prefTest.getTitle()
            .test()
            .assertValueCount(1)
            .assertValue("")
            .assertNotComplete()
            .assertNoErrors()

        val title = "hello world"
        prefTest.setTitle(title)
            .blockingGet()

        testObserver.assertValueCount(2)
            .assertValueAt(1, title)
            .assertNotComplete()
            .assertNoErrors()

        val defaultValue = TestPersonObj("")
        val personObserver = prefTest.getPerson(defaultValue)
            .test()
            .assertValueCount(1)
            .assertValue(defaultValue)
            .assertNotComplete()

        prefTest.setPerson(TestPersonObj(MOCK_NAME))
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertComplete()
            .dispose()

        personObserver.assertValueCount(2)
            .assertValueAt(1, TestPersonObj(MOCK_NAME))
            .assertNotComplete()
            .assertNoErrors()

        println(personObserver.values())

        prefTest.clear()
            .blockingAwait()
        println(personObserver.values())

        personObserver.assertValueCount(3)
            .assertValueAt(2, defaultValue)
            .assertNotComplete()
            .assertNoErrors()
            .dispose()

        testObserver.assertValueCount(3)
            .assertValueAt(2, "")
            .assertNotComplete()
            .assertNoErrors()
            .dispose()
    }

    companion object {
        private const val MOCK_NAME = "mock-name"
    }

}

interface PrefTest {
    @GetString(KEY_TITLE)
    fun getTitle(): Observable<String>

    @Set(KEY_TITLE)
    fun setTitle(title: String): Single<Boolean>

    @Set(KEY_PERSON)
    fun setPerson(person: TestPersonObj): Single<Boolean>

    @GetObject(KEY_PERSON)
    fun getPerson(@DefaultObject defaultValue: TestPersonObj): Observable<TestPersonObj>

    @Clear
    fun clear(): Completable

    @Clear
    fun clearSync()

    @Clear
    fun clearFlow(): Flow<Boolean>

    companion object {
        private const val KEY_TITLE = "1dhas"
        private const val KEY_PERSON = "asdjh8"
    }
}