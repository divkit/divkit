import com.yandex.divkit.gradle.optBooleanProperty

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.publishing-project")
    id("divkit.convention.publishing-module")
}

val useLegacyGroupId = optBooleanProperty("useLegacyGroupId") ?: false
if (useLegacyGroupId) {
    group = "com.yandex.div"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":divan-dsl"))
}
