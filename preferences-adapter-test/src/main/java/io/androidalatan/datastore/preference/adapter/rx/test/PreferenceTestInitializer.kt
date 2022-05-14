package io.androidalatan.datastore.preference.adapter.rx.test

import io.androidalatan.datastore.preference.PreferencesBuilderInitializer
import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowClearAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowSetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxClearAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxSetAdapter
import io.reactivex.rxjava3.core.Scheduler
import kotlin.coroutines.CoroutineContext

fun preferenceInit(scheduler: Scheduler, coroutineContext: CoroutineContext) {
    PreferencesBuilderInitializer.init(
        listOf(
            SetAdapter.Factory { RxSetAdapter(scheduler) },
            SetAdapter.Factory { FlowSetAdapter(coroutineContext) }
        ),
        listOf(
            GetAdapter.Factory { valueObserver -> RxGetAdapter(valueObserver, scheduler) },
            GetAdapter.Factory { valueObserver -> FlowGetAdapter(valueObserver, coroutineContext) }
        ),
        listOf(
            ClearAdapter.Factory { RxClearAdapter(scheduler) },
            ClearAdapter.Factory { FlowClearAdapter(coroutineContext) }
        )
    )
}