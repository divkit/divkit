plugins {
    `kotlin-dsl`
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
