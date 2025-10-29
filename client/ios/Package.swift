// swift-tools-version:5.9

import Foundation
import PackageDescription

let vgsl = {
  let version = Version("7.12.0")
  return (
    package: Package.Dependency.package(url: "https://github.com/yandex/vgsl.git", from: version),
    packageName: "vgsl"
  )
}()

let markdown = {
  let version = Version("0.6.0")
  return (
    package: Package.Dependency.package(
      url: "https://github.com/apple/swift-markdown.git", exact: version
    ),
    packageName: "swift-markdown"
  )
}()

let swiftSettings: [SwiftSetting] = [.unsafeFlags(["-warnings-as-errors"])]

let package = Package(
  name: "DivKit",
  platforms: [
    .iOS(.v13),
    .macOS(.v10_15),
  ],
  products: [
    .library(name: "DivKit", targets: ["DivKit"]),
    .library(name: "DivKitExtensions", targets: ["DivKitExtensions"]),
    .library(name: "DivKitMarkdownExtension", targets: ["DivKitMarkdownExtension"]),
    .library(name: "DivKitSVG", targets: ["DivKitSVG"]),
    .library(name: "LayoutKit", targets: ["LayoutKit"]),
    .library(name: "LayoutKitInterface", targets: ["LayoutKitInterface"]),
    .library(name: "Serialization", targets: ["Serialization"]),
  ],
  dependencies: [
    vgsl.package,
    markdown.package,
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
        .product(name: "Markdown", package: markdown.packageName),
      ],
      path: "DivKitMarkdownExtension",
      swiftSettings: swiftSettings
    ),
    .target(
      name: "DivKitSVG",
      dependencies: [
        "DivKit",
        .product(name: "VGSL", package: vgsl.packageName),
      ],
      path: "DivKitSVG",
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
