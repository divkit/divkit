plugins {
    kotlin("jvm")
    id("divkit.convension.publishing-project")
    id("divkit.convension.publishing-module")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":divan-json-builder"))
    api(project(":legacy-json-builder"))
}
