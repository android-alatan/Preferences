package io.androidalatan.datastore.preference.annotations.getter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetInt(
    val name: String,
    val defaultValue: Int = 0,
    val disable: Boolean = false,
    val disableValue: Int = 0
)