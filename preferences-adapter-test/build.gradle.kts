plugins {
    id("lib-kotlin-android-no-config")
    id("publish-android")
}

dependencies {
    api(project(":preferences-adapter-api"))
    api(project(":preferences-builder"))
    api(project(":preferences-adapter-rx"))
    api(project(":preferences-adapter-flow"))
    api(project(":coroutine-test-util"))
    api(libs.rxjava)
    api(libs.coroutine)
    api(libs.junit5)
}