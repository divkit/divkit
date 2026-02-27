import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import java.lang.Boolean.parseBoolean

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.multiplatform)
}

apply(from = "../kmp-library.gradle")
apply(from = "../publish-common.gradle")

kotlin {
    androidLibrary {
        namespace = "com.yandex.div.logging"
    }
}

buildkonfig {
    packageName = "com.yandex.div.logging"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DISABLE_LOGS", "${providers.gradleProperty("disableLogsInBuild").map(::parseBoolean).get()}")
    }
}
