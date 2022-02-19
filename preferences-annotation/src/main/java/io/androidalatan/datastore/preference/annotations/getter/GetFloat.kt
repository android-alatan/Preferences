package io.androidalatan.datastore.preference.annotations.getter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetFloat(
    val name: String,
    val defaultValue: Float = 0f,
    val disable: Boolean = false,
    val disableValue: Float = 0f
)