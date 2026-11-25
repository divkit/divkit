plugins {
    id("divkit.convention.library-kmp-ios")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.jvm-api-check")
}

kotlin {
    jvm()

    android {
        namespace = "com.yandex.div.render.common"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
        }
        androidMain.dependencies {
            api(libs.androidx.core)
            compileOnly(libs.androidx.annotation)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
