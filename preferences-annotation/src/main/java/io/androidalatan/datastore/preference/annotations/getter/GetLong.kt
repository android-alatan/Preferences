package io.androidalatan.datastore.preference.annotations.getter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetLong(
    val name: String,
    val defaultValue: Long = 0L,
    val disable: Boolean = false,
    val disableValue: Long = 0L
)