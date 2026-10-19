plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
    alias(libs.plugins.apiGenerator)
}

kotlin {
    android {
        namespace = "com.yandex.div.parser.json"

        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            implementation(libs.androidx.collection)
        }

        androidMain.dependencies {
            api(project(":div-data"))

            implementation(project(":div-evaluable"))
            implementation(libs.androidx.core)
        }

        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.mockito.kotlin)
        }
    }
}

schemas {
    create("parser") {
        schemas.set(file("../../../schema"))
        config.set(file("parser-generator-config.json"))
    }
}
