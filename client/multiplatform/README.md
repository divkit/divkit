# DivKit Multiplatform (KMP)

Kotlin Multiplatform library that embeds DivKit into Kotlin Multiplatform apps. The repository includes:

- `divkit-multiplatform` — KMP library with a simple Kotlin API
- `sample/app` — KMP sample (Android + iOS targets)
- `sample/iosApp` — native iOS project with Swift Package Manager (SPM) integration

## Running

Open the project in Android Studio to run the Android sample. For iOS, use macOS with Xcode, JDK 17, and the Android SDK configured for Gradle. Open `sample/iosApp/iosApp.xcodeproj`, select the `iosApp` scheme and an iOS simulator, then run the app. Xcode resolves the local DivKit Swift package, links its `DivKitKMP` product, and builds the Kotlin `SampleApp` framework through Gradle.

To build the iOS sample from this directory:

```sh
xcodebuild -project sample/iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO -quiet
```

The local Swift package is at `../ios` and includes the `DivKitKMP` bridge alongside the DivKit sources. Gradle builds its `DivKitKMP` product to generate the Objective-C header used by Kotlin/Native cinterop, so the bindings follow the checked-out sources. CocoaPods is not required. The standalone Swift example is available at `divkit-multiplatform/DivKitKMP/Example/Example.xcodeproj`.

## Using the library in your KMP project

The library is published in Maven Central inside group `com.yandex.divkit.multiplatform` and versioned in accordance with the versions of the main DivKit library.

### 1) Gradle dependency

Add the library as a dependency of your shared module:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.yandex.divkit.multiplatform:divkit-multiplatform:<divkit-version>")
        }
    }
}
```

### 2) iOS: add the `DivKitKMP` product through SPM

In Xcode, choose **File → Add Package Dependencies**, enter [the DivKit iOS package URL](https://github.com/divkit/divkit-ios), and select **Exact Version** with the same `<divkit-version>` as the Maven dependency. Add the `DivKitKMP` product to your iOS application target. It includes the required `DivKit` and `DivKitExtensions` dependencies.

Configure your shared Kotlin module to produce a static iOS framework:

```kotlin
kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
}
```

Connect that framework to the application using [Kotlin's direct integration](https://kotlinlang.org/docs/multiplatform-direct-integration.html). The application links both the Kotlin framework and the SPM product; the Maven dependency contains the Kotlin bindings, while SPM provides the native implementation. Update their versions together.

When migrating an existing app, remove the `DivKitKMP` entry from its `Podfile` and remove any explicit DivKit pods that are replaced by SPM. Update the Pods integration if the app still uses other pods. If the Kotlin framework itself was integrated through CocoaPods, follow the migration steps in Kotlin's direct integration guide as well.

### 3) Render a Div card in Kotlin multiplatform

Common code:

```kotlin
DivKit(dependencies) {
    var greeting by variable(name = "greeting", value = "Hello, Username")
    Column(
        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DivView(
            cardId = "Sample",
            jsonData = jsonString,
            modifier = Modifier.wrapContentSize()
        )
        Button(
            onClick = {
                greeting = "Hello, DivKit User"
            },
            content = {
                Text("Say \"Hello\"")
            }
        )
    }
}
```

Also you can setup some platform-specific dependencies in platform-specific code. For example:

In Android part:

```kotlin
val environment = DivKitEnvironment.Builder(baseContext = this)
    .imageLoaderFactory { ctx -> PicassoDivImageLoader(ctx) }
    .lifecycleOwner(this)
    .build()

setContent {
    inject(environment) {
        MainScreen()
    }
}
```

## Using a snapshot

The `divkit_multiplatform_publish_snapshot` CI flow publishes a Maven snapshot and attaches the matching native sources. Use both artifacts from the same CI launch:

1. Read the complete Maven version from the `build_number` resource (`buildNumber`) and use it as `<divkit-version>` in the Gradle dependency.
2. Add the snapshot repository to the repositories used by your shared module:

   ```kotlin
   maven("https://central.sonatype.com/repository/maven-snapshots/")
   ```

3. Download the `spm_sources` resource (`spm-snapshot.tar.gz`) and extract it, preserving its directory structure.
4. In Xcode, add the local package at `<extracted-directory>/client/ios` and link its `DivKitKMP` product to the app target. Keep the static Kotlin framework configuration described above.

The package includes the matching DivKit and `DivKitKMP` sources. Snapshot versions have no corresponding stable SPM tag, so use the archive from the same launch whenever you update the Maven snapshot.
