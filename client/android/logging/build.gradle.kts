import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import java.lang.Boolean.parseBoolean

plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.stub-aar")
    alias(libs.plugins.buildkonfig)
}

kotlin {
    android {
        namespace = "com.yandex.div.logging"
    }
}

buildkonfig {
    packageName = "com.yandex.div.logging"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DISABLE_LOGS", "${providers.gradleProperty("disableLogsInBuild").map(::parseBoolean).get()}")
    }
}

tasks.named("metalavaGenerateSignature") {
    dependsOn("generateBuildKonfig")
}

tasks.named("metalavaCheckCompatibility") {
    dependsOn("generateBuildKonfig")
}
