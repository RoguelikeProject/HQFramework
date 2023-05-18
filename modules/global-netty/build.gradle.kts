plugins {
    id("hqframework.shared")
    id("hqframework.publish")
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.kotlinx.coroutines.core)
    api(libs.netty)
    api(libs.guava)
    api(project(":modules:global-yaml"))

    testImplementation(libs.junit.parameterizedTest)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.netty)
    testImplementation(project(":modules:test"))
}