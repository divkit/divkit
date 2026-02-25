import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import java.lang.Boolean.parseBoolean

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.multiplatform)
}

apply(from = "../publish-common.gradle")

kotlin {
    androidLibrary {
        namespace = "com.yandex.div.assertion"
        compileSdk = rootProject.extra["compileSdkVersion"] as Int
        minSdk = rootProject.extra["minSdkVersion"] as Int
    }
}

dependencies {
    lintChecks(project(":lint-rules"))
}

buildkonfig {
    packageName = "com.yandex.div.internal"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DISABLE_ASSERTS", "${providers.gradleProperty("disableAssertsInBuild").map(::parseBoolean).get()}")
    }
}
