plugins {
    id("lib-kotlin-android-no-config")
    id("publish-android")
}

dependencies {
    api(project(":preferences-annotation"))
    api(project(":preferences-adapter-api"))
    api(libs.jsonParser.api)
    compileOnly(libs.androidAnnotation)

    testImplementation(project(":preferences-adapter-rx"))
    testImplementation(project(":preferences-adapter-flow"))
    testImplementation(libs.jsonParser.impl)
    testImplementation(libs.moshi)
    testImplementation(libs.rxjava)
    testImplementation(libs.coroutine)
    testImplementation(project(":coroutine-test-util"))
}