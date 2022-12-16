# DivKit Android. Quick start.
## Connect the library.
To connect the library to the project, all you have to do is add three dependencies to build.gradle:
```groovy
implementation 'com.yandex.div:div-core:3.0.1'
implementation 'com.yandex.div:div:3.0.1'
implementation 'com.yandex.div:div-json:3.0.1'
```
## Build an MVP.
Code-wise, connecting DivKit consists of three parts: initializing DivConfiguration, parsing the JSON file with the layout, and adding DivView to your view hierarchy.
#### Initialize the configuration.
DivConfiguration is basically just a container for client dependencies. We'll talk about them below. DivImageLoader is the only required dependency, and you can use your ImageManager to implement it. [Configuration example](https://github.com/divkit/divkit/blob/main/client/android/divkit-demo-app/src/main/java/com/yandex/divkit/demo/div/DivUtils.kt#L37) from our demo app.
#### Parse the div layout.
The div card is made of two parts: the templates and the layout. The templates and the card layout are often parsed separately. In this example, we overlook that and parse both the templates and the layout in a single JSON file.
```kotlin
fun JSONObject.asDivDataWithTemplates(): DivData {
   val templates = getJSONObject("templates")
   val card = getJSONObject("card")
   val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
   environment.parseTemplates(templates)
   return DivData(environment, card)
}
```
You can find examples of div cards in the [samples](https://github.com/divkit/divkit/tree/main/test_data/samples) or [testing](https://github.com/divkit/divkit/tree/main/test_data/regression_test_data) sections of the demo app.
#### Add DivView to the hierarchy.
Adding DivView to the view hierarchy is similar to adding regular views:
```kotlin
val divView = DivView(DivContext(baseContext = this, configuration = config))
yourViewGroup.addView(divView)
divView.setData(divData, DivDataTag("your_unique_tag_here"))
```
Congratulations! You've drawn your first div view. The section about DivLogger below describes the importance of DivDataTag.


## All about DivConfiguration.
#### Event handling. ActionHandler.
`DivConfiguration.Builder#actionHandler`. Takes as an argument the descendant of the DivActionHandler class, where you're recommended to redefine two methods:
```java
@CallSuper
public boolean handleAction(@NonNull DivAction action, @NonNull DivViewFacade view) {
   // handle action
}


@CallSuper
public boolean handleAction(@NonNull DivVisibilityAction action, @NonNull DivViewFacade view) {
   // handle visibility action
}
```
This handler will handle `visibility_actions`, which are called when an element appears on the screen, and `actions`, which are called when the user clicks the view. Actions are set in the layout in the following way:
```json
"actions": [
   {
      "log_id": "collapse_comment",
      "url": "div-action://set_state?state_id=0/content/expanded/comments/collapsed"
   },
   {
      "log_id": "open_yandex",
      "url": "https://yandex.ru"
   }
],
"visibility_actions": [
   {
       "log_id": "item_log_id",
       "url": "div-action://hide_tooltip?id=tooltip",
       "log_limit": 0,
       "visibility_duration": 1000,
       "visibility_percentage": 80
  }
]
```
By default, divs can process URIs intended for any actions in the layout using the `div-action` scheme:
`div-action://download?url=<patch_url>` — Download more layout from the specified source.
div-action://set_state?state_id=<div_data_state_id/div_id/state_id>&temporary=<bool>` — Change the layout state.
`div-action://show_tooltip?id={$id_here}` — Show the tooltip for the specified element.
`div-action://set_current_item?id=<div_id>&item=<item_index>` — Search for an element by its index.
`div-action://timer?action=<action>&id=<id>` — Manage timers.
#### Log events. DivLogger.
`DivConfiguration.Builder#div2Logger`. Takes the descendant of the Div2Logger interface as an argument.
The interface is quite clear. You can use it to log events in the format that you need, save them locally, and send them to the backend. But note the importance of `DivDataTag` here. Most likely, you want to distinguish between the cards that have identical layouts but different content in your logs. To do this, you can use the `Div2View#dataTag` field. You can also add the `id` field to all divs.
**Attention!** `DivDataTag` is also used for saving states and caching. For example, if you have a layout where the state changes frequently, DivDataTag must be the same only for the views whose state must be preserved. This is important for correct caching.
#### Patches. DivDownloader.
`DivConfiguration.Builder#divDownloader` takes the descendant of `DivDownloader` as the argument. It's used for loading heavy/optional parts of the layout. Let's say you have a large card with several states - for example, the card opens or it's a gallery with a lot of elements in it. It would be difficult and would take a long time to send the layout of all possible states for every card. You can use **patches** to save time.
To download patches, use `actions`: `div-action://download?url=<patch_url>`. You can view patch examples in our repository at [link](https://github.com/divkit/divkit/tree/main/test_data/unit_test_data/patches). For example, this [container](https://github.com/divkit/divkit/blob/main/test_data/unit_test_data/patches/container/container-initial.json) uses this [patch](https://github.com/divkit/divkit/blob/main/test_data/unit_test_data/patches/container/container-patch-partial.json), which replaces part of the child elements.
This means that a certain action is set in the layout according to the scheme `div-action://download?url=<patch_url>`.
When this action is activated, the `DivDownloader#downloadPatch` method is called in the code, where you download patches in your own stack the way you want. Regarding the contract, the code looks like this:
```kotlin
override fun downloadPatch(
   divView: Div2View,
   downloadUrl: String,
   callback: DivPatchDownloadCallback)
```
1) `divView` — Div2View of the card that the patch should be attached to via the `callback#onSuccess` call. You can also attach patches without this scheme via `divView#applyPatch`.
2) `downloadUrl` — The address where your backend should send the patch for this particular card. As in the example above. In the method body, we take the layout from `downloadUrl` (be careful — it's called on the main flow!) and parse it in the way described above.
3) `callback` — Call `onFail` if the request failed and `onSuccess` if it was successful.
#### Custom View. DivCustomViewAdapter.
`DivConfiguration.Builder#divCustomViewAdapter` accepts the descendant of `DivCustomViewAdapter`. Its contract is similar to RecyclerView.Adapter:
```kotlin
   fun createView(
       div: DivCustom,
       View: Div2View
   ): View


   fun bindView(
       view: View,
       div: DivCustom,
       divView: Div2View
   )
  
   fun isCustomTypeSupported(type: String): Boolean


   fun release(view: View, div: DivCustom)
```
DivCustom is used for supporting your own div types.
Note: unlike with `RecyclerView.Adapter`, divs send the release event for the views that will definitely not be used anymore. So you can either clean up some data or reuse the same view and pass it to the next CreateView call.
For an example of `DivCustom`, see our video custom implementation. The layout is [here](https://github.com/divkit/divkit/blob/main/test_data/regression_test_data/video/video_controls.json). You can view the adapter implementation [here](https://github.com/divkit/divkit/blob/main/client/android/video-custom/src/main/java/com/yandex/div/video/custom/VideoCustomAdapter.kt).
#### Extensions.
If a div layout is suitable for you, but you want to add an action, use extensions. They're also suitable if you need to edit data in divs from the client, and the main layout must come from the server.
Examples of our extensions: [pinch-to-zoom](https://github.com/divkit/divkit/tree/main/client/android/div-pinch-to-zoom/src/main/java/com/yandex/div/zoom) and [lottie](https://github.com/divkit/divkit/tree/main/client/android/div-lottie/src/main/java/com/yandex/div/lottie).
They're added via the `DivConfiguration.Builder#extension` call, for example:
```kotlin
builder.extension(DivPinchToZoomExtensionHandler(DivPinchToZoomConfiguration.Builder(this).build()))
 .extension(DivLottieExtensionHandler(rawResProvider, logger))
```
In `DivConfiguration.Builder#extension`, you'll need to pass the descendant to `DivExtensionHandler` with the following contract:
```kotlin
interface DivExtensionHandler {
   fun matches(div: DivBase): Boolean
   fun beforeBindView(divView: Div2View, view: View, div: DivBase) = Unit
   fun bindView(divView: Div2View, view: View, div: DivBase)
   fun unbindView(divView: Div2View, view: View, div: DivBase)
}
```
In the div card, the use of extensions looks like this:
```json
{
 "type": "gif_image",
 "extensions": [
   {
     "id": "lottie",
     "params": {
       "lottie_url": "https://assets9.lottiefiles.com/packages/lf20_kl6ebx8f.json",
       "repeat_count": 3,
       "repeat_mode": "reverse"
     }
   }
 ],
 "gif_url": "https://i.gifer.com/VhdJ.gif"
}
```
**Note**: The `unbindView` method implements the contract for every extension atLeastOnce, not `exactlyOnce`, so make sure that this behavior is expected.
To avoid `Inheritance from an interface with '@JvmDefault' members is only allowed with -Xjvm-default option`, specify the compiler flag `-Xjvm-default=all` when inheriting from `DivExtensionHandler`.
#### Fonts. TypefaceProvider.
The `TypefaceProvider` interface is used for adding fonts to the layout. For example, let's see how to add the YandexSans font.
1) Implement fonts in build.gradle:
```groovy
   implementation "com.yandex.android.uikit:fonts-ya-regular:$versions.uikit"
   implementation "com.yandex.android.uikit:fonts-ya-medium:$versions.uikit"
   implementation "com.yandex.android.uikit:fonts-ya-light:$versions.uikit"
   implementation "com.yandex.android.uikit:fonts-ya-bold:$versions.uikit"
```
2) Inherit `TypefaceProvider` and return Typeface via `ResourcesCompat.getFont(context, R.font.ys_text_{font_type_here})` in the methods.
3) Pass your `TypefaceProvider` implementation in the `DivConfiguration.Builder#typefaceProvider` builder.


To use YandexSansDisplay:
1) In the JSON of the div itself, specify `font_family="display"`.
2) If a standard font is used after that, check whether `YandexSansDisplayDivTypefaceProvider` is passed to `DivConfiguration`, for example, as in [Div2Activity](https://github.com/divkit/divkit/blob/main/client/android/divkit-demo-app/src/main/java/com/yandex/divkit/demo/div/Div2Activity.kt#L99).
#### Word wrap support. SupportHyphenation.
In `DivConfiguration.Builder#supportHyphenation`, you can enable word wrap support. By default, the function is disabled.
#### Error and warning display. VisualErrors.
In `DivConfiguration.Builder#visualErrorsEnabled`, you can enable error and warning display. For example, you can enable error display in the debug app version and make sure that the app layout is correct. By default, the function is disabled.
#### Accessibility support. EnableAccessibility.
Divs support [accessibility](https://developer.android.com/guide/topics/ui/accessibility). By default, this feature is disabled. You can enable it using `DivConfiguration#enableAccessibility'.
An example of using accessibility in the layout:
```json
{
   "type": "button",
   "text": "Play",
   "accessibility": {
       "description": "Play music",
       "type": "button",
       "hint": "Tap on the button to play music."
 }
}
```
#### Tooltips. TooltipRestrictor.
Divs can show any views as tooltips. To declare tooltips in the layout, add the `"tooltips"` field in the div and specify where a new view will pop up relative to the div, how much will be displayed, and what the div layout is. For example, see a [card from the demo app](https://github.com/divkit/divkit/blob/main/test_data/regression_test_data/animations/article.json#L281).
You can manage tooltip display through any action with directives:
`div-action:show_tooltip?id={your_tooltip_id}` and `div-action:hide_tooltip?id={your_tooltip_id}`.
You can also redefine the tooltip display logic (or show your own tooltip) on the client side. To do this, provide a descendant to `DivTooltipRestrictor` in `DivConfiguration.Builder#tooltipRestrictor` and implement the necessary methods in it.
#### longtap setup. EnableLongtapActionsPassingToChild.
The `longtap_actions` events don't pop up (they work on the main container, but aren't affected by the child ones) if the nested element has `actions`. This is the standard Android and iOS logic, but if you need `longtap_actions` to extend to all child elements, regardless of whether they have `actions`, you can change the logic by calling the `DivConfiguration.Builder#enableLongtapActionsPassingToChild` method.