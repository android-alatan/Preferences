[![Main Commit](https://github.com/android-alatan/Preferences/actions/workflows/lib-main-branch.yml/badge.svg?branch=main)](https://github.com/android-alatan/Preferences/actions/workflows/lib-main-branch.yml)
[![Release](https://jitpack.io/v/android-alatan/preferences.svg)](https://jitpack.io/#android-alatan/preferences)
# Preferences

When devs want to save specific key-value in Android, We usually consider of SharedPreferences.

So for that, devs usually follow these process:
  * define Key and value type
  * declare read/write interface to access value in SharedPreferences
  * implements read/write code

Implementation part will bring lots of boiler-plate code. Especially if you are using RxJava or Coroutine.

This tools is to skip implementation part likely Retrofit.

## Installation
```kotlin
implementation("com.github.android-alatan.preferences:preferences-builder:$version")
implementation("com.github.android-alatan.preferences:preferences-annotation:$version")
implementation("com.github.android-alatan.preferences:preferences-adapter-api:$version") // optional

implementation(":com.github.android-alatan.preferences:preferences-adapter-rx:$version") // in case of RxJava
implementation(":com.github.android-alatan.preferences:preferences-adapter-flow:$version") // in case of Flow
```

## Example
in root module, you must initialize Preference Builder Adapters
```kotlin
class MyAppApplication {
    fun onCreate() {
        val setAdapters = listOf(
            SetAdapter.Factory { RxSetAdapter(Schedulers.io()) },
            SetAdapter.Factory { FlowSetAdapter(Dispatchers.IO) }
        )
        val getAdapters = listOf(
            GetAdapter.Factory { FlowGetAdapter(it, Dispatchers.IO) },
            GetAdapter.Factory { RxGetAdapter(it, Schedulers.io()) },
        )
        val clearAdapters = listOf(
            ClearAdapter.Factory { FlowClearAdapter(Dispatchers.IO) },
            ClearAdapter.Factory { RxClearAdapter(Schedulers.io()) }
        )
        PreferencesBuilderInitializer.init(
            setAdapters,
            getAdapters,
            clearAdapters
        )
    }
}
```

In your preference module, you can build your own Preferences
```kotlin
// preference interface
interface MyPreference {
  @Set("key-name")
  fun setName(name: String): Boolean // or Unit

  @Set("key-name")
  fun setNameObs(name: String): Observable<Boolean> // RxJava

  @Set("key-name")
  fun setNameFlow(name: String): Flow<Boolean> // Coroutine Flow

  @GetString("key-name", defaultValue = "empty-name")
  fun getName(): String // Work on caller thread

  @GetString("key-name", defaultValue = "empty-name")
  fun getNameObs(): Observable<String> // RxJava

  @GetString("key-name", defaultValue = "empty-name")
  fun getNameFlow(): Flow<String> // Coroutine Flow
}

// build preference
val myPreference = PreferencesBuilder(context)
  .name("my-preference") // default will be class' canonical name
  .mode(Context.MODE_PRIVATE) // default is Context.MODE_PRIVATE
  .create(MyPreference::class.java)
```
Once setting up above, you can use any methods in MyPreference.
```kotlin
Assertions.assertTrue(myPreference.setName("Foo"))
Assertions.assertEqual("Foo", myPreference.getName())
```

## JUnit Test
If you put context as null in PreferencesBuilder, It will work as In Memory data holder. It is to use on JUnit Test.
```kotlin
val myPreference = PreferencesBuilder().create(MyPreference::class.java)
```
`myPreference` will work same as real Preference But in-memory data holder. Once Test is done, Nothing will be behind.

Take a look below example for test.
```kotlin
class MyTest {
  private val myPreference = PreferencesBuilder().create(MyPreference::class.java)

  @Test
  fun myTest() {
    val nameObs = myPreference.getNameObs()
      .test()
      .assertValueCount(1)
      .assertValue("")
      .assertNoError()
      .assetNotCompleted()

    myPreference.setName("Foo")

    nameObs.assertValueCount(2)
      .assertValueAt(1, "Foo")
      .assertNoError()
      .assertNoCompleted()
      .dispose()
  }
}
```

## Limitation
Set and Clear return Boolean or void/Unit

Get denies Void/Unit
