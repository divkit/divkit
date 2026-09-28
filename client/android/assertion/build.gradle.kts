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
        namespace = "com.yandex.div.assertion"
    }
}

buildkonfig {
    packageName = "com.yandex.div.internal"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DISABLE_ASSERTS", "${providers.gradleProperty("disableAssertsInBuild").map(::parseBoolean).get()}")
    }
}

tasks.named("metalavaGenerateSignature") {
    dependsOn("generateBuildKonfig")
}

tasks.named("metalavaCheckCompatibility") {
    dependsOn("generateBuildKonfig")
}
