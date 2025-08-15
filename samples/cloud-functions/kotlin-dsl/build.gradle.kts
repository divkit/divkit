plugins {
    kotlin("jvm") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.yandex.div:kotlin-json-builder:31.0.0")
}
