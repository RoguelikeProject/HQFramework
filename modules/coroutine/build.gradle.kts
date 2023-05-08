plugins {
    id("hqframework.shared")
    id("hqframework.publish")
}

dependencies {
    compileOnly(libs.spigot.api)
    api(libs.kotlinx.coroutines.core)
    api(project(":modules:core"))

    testImplementation(libs.mockBukkit)
    testImplementation(libs.mockK)
    testImplementation(libs.junit.parameterizedTest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(project(":modules:core"))
    testImplementation(project(":modules:test"))
}