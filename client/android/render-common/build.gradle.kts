plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
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
            implementation(libs.kotlin.test)
        }
    }
}
