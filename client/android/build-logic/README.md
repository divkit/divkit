# KMP library conventions

`divkit.convention.library-kmp` configures Android. Modules can also declare
`jvm()` in their `kotlin` block.

To opt a module into iOS, replace `divkit.convention.library-kmp` with
`divkit.convention.library-kmp-ios`:

```kotlin
plugins {
    id("divkit.convention.library-kmp-ios")
    id("divkit.convention.publishing-module-kmp")
}

kotlin {
    android {
        namespace = "com.yandex.div.example"
    }
}
```

The iOS convention includes the base KMP convention and adds `iosArm64` and
`iosSimulatorArm64`. Kotlin's default hierarchy supplies `iosMain` and `iosTest`
shared by both targets. Keep module-specific extensions to
`applyDefaultHierarchyTemplate`, such as `jvmAndAndroid`, unchanged; they extend
the same default hierarchy. Do not wire these source sets with manual
`dependsOn` edges.

Opt in only after the module and its dependencies support Native compilation,
including required `actual` declarations and portable common tests. Applying the
base convention to another module does not enable iOS for it. Target declarations
are independent of the host OS so the published target model stays consistent;
building and running iOS test executables requires macOS with Xcode.

## Core iOS build and CI

`core` applies the iOS convention by default. No flag is needed to build or test it:

```sh
./gradlew \
  :core:compileKotlinIosArm64 :core:compileKotlinIosSimulatorArm64 \
  :core:iosSimulatorArm64Test
```

The `kmp_core_ios` action in `public/client/android/a.yaml` runs these tasks on
macOS ARM64, plus the Android compilation and JVM tests. It also checks that
Simulator tests actually executed.

## Publication

The Android publishing jobs run on macOS so that Android, JVM and iOS artifacts
and root KMP metadata are published together with the same version. No additional
Gradle flag is needed. `core` publishes `core-iosarm64` and `core-iossimulatorarm64`
alongside its existing Android/JVM artifacts.

The `kmp_core_ios` action also publishes the project's libraries to an isolated
file-based Maven repository and checks the platform artifact references for
`core`. This check does not upload artifacts to Bucket or Maven Central.
