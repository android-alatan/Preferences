package io.androidalatan.datastore.preference.adapter.rx

import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class RxGetAdapter(
    private val valueObserver: ValueObserver,
    private val scheduler: Scheduler,
) : GetAdapter {
    override fun acceptable(returnType: Class<*>): Boolean {
        return when (returnType) {
            Single::class.java,
            Maybe::class.java,
            Observable::class.java -> true
            Completable::class.java -> throw IllegalStateException("We don't allow to use Completable for \"Get\"")
            else -> false
        }
    }

    override fun <T> adapt(returnType: Class<*>, key: String, defaultValue: T): Any? {
        return when (returnType) {
            Single::class.java -> single(key, defaultValue)
            Maybe::class.java -> maybe<T>(key, defaultValue)
            Observable::class.java -> observable<T>(key, defaultValue)
            else -> throw IllegalStateException()
        }
    }

    private fun <T> maybe(key: String, defaultValue: T): Maybe<T> {
        return valueObserver.asObservable(key, defaultValue)
            .firstElement()
            .subscribeOn(scheduler)
    }

    private fun <T> observable(key: String, defaultValue: T): Observable<T> {
        return valueObserver.asObservable(key, defaultValue)
            .subscribeOn(scheduler)
    }

    private fun <T> single(key: String, defValue: T): Single<T> {
        return valueObserver.asObservable(key, defValue)
            .first(defValue)
            .subscribeOn(scheduler)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> ValueObserver.asObservable(key: String, defaultValue: T): Observable<T> {
        return Observable.create { emitter ->
            val callback = ValueObserver.Callback {
                emitter.onNext(it as T)
            }
            registerCallback(key, callback = callback)
            getValue(key) ?: emitter.onNext(defaultValue)
            emitter.setCancellable {
                unregisterCallback(key, callback)
            }
        }
    }
}