package io.androidalatan.datastore.preference.adapter.rx.test

import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class PreferenceTestExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        preferenceInit(Schedulers.trampoline(), Dispatchers.Unconfined)
    }
}