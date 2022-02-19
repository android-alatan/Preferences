package io.androidalatan.datastore.preference.adapter.rx

import io.androidalatan.datastore.preference.inmemory.InMemoryPreference
import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RxSetAdapterTest {

    private val setAdapter = RxSetAdapter(Schedulers.trampoline())

    private val preference = InMemoryPreference()

    @Test
    fun acceptable() {
        Assertions.assertTrue(setAdapter.acceptable(Single::class.java))
        Assertions.assertTrue(setAdapter.acceptable(Maybe::class.java))
        Assertions.assertTrue(setAdapter.acceptable(Observable::class.java))
        Assertions.assertTrue(setAdapter.acceptable(Completable::class.java))

        Assertions.assertFalse(setAdapter.acceptable(Boolean::class.java))
    }

    @Test
    fun `adapt single`() {
        val value = true
        (setAdapter.adapt(Single::class.java, preference, ValueObserverImpl(), KEY, value) {
            it.putBoolean(KEY, value)
                .apply()
        } as Single<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `adapt maybe`() {
        val value = true
        (setAdapter.adapt(Maybe::class.java, preference, ValueObserverImpl(), KEY, value) {
            it.putBoolean(KEY, value)
                .apply()
        } as Maybe<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `adapt observable`() {
        val value = true
        (setAdapter.adapt(Observable::class.java, preference, ValueObserverImpl(), KEY, value) {
            it.putBoolean(KEY, value)
                .apply()
        } as Observable<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(value)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    @Test
    fun `adapt completable`() {
        val value = true
        (setAdapter.adapt(Completable::class.java, preference, ValueObserverImpl(), KEY, value) {
            it.putBoolean(KEY, value)
                .apply()
        } as Completable)
            .test()
            .assertNoValues()
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }

    companion object {
        private const val KEY = "key-1"
    }
}