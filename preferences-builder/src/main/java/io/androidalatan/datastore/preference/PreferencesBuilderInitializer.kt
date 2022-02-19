package io.androidalatan.datastore.preference

import io.androidalatan.datastore.preference.adapter.api.ClearAdapter
import io.androidalatan.datastore.preference.adapter.api.GetAdapter
import io.androidalatan.datastore.preference.adapter.api.SetAdapter

object PreferencesBuilderInitializer {

    internal var setAdapterFactories: List<SetAdapter.Factory> = emptyList()
        private set
    internal var getAdapterFactories: List<GetAdapter.Factory> = emptyList()
        private set
    internal var clearAdapterFactories: List<ClearAdapter.Factory> = emptyList()
        private set

    fun init(
        setAdapterFactories: List<SetAdapter.Factory>,
        getAdapterFactories: List<GetAdapter.Factory>,
        clearAdapterFactories: List<ClearAdapter.Factory>
    ) {
        this.clearAdapterFactories = clearAdapterFactories
        this.setAdapterFactories = setAdapterFactories
        this.getAdapterFactories = getAdapterFactories
    }
}