package io.androidalatan.datastore.preference.adapter.rx

import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RxClearAdapterTest {
    val sharedPreferences = InMemoryPreference()

    @Test
    fun acceptable() {
        RxClearAdapter(Schedulers.trampoline()).apply {
            Assertions.assertTrue(acceptable(Single::class.java))
            Assertions.assertTrue(acceptable(Maybe::class.java))
            Assertions.assertTrue(acceptable(Observable::class.java))
            Assertions.assertTrue(acceptable(Completable::class.java))

            Assertions.assertFalse(acceptable(Boolean::class.java))
        }
    }

    @BeforeEach
    fun setUp() {
        sharedPreferences.edit()
            .putBoolean("key-1", true)
            .apply()
    }

    @Test
    fun `adapt single`() {
        (RxClearAdapter(Schedulers.trampoline()).adapt(Single::class.java, sharedPreferences, ValueObserverImpl()) as Single<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `adapt maybe`() {
        (RxClearAdapter(Schedulers.trampoline()).adapt(Maybe::class.java, sharedPreferences, ValueObserverImpl()) as Maybe<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `adapt observable`() {
        (RxClearAdapter(Schedulers.trampoline()).adapt(
            Observable::class.java,
            sharedPreferences,
            ValueObserverImpl()
        ) as Observable<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `adapt completable`() {
        (RxClearAdapter(Schedulers.trampoline()).adapt(Completable::class.java, sharedPreferences, ValueObserverImpl()) as Completable)
            .test()
            .assertNoValues()
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }
}