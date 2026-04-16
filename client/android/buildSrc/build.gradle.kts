plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.reflect)
}
