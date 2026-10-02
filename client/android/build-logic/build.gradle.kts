plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    // Puts the generated version catalog accessors on the compile classpath so that plugins and
    // precompiled script plugins can read `libs` too. https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.agp.gradle)
    implementation(libs.google.testing.platform.proto)
    implementation(libs.kotlin.gradle)
    implementation(libs.metalava)
    implementation(libs.nexusPublishPlugin)
}
