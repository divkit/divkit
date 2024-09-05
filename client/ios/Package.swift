// swift-tools-version:5.9

import Foundation
import PackageDescription

let vgsl = {
  let version = Version("6.0.0")
  return (
    package: Package.Dependency.package(url: "https://github.com/yandex/vgsl.git", from: version),
    packageName: "vgsl"
  )
}()

let swiftSettings: [SwiftSetting] = [.unsafeFlags(["-warnings-as-errors"])]

let package = Package(
  name: "DivKit",
  platforms: [
    .iOS(.v13),
  ],
  products: [
    .library(name: "DivKit", targets: ["DivKit"]),
    .library(name: "DivKitExtensions", targets: ["DivKitExtensions"]),
    .library(name: "DivKitMarkdownExtension", targets: ["DivKitMarkdownExtension"]),
    .library(name: "LayoutKit", targets: ["LayoutKit"]),
    .library(name: "LayoutKitInterface", targets: ["LayoutKitInterface"]),
    .library(name: "Serialization", targets: ["Serialization"]),
  ],
  dependencies: [
    vgsl.package,
    .package(url: "https://github.com/apple/swift-markdown.git", from: "0.4.0"),
  ],
  targets: [
    .target(
      name: "DivKit",
      dependencies: [
        "LayoutKit",
        .product(name: "VGSL", package: vgsl.packageName),
        "Serialization",
      ],
      path: "DivKit",
      exclude: [
        "generator_config.json",
        "shared_data_generator_config.json",
      ],
      swiftSettings: swiftSettings
    ),
    .target(
      name: "DivKitExtensions",
      dependencies: [
        "DivKit",
      ],
      path: "DivKitExtensions",
      swiftSettings: swiftSettings
    ),
    .target(
      name: "DivKitMarkdownExtension",
      dependencies: [
        "DivKit",
        "LayoutKit",
        .product(name: "VGSL", package: vgsl.packageName),
        .product(name: "Markdown", package: "swift-markdown"),
      ],
      path: "DivKitMarkdownExtension",
      swiftSettings: swiftSettings
    ),
    .target(
      name: "LayoutKit",
      dependencies: [
        .product(name: "VGSL", package: vgsl.packageName),
        "LayoutKitInterface",
      ],
      path: "LayoutKit/LayoutKit",
      swiftSettings: swiftSettings
    ),
    .target(
      name: "LayoutKitInterface",
      dependencies: [
        .product(name: "VGSL", package: vgsl.packageName),
      ],
      path: "LayoutKit/Interface",
      swiftSettings: swiftSettings
    ),
    .target(
      name: "Serialization",
      dependencies: [
        .product(name: "VGSL", package: vgsl.packageName),
      ],
      path: "Serialization",
      swiftSettings: swiftSettings
    ),
  ]
)
