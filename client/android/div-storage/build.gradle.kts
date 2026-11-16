plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.storage"
}

dependencies {
    api(project(":storage"))
    implementation(project(":assertion"))
    implementation(project(":div-core"))
    implementation(project(":div-data"))
    implementation(project(":div-histogram"))
    implementation(project(":utils"))

    implementation(libs.androidx.core)
    implementation(libs.javax)

    testImplementation(libs.androidx.test.core)
    testImplementation(libs.json)
    testImplementation(libs.kotlinx.coroutines.core)
}

metalava {
    // These public types moved to the API dependency while retaining their JVM names.
    additionalSourceSets.from(
        project(":storage").file("src/commonMain/kotlin"),
        project(":storage").file("src/jvmAndAndroidMain/kotlin"),
    )
}
