plugins {
    id("divkit.convention.library-kmp-ios")
    id("divkit.convention.publishing-module-kmp")
    alias(libs.plugins.apiGenerator)
}

kotlin {
    jvm()

    android {
        namespace = "com.yandex.div.model"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

schemas {
    create("model") {
        schemas.set(file("../../../schema"))
        config.set(file("model-generator-config.json"))
        sourceSet.set("commonMain")
    }
    create("sharedData") {
        schemas.set(file("../../../shared_data"))
        config.set(file("model-shared-data-generator-config.json"))
        sourceSet.set("commonMain")
    }
}

tasks.named("metalavaGenerateSignature") {
    dependsOn("generateModel")
}

tasks.named("metalavaCheckCompatibility") {
    dependsOn("generateModel")
}
