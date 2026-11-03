# DivKit for iOS client library

[![SwiftPM](https://img.shields.io/badge/SPM-supported-DE5C43)](https://github.com/divkit/divkit-ios)
![Size](https://img.shields.io/badge/ipa%20size-~6%20MB-blue)

## How it works

DivKit builds native views from JSON data.

JSON → DivData → DivView

- JSON – raw data with templates in DivKit format (see [DivKit schema](../../schema)).

- DivKitComponents – DivView dependencies.

- DivView - plain UIKit View.

Although DivKit produces UIKit views, it can be easily used in SwiftUI apps (see [playground application](DivKitPlayground)).

## Installing DivKit

Install DivKit with [Swift Package Manager](https://www.swift.org/package-manager/) using this repository: https://github.com/divkit/divkit-ios.git.

New DivKit versions are no longer published to CocoaPods. Previously published pods remain available; see [Specs](https://github.com/divkit/divkit-ios/tree/main/Specs) for the last published versions.

## DivKit Project

`DivKit.xcodeproj` – main Xcode project that contains all DivKit modules, tests and playground application.

Use `DivKitPlayground` scheme to build and run playground application.

Use `UnitTests` scheme to run all unit tests.

Use `SnapshotTests` scheme to run snapshot tests.

Use `DivKitRegressionUITests` scheme to run automated UI scenarios in `DivKitUITestsHostApp`.
The host shows a single card and provides the test connection for `div_action`.

## Sample Projects

`Samples/UIKitIntegration/DivKitSample.xcodeproj` – sample project with simple DivKit integration using Swift Package Manager and UIKit.
`Samples/SwiftUIIntegration/DivKitSample.xcodeproj` – sample project with simple DivKit integration using Swift Package Manager and SwiftUI.

---

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).
