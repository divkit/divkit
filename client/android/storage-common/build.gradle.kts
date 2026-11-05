plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.jvm-api-check")
}

kotlin {
    jvm()

    android {
        namespace = "com.yandex.div.storage.common"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
