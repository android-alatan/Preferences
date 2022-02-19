package io.androidalatan.datastore.preference.adapter.rx

import android.content.SharedPreferences
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.ValueObserver
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

class RxClearAdapter(private val scheduler: Scheduler) : ClearAdapter {
    override fun acceptable(returnType: Class<*>): Boolean {
        return when (returnType) {
            Single::class.java,
            Maybe::class.java,
            Observable::class.java,
            Completable::class.java -> true
            else -> false
        }
    }

    override fun adapt(returnType: Class<*>, sharedPreferences: SharedPreferences, valueObserver: ValueObserver): Any? {

        return when (returnType) {
            Single::class.java -> {
                Single.create<Boolean> { emitter ->
                    val onSharedPreferenceChangeListener = registerForOnetime(sharedPreferences) {
                        valueObserver.clear()
                        emitter.onSuccess(true)
                    }
                    sharedPreferences.edit()
                        .clear()
                        .apply()
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
                    }

                }
                    .subscribeOn(scheduler)
            }
            Maybe::class.java -> {
                Maybe.create<Boolean> { emitter ->
                    val onSharedPreferenceChangeListener = registerForOnetime(sharedPreferences) {
                        valueObserver.clear()
                        emitter.onSuccess(true)
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
                    }
                    sharedPreferences.edit()
                        .clear()
                        .apply()
                }
                    .subscribeOn(scheduler)
            }
            Completable::class.java -> {
                Completable.create { emitter ->
                    val onSharedPreferenceChangeListener = registerForOnetime(sharedPreferences) {
                        valueObserver.clear()
                        emitter.onComplete()
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
                    }
                    sharedPreferences.edit()
                        .clear()
                        .apply()
                }
                    .subscribeOn(scheduler)
            }
            Observable::class.java -> {
                Observable.create<Boolean> { emitter ->
                    val onSharedPreferenceChangeListener = registerForOnetime(sharedPreferences) {
                        valueObserver.clear()
                        emitter.onNext(true)
                        emitter.onComplete()
                    }
                    emitter.setCancellable {
                        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
                    }
                    sharedPreferences.edit()
                        .clear()
                        .apply()
                }
                    .subscribeOn(scheduler)
            }
            else -> throw IllegalStateException()
        }
    }

    private fun registerForOnetime(
        sharedPreferences: SharedPreferences,
        notified: (SharedPreferences) -> Unit = {}
    ): SharedPreferences.OnSharedPreferenceChangeListener {
        val onSharedPreferenceChangeListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
                if (sharedPreferences.all.isEmpty()) {
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
                    notified.invoke(sharedPreferences)
                }

            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        return onSharedPreferenceChangeListener
    }

}