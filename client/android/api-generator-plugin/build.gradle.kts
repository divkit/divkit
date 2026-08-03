plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    implementation(gradleApi())
    compileOnly(libs.agp.gradleApi)
}

gradlePlugin {
    plugins {
        create("apiGeneratorPlugin") {
            id = "com.yandex.divkit.api-generator"
            implementationClass = "com.yandex.divkit.generator.ApiGeneratorPlugin"
        }
    }
}
