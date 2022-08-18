// swift-tools-version:5.5

import PackageDescription

let package = Package(
  name: "DivKit",
  platforms: [
    .iOS(.v10),
  ],
  products: [
    .library(name: "BaseUI", targets: ["BaseUI"]),
    .library(name: "DivKit", targets: ["DivKit"]),
    .library(name: "DivKitExtensions", targets: ["DivKitExtensions"]),
    .library(name: "LayoutKit", targets: ["LayoutKit"]),
    .library(name: "LayoutKitInterface", targets: ["LayoutKitInterface"]),
    .library(name: "TemplatesSupport", targets: ["TemplatesSupport"]),
  ],
  targets: [
    .target(
      name: "BaseUI",
      dependencies: [
        "BaseTiny",
      ],
      path: "Core/BaseUI"
    ),

    .target(
      name: "Base",
      dependencies: [
        "BaseTiny",
        "BaseUI",
      ],
      path: "Core/Base"
    ),

    .target(
      name: "BaseTiny",
      path: "Core/BaseTiny"
    ),

    .target(
      name: "CommonCore",
      dependencies: [
        "Base",
        "BaseUI",
        "CommonCoreTiny",
      ],
      path: "Core/CommonCore"
    ),

    .target(
      name: "CommonCoreTiny",
      dependencies: [
        "BaseTiny",
      ],
      path: "Core/CommonCoreTiny"
    ),

    .target(
      name: "Networking",
      dependencies: [
        "Base",
      ],
      path: "Core/Networking"
    ),

    .target(
      name: "DivKit",
      dependencies: [
        "CommonCore",
        "LayoutKit",
        "Networking",
        "Serialization",
        "TemplatesSupport",
      ],
      path: "DivKit",
      exclude: [
        "generator_config.json",
      ],
      swiftSettings: [
        .unsafeFlags(["-warnings-as-errors"])
      ]
    ),

    .target(
      name: "DivKitExtensions",
      dependencies: [
        "DivKit",
      ],
      path: "DivKitExtensions",
      swiftSettings: [
        .unsafeFlags(["-warnings-as-errors"])
      ]
    ),

    .target(
      name: "LayoutKit",
      dependencies: [
        "CommonCore",
        "LayoutKitInterface",
      ],
      path: "LayoutKit/LayoutKit",
      swiftSettings: [
        .unsafeFlags(["-warnings-as-errors"])
      ]
    ),

    .target(
      name: "LayoutKitInterface",
      dependencies: [
        "CommonCore",
      ],
      path: "LayoutKit/Interface",
      swiftSettings: [
        .unsafeFlags(["-warnings-as-errors"])
      ]
    ),

    .target(
      name: "Serialization",
      dependencies: [
        "CommonCore",
      ],
      path: "Serialization",
      swiftSettings: [
        .unsafeFlags(["-warnings-as-errors"])
      ]
    ),

    .target(
      name: "TemplatesSupport",
      dependencies: [
        "CommonCore",
        "Serialization",
      ],
      path: "TemplatesSupport",
      swiftSettings: [
        .unsafeFlags(["-warnings-as-errors"])
      ]
    ),
  ]
)
