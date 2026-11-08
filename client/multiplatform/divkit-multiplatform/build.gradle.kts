import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)

    id("divkit.convention.android-publishing-module")
}

kotlin {
    androidLibrary {
        namespace = "com.yandex.divkit.multiplatform"
        compileSdk = 36
        minSdk = 23
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "divkit-multiplatform"
            isStatic = true
        }
        val sdk = if (iosTarget.konanTarget == KonanTarget.IOS_ARM64) "iphoneos" else "iphonesimulator"
        val arch = if (iosTarget.konanTarget == KonanTarget.IOS_X64) "x86_64" else "arm64"
        val spmBuildDir = layout.buildDirectory.dir("spm/${iosTarget.name}")
        val headersDir = spmBuildDir.map { it.dir("headers") }
        val buildBridge = tasks.register<Exec>("buildDivKitKMP${iosTarget.name.replaceFirstChar { it.uppercase() }}") {
            group = "build"
            description = "Builds the current DivKit Swift package for ${iosTarget.name} cinterop."
            onlyIf { HostManager.hostIsMac }
            // Xcode tracks Swift sources and toolchain changes in its own incremental build.
            commandLine(
                "bash", project.file("scripts/build-spm-bridge.sh"),
                sdk, arch, spmBuildDir.get().asFile.absolutePath
            )
        }
        val interop = iosTarget.compilations.getByName("main").cinterops.create("DivKitKMP") {
            definitionFile.set(project.file("src/nativeInterop/cinterop/DivKitKMP.def"))
            includeDirs(headersDir)
        }
        tasks.named(interop.interopProcessingTaskName) {
            dependsOn(buildBridge)
            inputs.file(headersDir.map { it.file("DivKitKMP-Swift.h") })
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.ui.tooling)
            implementation(libs.div)
            implementation(libs.div.core)
            implementation(libs.div.picasso)
            implementation(libs.div.shimmer)
        }
    }
}
