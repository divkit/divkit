# DivKit for Android client library

## How it works

DivKit builds native views from JSON data.

JSON → DivData → Android View

- JSON – raw data with templates in DivKit format (see [DivKit schema](../../schema)).

- DivData – data objects parsed from JSON.

- Android View - Plain old Android view system

## DivKit Project

`build.gradle` – main Android Studio project with everything Android related.

Use `divkit-demo-app` configuration to build and launch our demo app.

## Sample Project

`sample` – sample project with a very simple DivKit integration

---

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

---

## DivKit Android. Quick start.

### Build MVP and draw first DivView.
To get started with drawing your first view, follow these three simple steps:
- **Initialize the Configuration**: Begin by creating an instance of `DivConfiguration`. The only required parameter is `DivImageLoader`. You have the option to implement your own custom implementation of this interface or use one of our implementations: `PicassoDivImageLoader` or `GlideImageLoader`. For an example of how to create a `DivConfiguration` in our demo app, you can refer to [this link](https://github.com/divkit/divkit/blob/05a420e0861517e68a3fc68c853fb640af092df2/client/android/divkit-demo-app/src/main/java/com/yandex/divkit/demo/div/Div2Activity.kt#L98).
   Each parameter of `DivConfiguration#builder` will be described below.
- **JSON-layout parsing**: A div-layout comprises two crucial parts: `"templates"` and `"cards"`. In our example, both parts are contained within a single JSON-file. However, in your code, you are free to separate them if it suits your needs. Your card parsing method might resemble something along these lines:
```kotlin 
fun JSONObject.asDiv2DataWithTemplates(): DivData {
    val templates = getJSONObject("templates")
    val card = getJSONObject("card")
    val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
    environment.parseTemplates(templates)
    return DivData(environment, card)
}
``` 
You are free to use our examples of div layouts: [sample cards](https://github.com/divkit/divkit/tree/main/test_data/samples) and [cards for testing](https://github.com/divkit/divkit/tree/main/test_data/regression_test_data).

- **Adding Div2View to your layout** is pretty simple:
```kotlin 
    val div2View = Div2View(Div2Context(baseContext = this, configuration = config))
    yourViewGroup.addView(div2View)
    div2View.setData(divData, DivDataTag("your_unique_tag_here"))
```
Congrats, you just drawn your fist Div2View! To understand why DivDataTag is so important check section about Div2Logger.

### Everything you need to know about DivConfiguration.

#### Handling actions in your layout. *DivConfiguration.Builder#actionHandler*.
To learn more about actions, please refer to [this section](https://divkit.tech/en/doc/overview/concepts/interaction.html?lang=en) in our documentation. DivKit will automatically handle all actions defined in our schema. However, you might want to trigger custom code from your layout. To achieve this, you can implement your own `DivActionHandler` interface.

As an example, you can refer to our implementations of [implementation in the SamplesActivity](https://github.com/divkit/divkit/blob/368ffde173d928c1f825f093efe87ba04b800b65/client/android/divkit-demo-app/src/main/java/com/yandex/divkit/demo/div/Div2Activity.kt#L169) from our demo app. Don't forget to call `super.handleAction` if action wasn't handled by your custom handler!

#### Logging actions. *DivConfiguration.Builder#div2Logger*.
Interface `Div2Logger` is pretty simple. When you define your implementation, DivKit will log each action for you. Additionally, it's essential to note that you can set the `DivDataTag` to distinguish between different `Div2View` in your logs. You can also specify the `"id"` field when describing views within your layout.

As an example you can reffer to our implementation of [DemoDiv2Logger](https://github.com/divkit/divkit/blob/R-28.4/client/android/divkit-demo-app/src/main/java/com/yandex/divkit/demo/div/DemoDiv2Logger.kt).

#### Using custom views inside DivKit layout. *DivConfiguration.Builder#divCustomContainerViewAdapter*
You can define any custom view you want, set some custom-name and then use in inside your layout.

As an example, you can refer to our [video-custom implementation](https://github.com/divkit/divkit/tree/R-28.4/client/android/video-custom/src/main/java/com/yandex/div/video/custom). [Here](https://github.com/divkit/divkit/tree/R-28.4/client/android/video-custom/src/main/java/com/yandex/div/video/custom/VideoCustomAdapter.kt) we define implementation of custom adapter. Take note of the method `VideoCustomAdapter#isCustomTypeSupported`, which specifies that only custom elements with the type `"custom_video"` can be handled by this adapter. Once you've created the `VideoCustomAdapter`, simply pass it to DivConfiguration, and your video_custom element is ready to be used within your [JSON layout](https://github.com/divkit/divkit/blob/368ffde173d928c1f825f093efe87ba04b800b65/test_data/regression_test_data/video/video_controls.json#L60C29-L60C29).
<details>
  <summary>Custom View Groups</summary>

   The implementation does not differ significantly from the implementation of regular custom views. The only notable difference is that you will need to extend the handling of the `createView` and `bindView` events deeper down the hierarchy of views. Fortunately,  we've introduced a static method, `DivCustomContainerViewAdapter#getDivChildFactory`, which can create and bind these wondrous views. For instance, if there are some items in your layout:
```json
{
   "type": "custom",
   "id": "new_custom_container_1",
   "items": [
      {
         "type": "text",
         "news_item_text": "This is div-text item 1"
      },
      {
         "type": "text",
         "news_item_text": "This is div-text item 2"
      }
   ],
   "custom_type": "new_custom_container_1"
}
```
You can override `DivCustomContainerViewAdapter#CreateView` like this:
```kotlin
div.items!!.forEach {
   val childDivView = getDivChildFactory(divView).createChildView(
           it,
           path,
           divView
   )
   (customView as ViewGroup).addView(childDivView)
}
```
And then bind child items inside `DivCustomContainerViewAdapter#BindView`:
```kotlin
for (i in div.items!!.indices) {
   val childDivView = customView.getChildAt(i)
   val childDiv = div.items!![i]
   getDivChildFactory(divView).bindChildView(
           childDivView,
           childDiv,
           path,
           divView
   )
}
```
</details>

#### Custom fonts. *DivConfiguration.Builder#typefaceProfiders*
You can implement your own DivTypefaceProvider and use them inside our layout. As an example, you can refer to [YandexSansDisplayDivTypefaceProvider](https://github.com/divkit/divkit/blob/main/client/android/fonts/src/main/java/com/yandex/div/font/YandexSansDisplayDivTypefaceProvider.kt). Then we put `{"display" to YandexSansDisplayDivTypefaceProvider()}` to `DivConfiguration.Builder#typefaceProfiders`. And from now on you can set `"font_family": "display"` to text in your json. If you want to override standard font, just pass your typeface provider to `DivConfiguration.Builder#typefaceProfider`.

#### Word wrap. *DivConfiguration.Builder#supportHyphenation*
In `DivConfiguration.Builder#supportHyphenation`, you can enable word wrap support by setting the Hyphenation option. Please note that this function is disabled by default.

#### Menu items. *DivConfiguration.Builder#overrideContextMenuHandler*.
In your layout, you can easily set up a pop-up menu by specifying a `div-action` for the desired div, which includes the `menu_items` field.

From a coding perspective, in `DivConfiguration.Builder#overrideContextMenuHandler` you can just turn off them. You can use it to determine whether you want to display as custom menus (by capturing the necessary actions in `DivActionHandler`).

#### Longtap actions settings. *DivConfiguration.Builder#enableLongtapActionsPassingToChild*
By default, longtap_actions events do not propagate (they work on the main container, but are not captured by child elements) if the nested element has its own actions. This behavior aligns with the standard logic of Android and iOS platforms.

However, if you have a specific need for longtap_actions to propagate to all child elements you can change this by using the `DivConfiguration.Builder#enableLongtapActionsPassingToChild` method.
