# DivKit for iOS client library

## How it works

DivKit builds native views from JSON data.

JSON → DivData → LayoutKit block → UIView

- JSON – raw data with templates in DivKit format (see [DivKit schema](https://github.com/divkit/divkit/tree/main/schema)).

- DivData – data objects parsed from JSON.

- LayoutKit is a library that builds UIKit views using declarative approach.

Although DivKit produces UIKit views, it can be easyly used in SwiftUI apps (see [demo application](https://github.com/divkit/divkit/tree/main/client/ios/DivKitDemoApp)).

## DivKit Project

`DivKit.xcodeproj` – main Xcode project that contains all DivKit modules, tests and demo application.

Use `DivKitDemoApp` scheme to build and run demo application.

Use `UnitTests` sheme to run all unit tests.

Use `SnapshotTests` scheme to run snapshot tests.

## Sample Project

`Sample/DivKitSample.xcodeproj` – sample project with simple DivKit integration using CocoaPods.

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
