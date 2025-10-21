plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.publishing-project")
    id("divkit.convention.publishing-module")
}

group = "com.yandex.div"

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":divan-dsl"))
    api(project(":legacy-json-builder"))
}
