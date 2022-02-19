package io.androidalatan.datastore.preference.annotations.getter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetString(
    val name: String,
    val defaultValue: String = "",
    val disable: Boolean = false,
    val disableValue: String = ""
)