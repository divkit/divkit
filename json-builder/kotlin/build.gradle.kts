plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.publishing-project")
    id("divkit.convention.publishing-module")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":divan-json-builder"))
    api(project(":legacy-json-builder"))
}
