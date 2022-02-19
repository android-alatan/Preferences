package io.androidalatan.datastore.preference.invocator.clear

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.adapter.flow.FlowClearAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxClearAdapter
import io.androidalatan.datastore.preference.annotations.setter.Clear
import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.androidalatan.datastore.preference.invocator.clear.adapter.DefaultClearAdapter
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ClearInvokerTest {

    @Test
    fun `execute Completable`() {
        val sharedPreferences = InMemoryPreference()
        val method = ClearPrefTest::class.java.methods.first { it.name == "clearCompletable" }
        val valueObserver = ValueObserverImpl()
        val invoker = ClearInvoker(
            sharedPreferences, method, valueObserver,
            DefaultClearAdapter(), listOf(RxClearAdapter(Schedulers.trampoline()))
        )

        valueObserver.updateValue(KEY_TITLE, VALUE_TITLE)
        sharedPreferences.edit()
            .putString(KEY_TITLE, VALUE_TITLE)
            .commit()
        Assertions.assertEquals(VALUE_TITLE, valueObserver.getValue(KEY_TITLE))
        Assertions.assertEquals(VALUE_TITLE, sharedPreferences.values[KEY_TITLE])

        val execute = invoker.execute(emptyArray())
        Assertions.assertTrue(execute is Completable)
        (execute as Completable)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertNoValues()
            .dispose()

        Assertions.assertNull(valueObserver.getValue(KEY_TITLE))
        Assertions.assertTrue(sharedPreferences.values.isEmpty())

    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `execute Single`() {
        val sharedPreferences = InMemoryPreference()
        val method = ClearPrefTest::class.java.methods.first { it.name == "clearSingle" }
        val valueObserver = ValueObserverImpl()
        val invoker = ClearInvoker(sharedPreferences, method, valueObserver,
                                   DefaultClearAdapter(), listOf(RxClearAdapter(Schedulers.trampoline())))

        valueObserver.updateValue(KEY_TITLE, VALUE_TITLE)
        sharedPreferences.edit()
            .putString(KEY_TITLE, VALUE_TITLE)
            .commit()

        Assertions.assertEquals(VALUE_TITLE, valueObserver.getValue(KEY_TITLE))
        Assertions.assertEquals(VALUE_TITLE, sharedPreferences.values[KEY_TITLE])

        val execute = invoker.execute(emptyArray())
        Assertions.assertTrue(execute is Single<*>)
        (execute as Single<Boolean>)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue(true)
            .dispose()

        Assertions.assertNull(valueObserver.getValue(KEY_TITLE))
        Assertions.assertTrue(sharedPreferences.values.isEmpty())

    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `execute Flow`() {
        val sharedPreferences = InMemoryPreference()
        val method = ClearPrefTest::class.java.methods.first { it.name == "clearFlow" }
        val valueObserver = ValueObserverImpl()
        val invoker = ClearInvoker(sharedPreferences, method, valueObserver,
                                   DefaultClearAdapter(), listOf(FlowClearAdapter(Dispatchers.Unconfined)))

        valueObserver.updateValue(KEY_TITLE, VALUE_TITLE)
        sharedPreferences.edit()
            .putString(KEY_TITLE, VALUE_TITLE)
            .commit()

        Assertions.assertEquals(VALUE_TITLE, valueObserver.getValue(KEY_TITLE))
        Assertions.assertEquals(VALUE_TITLE, sharedPreferences.values[KEY_TITLE])

        val execute = invoker.execute(emptyArray())
        Assertions.assertTrue(execute is Flow<*>)
        (execute as Flow<Boolean>)
            .turbine { flowTurbine ->
                Assertions.assertTrue(flowTurbine.awaitItem())
                flowTurbine.awaitComplete()
            }

        Assertions.assertNull(valueObserver.getValue(KEY_TITLE))
        Assertions.assertTrue(sharedPreferences.values.isEmpty())

    }

    @Test
    fun `execute void`() {
        val sharedPreferences = InMemoryPreference()
        val method = ClearPrefTest::class.java.methods.first { it.name == "clearSync" }
        val valueObserver = ValueObserverImpl()
        val invoker = ClearInvoker(sharedPreferences, method, valueObserver,
                                   DefaultClearAdapter(), emptyList())

        valueObserver.updateValue(KEY_TITLE, VALUE_TITLE)
        sharedPreferences.edit()
            .putString(KEY_TITLE, VALUE_TITLE)
            .commit()

        Assertions.assertEquals(VALUE_TITLE, valueObserver.getValue(KEY_TITLE))
        Assertions.assertEquals(VALUE_TITLE, sharedPreferences.values[KEY_TITLE])

        val execute = invoker.execute(emptyArray())
        Assertions.assertTrue(execute is Unit)

        Assertions.assertNull(valueObserver.getValue(KEY_TITLE))
        Assertions.assertTrue(sharedPreferences.values.isEmpty())

    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val VALUE_TITLE = "title-1"
    }
}

@Suppress("unused")
interface ClearPrefTest {

    @Clear
    fun clearSingle(): Single<Boolean>

    @Clear
    fun clearCompletable(): Completable

    @Clear
    fun clearFlow(): Flow<Boolean>

    @Clear
    fun clearSync()

}