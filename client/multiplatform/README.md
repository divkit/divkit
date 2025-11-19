# DivKit Multiplatform (KMP)

Kotlin Multiplatform library that embeds DivKit into Kotlin Multiplatform apps. The repository includes:

- `divkit-multiplatform` — KMP library with a simple Kotlin API
- `sample/app` — KMP sample (Android + iOS targets)
- `sample/iosApp` — native iOS project with CocoaPods integration

## Running

Open the project in Android Studio. You can run both Android and iOS targets directly from the IDE. For iOS, ensure CocoaPods is installed; Android Studio will run the necessary CocoaPods steps for the sample. When integrating the library into your own app, make sure to add the `DivKitKMP` pod as described below.

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
import com.yandex.divkit.multiplatform.DivKitFactory
import com.yandex.divkit.multiplatform.makeDivKitFactory
import com.yandex.divkit.multiplatform.dependencies.ActionHandler
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import com.yandex.divkit.multiplatform.dependencies.ErrorReporter

val factory: DivKitFactory = makeDivKitFactory(
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

Also you can setup some platform-specific dependencies in platform-specific code. For example:

In Android part:

```kotlin
DivKitAndroidEnvironment.set(
    DivKitEnvironment(
        imageLoaderFactory = { ctx ->
            PicassoDivImageLoader(ctx)
        },
        lifecycleOwner = null
    )
)
```
