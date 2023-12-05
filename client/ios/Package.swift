// swift-tools-version:5.7

import Foundation
import PackageDescription

let vgsl = {
  let url = ProcessInfo.processInfo.environment["VGSL_SPM_REPO"] ?? "https://github.com/yandex/vgsl.git"
  let packageName = URL(string: url)!.deletingPathExtension().lastPathComponent
  return (
    url: url,
    packageName: packageName,
    version: Version("2.3.3")
  )
}()

let package = Package(
  name: "DivKit",
  platforms: [
    .iOS(.v11),
  ],
  products: [
    .library(name: "DivKit", targets: ["DivKit"]),
    .library(name: "DivKitExtensions", targets: ["DivKitExtensions"]),
    .library(name: "LayoutKit", targets: ["LayoutKit"]),
    .library(name: "LayoutKitInterface", targets: ["LayoutKitInterface"]),
    .library(name: "Serialization", targets: ["Serialization"]),
  ],
  dependencies: [
    .package(url: vgsl.url, from: vgsl.version),
  ],
  targets: [
    .target(
      name: "DivKit",
      dependencies: [
        "LayoutKit",
        .product(name: "CommonCorePublic", package: vgsl.packageName),
        .product(name: "NetworkingPublic", package: vgsl.packageName),
        "Serialization",
      ],
      path: "DivKit",
      exclude: [
        "generator_config.json",
        "shared_data_generator_config.json",
      ],
      swiftSettings: [
        .unsafeFlags(
          [
            "-warnings-as-errors",
          ]
        ),
      ]
    ),
    .target(
      name: "DivKitExtensions",
      dependencies: [
        "DivKit",
      ],
      path: "DivKitExtensions",
      swiftSettings: [
        .unsafeFlags(
          [
            "-warnings-as-errors",
          ]
        ),
      ]
    ),
    .target(
      name: "LayoutKit",
      dependencies: [
        .product(name: "CommonCorePublic", package: vgsl.packageName),
        "LayoutKitInterface",
      ],
      path: "LayoutKit/LayoutKit",
      swiftSettings: [
        .unsafeFlags(
          [
            "-warnings-as-errors",
          ]
        ),
      ]
    ),
    .target(
      name: "LayoutKitInterface",
      dependencies: [
        .product(name: "BasePublic", package: vgsl.packageName),
      ],
      path: "LayoutKit/Interface",
      swiftSettings: [
        .unsafeFlags(
          [
            "-warnings-as-errors",
          ]
        ),
      ]
    ),
    .target(
      name: "Serialization",
      dependencies: [
        .product(name: "CommonCorePublic", package: vgsl.packageName),
      ],
      path: "Serialization",
      swiftSettings: [
        .unsafeFlags(
          [
            "-warnings-as-errors",
          ]
        ),
      ]
    ),
  ]
)
