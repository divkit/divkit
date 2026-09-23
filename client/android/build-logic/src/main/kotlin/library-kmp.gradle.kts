package divkit.convention

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("divkit.convention.abi-validation")
    id("divkit.convention.stub-aar")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

// TODO(gulevsky): remove withGroovyBuilder once com.yandex.div.gradle.Version class becomes available
val divkitVersion: Any by rootProject.extra
version = divkitVersion.withGroovyBuilder {
    getProperty("baseVersionName") as String
}

kotlin {
    android {
        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
    }
}

dependencies {
    add("lintChecks", project(":lint-rules"))
}
