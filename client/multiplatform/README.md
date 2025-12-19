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
