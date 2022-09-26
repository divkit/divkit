# DivKit for iOS client library

## How it works

DivKit builds native views from JSON data.

JSON → DivData → LayoutKit block → UIView

- JSON – raw data with templates in DivKit format (see [DivKit schema](../../schema)).

- DivData – data objects parsed from JSON.

- LayoutKit is a library that builds UIKit views using declarative approach.

Although DivKit produces UIKit views, it can be easyly used in SwiftUI apps (see [playground application](DivKitPlayground)).

## Installing DivKit

DivKit supports [Swift Package Manager](https://www.swift.org/package-manager/) and [CocoaPods](https://cocoapods.org/).

Use this repository to install DivKit using Swift Package Manager: https://github.com/divkit/divkit-ios.git.

Use `DivKit` podspec to install DivKit using CocoaPods:
```bash
pod 'DivKit'
pod install
```

## DivKit Project

`DivKit.xcodeproj` – main Xcode project that contains all DivKit modules, tests and playground application.

Use `DivKitPlayground` scheme to build and run playground application.

Use `UnitTests` sheme to run all unit tests.

Use `SnapshotTests` scheme to run snapshot tests.

## Sample Project

`Sample/DivKitSample.xcodeproj` – sample project with simple DivKit integration using Swift Package Manager.

---

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
