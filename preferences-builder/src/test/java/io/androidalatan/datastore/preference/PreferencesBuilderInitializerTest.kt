package io.androidalatan.datastore.preference

import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.SetAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowClearAdapter
import io.androidalatan.datastore.preference.adapter.flow.FlowGetAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxClearAdapter
import io.androidalatan.datastore.preference.adapter.rx.RxSetAdapter
import io.androidalatan.datastore.preference.invocator.clear.adapter.DefaultClearAdapter
import io.androidalatan.datastore.preference.invocator.get.adapter.DefaultGetAdapter
import io.androidalatan.datastore.preference.invocator.set.adapter.DefaultSetAdapter
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PreferencesBuilderInitializerTest {

    @Test
    fun init() {
        val setAdapters = listOf(
            SetAdapter.Factory { DefaultSetAdapter() },
            SetAdapter.Factory { RxSetAdapter(Schedulers.trampoline()) }
        )
        val getAdapters = listOf(
            GetAdapter.Factory { DefaultGetAdapter(it) },
            GetAdapter.Factory { FlowGetAdapter(it, Dispatchers.Unconfined) }
        )
        val clearAdapters = listOf(
            ClearAdapter.Factory { DefaultClearAdapter() },
            ClearAdapter.Factory { FlowClearAdapter(Dispatchers.Unconfined) },
            ClearAdapter.Factory { RxClearAdapter(Schedulers.trampoline()) }
        )
        PreferencesBuilderInitializer.init(
            setAdapters,
            getAdapters,
            clearAdapters
        )

        Assertions.assertEquals(PreferencesBuilderInitializer.setAdapterFactories, setAdapters)
        Assertions.assertEquals(PreferencesBuilderInitializer.getAdapterFactories, getAdapters)
        Assertions.assertEquals(PreferencesBuilderInitializer.clearAdapterFactories, clearAdapters)

    }
}