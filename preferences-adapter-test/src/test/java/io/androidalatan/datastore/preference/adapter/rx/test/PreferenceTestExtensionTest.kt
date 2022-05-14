package io.androidalatan.datastore.preference.adapter.rx.test

import io.androidalatan.coroutine.test.turbine
import io.androidalatan.datastore.preference.PreferencesBuilder
import io.androidalatan.datastore.preference.annotations.getter.GetString
import io.androidalatan.datastore.preference.annotations.setter.Clear
import io.androidalatan.datastore.preference.annotations.setter.Set
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PreferenceTestExtension::class)
class PreferenceTestExtensionTest {

    private val prefs by lazy { PreferencesBuilder().create(PrefTest::class.java) }

    @Test
    fun `prefTest rxjava`() {
        val testObserver = prefs.getTitle()
            .test()
            .assertValueCount(1)
            .assertValue("")
            .assertNotComplete()
            .assertNoErrors()

        val title = "hello"
        prefs.setTitle(title)
            .blockingGet()
        Assertions.assertEquals(title, prefs.getTitleSync())

        testObserver
            .assertValueCount(2)
            .assertValueAt(1, title)
            .assertNotComplete()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `prefTest flow`() {

        prefs.getTitleAsFlow()
            .turbine { flowTurbine ->
                Assertions.assertEquals("", flowTurbine.awaitItem())
                val newTitle = "title"
                prefs.setTitleAsFlow(newTitle)
                    .first()

                Assertions.assertEquals(newTitle, flowTurbine.awaitItem())
            }
    }
}

interface PrefTest {
    @GetString(KEY_TITLE)
    fun getTitle(): Observable<String>

    @GetString(KEY_TITLE)
    fun getTitleAsFlow(): Flow<String>

    @GetString(KEY_TITLE)
    fun getTitleSync(): String

    @Set(KEY_TITLE)
    fun setTitle(title: String): Single<Boolean>

    @Set(KEY_TITLE)
    fun setTitleAsFlow(title: String): Flow<Boolean>

    @Set(KEY_TITLE)
    fun setTitleSync(title: String): Boolean

    @Clear
    fun clear(): Completable

    @Clear
    fun clearAsFlow(): Flow<Boolean>

    companion object {
        private const val KEY_TITLE = "1dhas"
    }
}
