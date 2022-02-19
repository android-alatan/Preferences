plugins {
    id("lib-kotlin-android-no-config")
    id("publish-android")
}

dependencies {
    api(project(":preferences-adapter-api"))
    api(libs.rxjava)

    testImplementation(project(":preferences-builder"))
}