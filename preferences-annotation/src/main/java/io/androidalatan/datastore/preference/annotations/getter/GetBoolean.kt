package io.androidalatan.datastore.preference.annotations.getter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetBoolean(
    val name: String,
    val defaultValue: Boolean = false,
    val disable: Boolean = false,
    val disableValue: Boolean = false
)