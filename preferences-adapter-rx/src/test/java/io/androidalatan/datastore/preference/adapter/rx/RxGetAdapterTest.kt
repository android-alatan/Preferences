package io.androidalatan.datastore.preference.adapter.rx

import io.androidalatan.datastore.preference.invocator.ValueObserverImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RxGetAdapterTest {

    private val valueObserver = ValueObserverImpl()
    private val getAdapter = RxGetAdapter(valueObserver, Schedulers.trampoline())

    @Test
    fun acceptable() {
        Assertions.assertTrue(getAdapter.acceptable(Single::class.java))
        Assertions.assertTrue(getAdapter.acceptable(Maybe::class.java))
        Assertions.assertTrue(getAdapter.acceptable(Observable::class.java))
        Assertions.assertThrows(IllegalStateException::class.java) {
            getAdapter.acceptable(Completable::class.java)
        }

        Assertions.assertFalse(getAdapter.acceptable(Boolean::class.java))
    }

    @BeforeEach
    fun setUp() {
        valueObserver.updateValue(KEY, true)
    }

    @Test
    fun `adapt single`() {
        (getAdapter.adapt(Single::class.java, KEY, false) as Single<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `adapt maybe`() {
        (getAdapter.adapt(Maybe::class.java, KEY, false) as Maybe<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `adapt observable`() {
        val testObserver = (getAdapter.adapt(Observable::class.java, KEY, false) as Observable<Boolean>)
            .test()
            .assertValueCount(1)
            .assertValue(true)
            .assertNotComplete()
            .assertNoErrors()

        valueObserver.updateValue(KEY, false)

        testObserver.assertValueCount(2)
            .assertValueAt(1, false)
            .assertNoErrors()
            .assertNotComplete()
            .dispose()
    }

    companion object {
        private val KEY = "key-1"
    }
}