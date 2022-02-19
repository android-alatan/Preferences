package io.androidalatan.datastore.preference.annotations.setter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Set(val name: String)