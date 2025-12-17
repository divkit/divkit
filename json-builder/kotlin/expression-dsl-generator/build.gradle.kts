plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("divkit.dsl.expression.generator.Main")
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.kotlin)
    implementation(libs.jsonSchemaValidator)
}
