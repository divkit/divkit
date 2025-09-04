# DivKit Multiplatform (KMP)

Kotlin Multiplatform library that embeds DivKit into Kotlin Multiplatform apps. The repository includes:

- `divkit-multiplatform` — KMP library with a simple Kotlin API
- `sample/app` — KMP sample (Android + iOS targets)
- `sample/iosApp` — native iOS project with CocoaPods integration

## Running

Open the project in Android Studio. You can run both Android and iOS targets directly from the IDE. For iOS, ensure CocoaPods is installed; Android Studio will run the necessary CocoaPods steps for the sample. When integrating the library into your own app, make sure to add the `DivKitKMP` pod as described below.

## Using the library in your KMP project

### 1) Gradle dependency

Add the library as a dependency of your shared module:

#### Snapshots (current distribution)

This library is currently available only as a Maven snapshot. Add the Sonatype snapshots repository and use the latest `-SNAPSHOT` version.

Add the repository (project-level):

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}
```

Declare the dependency in your shared module:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.yandex.div:divkit-multiplatform:<latest-SNAPSHOT>")
        }
    }
}
```

Latest snapshot version - `32.15.0-20250830.000814-SNAPSHOT`

### 2) iOS: add the `DivKitKMP` CocoaPod

The iOS implementation relies on a small bridging Pod `DivKitKMP` to correctly link Obj-C symbols. Add it to your iOS app `Podfile` and run `pod install` from the iOS app folder:

```ruby
platform :ios, '13.0'
use_frameworks!

target 'YourApp' do
  pod 'DivKitKMP'

  # Other pods if needed
end
```

### 3) Render a Div card in Kotlin multiplatform

Common code:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.multiplatform.DivKitFactory
import com.yandex.div.multiplatform.makeComposeDivKitFactory
import com.yandex.div.multiplatform.dependencies.ActionHandler
import com.yandex.div.multiplatform.dependencies.DivKitDependencies
import com.yandex.div.multiplatform.dependencies.ErrorReporter

val factory: DivKitFactory = makeComposeDivKitFactory(
    dependencies = DivKitDependencies(
        actionHandler = object : ActionHandler {
            override fun handle(url: String) {
                // Handle actions (deeplink/URL)
            }
        },
        errorReporter = object : ErrorReporter {
            override fun report(cardId: String, message: String) {
                // Report parsing/rendering errors
            }
        }
    )
)

@Composable
fun DivScreen(cardId: String, json: String, modifier: Modifier = Modifier) {
    factory.DivView(cardId = cardId, jsonData = json, modifier = modifier)
}

fun setVariables() {
    factory.setGlobalVariables(
        mapOf(
            "user_name" to "Alex",
            "is_premium" to true
        )
    )
}
```
