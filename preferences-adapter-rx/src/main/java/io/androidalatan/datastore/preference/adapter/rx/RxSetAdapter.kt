package io.androidalatan.datastore.preference.adapter.rx

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.lang.reflect.Type

class RxSetAdapter(private val scheduler: Scheduler) : SetAdapter {

    override fun acceptable(returnType: Type): Boolean = ACCEPTABLE_HASH.contains(returnType)

    override fun adapt(
        returnType: Type,
        sharedPreferences: SharedPreferences,
        valueObserver: ValueObserver,
        key: String,
        value: Any,
        updateValue: (SharedPreferences.Editor) -> Unit
    ): Any {
        return when (returnType) {
            Single::class.java -> {
                Single.create<Boolean> { emitter ->
                    val listener = registerForOnetime(sharedPreferences) {
                        valueObserver.updateValue(key, value)
                        emitter.onSuccess(true)
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
                    }
                    updateValue(sharedPreferences.edit())
                }
                    .subscribeOn(scheduler)
            }
            Maybe::class.java -> {
                Maybe.create<Boolean> { emitter ->
                    val listener = registerForOnetime(sharedPreferences) {
                        valueObserver.updateValue(key, value)
                        emitter.onSuccess(true)
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
                    }
                    updateValue(sharedPreferences.edit())
                }
                    .subscribeOn(scheduler)
            }
            Observable::class.java -> {
                Observable.create<Boolean> { emitter ->

                    val listener = registerForOnetime(sharedPreferences) {
                        valueObserver.updateValue(key, value)
                        emitter.onNext(true)
                        emitter.onComplete()
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
                    }
                    updateValue(sharedPreferences.edit())
                }
                    .subscribeOn(scheduler)
            }
            else -> {
                Completable.create { emitter ->
                    val listener = registerForOnetime(sharedPreferences) {
                        valueObserver.updateValue(key, value)
                        emitter.onComplete()
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
                    }
                    updateValue(sharedPreferences.edit())
                }
                    .subscribeOn(scheduler)
            }
        }
    }

    private fun registerForOnetime(
        sharedPreferences: SharedPreferences,
        notified: () -> Unit
    ): SharedPreferences.OnSharedPreferenceChangeListener {
        val onSharedPreferenceChangeListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
                notified.invoke()

            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        return onSharedPreferenceChangeListener
    }

    companion object {
        private val ACCEPTABLE_HASH = setOf(
            Single::class.java,
            Maybe::class.java,
            Completable::class.java,
            Observable::class.java
        )
    }
}