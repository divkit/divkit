# DivKit for iOS client library

[![SwiftPM](https://img.shields.io/badge/SPM-supported-DE5C43)](https://github.com/divkit/divkit-ios)
[![CocoaPods](https://img.shields.io/badge/CocoaPods-supported-DE5C43)](https://github.com/divkit/divkit-ios/tree/main/Specs)
![Size](https://img.shields.io/badge/ipa%20size-~6%20MB-blue)

## How it works

DivKit builds native views from JSON data.

JSON → DivData → DivView

- JSON – raw data with templates in DivKit format (see [DivKit schema](../../schema)).

- DivKitComponents – DivView dependencies.

- DivView - plain UIKit View.

Although DivKit produces UIKit views, it can be easyly used in SwiftUI apps (see [playground application](DivKitPlayground)).

## Installing DivKit

DivKit supports [Swift Package Manager](https://www.swift.org/package-manager/) and [CocoaPods](https://cocoapods.org/).

Use this repository to install DivKit using Swift Package Manager: https://github.com/divkit/divkit-ios.git.

Add `DivKit` pod into your application `Podfile` to install DivKit using CocoaPods:
```
source 'https://github.com/divkit/divkit-ios.git'

target 'MyApp' do
  pod 'DivKit'
end
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
