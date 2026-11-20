plugins {
    id("divkit.convention.library-kmp-ios")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.jvm-api-check")
}

kotlin {
    jvm()

    android {
        namespace = "com.yandex.div.network.ktor"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
        jvmTest.dependencies {
            implementation(libs.ktor.client.encoding)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.okhttp.mockwebserver)
        }
    }
}

tasks.named<Test>("jvmTest") {
    systemProperty("kotlinx.coroutines.io.parallelism", "1")
}
