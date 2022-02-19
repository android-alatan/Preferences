package io.androidalatan.datastore.preference

import android.content.Context
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PreferencesBuilderTest {

    private val preferencesBuilder = PreferencesBuilder()

    @Test
    fun name() {
        val name = "hello"
        preferencesBuilder.name(name)
        Assertions.assertEquals(name, preferencesBuilder.name)
    }

    @Test
    fun mode() {
        val mode = Context.MODE_MULTI_PROCESS
        preferencesBuilder.mode(mode)
        Assertions.assertEquals(mode, preferencesBuilder.mode)
    }

    @Test
    fun create() {
        val dataStoreInterface = preferencesBuilder.create(DataStoreInterface::class.java)
        Assertions.assertNotNull(dataStoreInterface)
    }
}

interface DataStoreInterface {

}