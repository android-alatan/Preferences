plugins {
    id("lib-kotlin-android-no-config")
    id("publish-android")
}

dependencies {
    api(project(":preferences-adapter-api"))
    api(libs.coroutine)

    testImplementation(project(":preferences-builder"))
    testImplementation(project(":coroutine-test-util"))
}