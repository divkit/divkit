plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.stub-aar")
}

kotlin {
    android {
        namespace = "com.yandex.div.logging"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
        }
    }
}
