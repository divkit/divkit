# DivKit <img alt="Playground app" src="https://raw.githubusercontent.com/divkit/divkit/main/readme_pictures/app_screen.png" width="30%" align="right" style="margin:20px;">
[![Pub](https://img.shields.io/pub/v/divkit.svg)](https://pub.dartlang.org/packages/divkit)
[![GitHub Stars](https://img.shields.io/github/stars/divkit/divkit)](https://github.com/divkit/divkit/stargazers)

DivKit 🐋 is an open source Server-Driven UI (SDUI) framework. It allows you to roll out server-sourced updates to different app versions. Also, it can be used for fast UI prototyping, allowing you to write a layout once and then ship it anywhere using Flutter. DivKit is an excellent choice to start using server-driven UI in your project because it can be easily integrated as a simple view in any part of your app. At the starting point, you don’t need a server integration. You can include all JSON on the client-side to try it in a real-world application.

Also, we’ve made a [sandbox](https://divkit.tech/playground) for you to experiment with. You can try different samples in the web editor and see the results. You can use the [DivKit website](https://divkit.tech/en) to find a lot of handy samples and documentation, but feel free to ask us anything in the Telegram community chat.

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Обзор на Хабр](https://habr.com/ru/companies/yandex/articles/814187).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

## How it works

DivKit builds native views from JSON data.

JSON → DivData → DivKitView

- JSON – raw data with templates in DivKit format (see [DivKit schema](https://github.com/divkit/divkit/tree/main/schema)).

- DivData – data objects parsed from JSON (see [Generated DTO](https://github.com/divkit/divkit/tree/main/client/flutter/divkit/lib/src/schema)).

- DivKitView — plain Flutter Widget (you use it directly)

## Playground app
Since the Flutter client does not support full-fledged launch on the web, therefore, in order to poke the functionality, you need to run an example of the current library. Use [DivKit playground app](https://github.com/divkit/divkit/tree/main/client/flutter/divkit/example) to look through layout examples and supported functions.

The main part of samples is stored in monorepositry... Before starting, call the following command from `client/flutter/divkit` to create a soft link to them: 
```shell
  ./tool/get_test_data.sh
```

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
    final data = DefaultDivKitData.fromJson(json); // json is Map<String, dynamic>
    ```
    or from card and templates:
    ```dart
    final data = DefaultDivKitData.fromScheme(
        card: json['card'], // Map<String, dynamic>
        templates: json['templates'], // Map<String, dynamic>?
    );
    ```

    P.s. The process of building a DTO is quite expensive, so it is better to create it outside the widget in order to avoid frame loss.

    Build and preload data to ensure instant rendering:
    ```dart
    await data.build(); 
    // Or sync variant for small layouts
    // data.buildSync();
    await data.preload();  // Provide the storage built into DivKitView
    ```

4. Use DivKitView inside your widget tree with layout passed by param "data":
    ```dart
    DivKitView(
        data: data, // DivKitData
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

   P.s. If you wish to work with default div-actions and use your own actionHandler don't forget to inherit **DefaultDivActionHandler**.