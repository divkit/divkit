# divkit
[![Pub](https://img.shields.io/pub/v/divkit.svg)](https://pub.dartlang.org/packages/divkit)
DivKit client on Flutter library.

## How it works

DivKit builds native views from JSON data.

JSON → DivData → DivKitView

- JSON – raw data with templates in DivKit format (see [DivKit schema](../../../public/schema)).

- DivData – data objects parsed from JSON.

- DivKitView — plain Flutter Widget

## Playground app

Use [DivKit playground app](example/lib/main.dart) to look through layout examples and supported functions.

---

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

---
## Supported features
Flutter client is in development, feel free to contribute and help community use DivKit on this platform.

Supported components (may contain unavailable features for more info look at documentation):
+ text
+ image
+ container
+ state
+ input
+ gallery
+ custom

---
## DivKit Flutter. Quick start.
### Build app and draw your first DivKitView.

1. Add dependency to pubspec.yaml:
    ```yaml
    dependencies:
        divkit: any
    ```
2. Import library
    ```dart
    import 'package:divkit/divkit.dart';
    ```
3. Resolve your layout with DivKitData:

    from JSON:
    ```dart
    final data = DefaultDivKitData.fromJson(json); // Map<String, dynamic>
    ```
    or from card and templates:
    ```dart
    final data = DefaultDivKitData.fromScheme(
        card: json['card'], // Map<String, dynamic>
        templates: json['templates'], // Map<String, dynamic>?
    );
    ```

4. Use DivKitView inside your widget tree with layout passed by param "data":
    ```dart
    DivKitView(
        data: data,
    )
    ```
Please ensure that there is Directionality widget in the tree.

Optionally, you can pass customs handler, actions handler and other params to configure DivKitView behavior:
```dart
    DivKitView(
      data: data,
      customHandler: MyCustomHandler(), // DivCustomHandler?
      actionHandler: MyCustomActionHandler(), // DivActionHandler?
      variableStorage: MyOwnVariableStorage(), // DivVariableStorage?
    )
```
Important! If you wish to work with default div-actions and use your own actionHandler don't forget to inherit **DefaultDivActionHandler**.