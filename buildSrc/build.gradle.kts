@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = libs.versions.kotlin.get()
}
dependencies {
    implementation(libs.detekt)
    implementation(libs.kotlin)
    implementation(libs.agp)
}