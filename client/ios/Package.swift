// swift-tools-version:5.7

import PackageDescription

let package = Package(
  name: "DivKit",
  platforms: [
    .iOS(.v11),
  ],
  products: [
    .library(name: "BaseUIPublic", targets: ["BaseUIPublic"]),
    .library(name: "DivKit", targets: ["DivKit"]),
    .library(name: "DivKitExtensions", targets: ["DivKitExtensions"]),
    .library(name: "LayoutKit", targets: ["LayoutKit"]),
    .library(name: "LayoutKitInterface", targets: ["LayoutKitInterface"]),
  ],
  targets: [
    .target(
      name: "BasePublic",
      dependencies: [
        "BaseTinyPublic",
        "BaseUIPublic",
      ],
      path: "Core/BasePublic",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
          ]
        )
      ]
    ),

    .target(
      name: "BaseTinyPublic",
      path: "Core/BaseTinyPublic",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
          ]
        )
      ]
    ),

    .target(
      name: "BaseUIPublic",
      dependencies: [
        "BaseTinyPublic",
      ],
      path: "Core/BaseUIPublic",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
          ]
        )
      ]
    ),

    .target(
      name: "CommonCorePublic",
      dependencies: [
        "BasePublic",
      ],
      path: "Core/CommonCorePublic",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
          ]
        )
      ]
    ),

    .target(
      name: "NetworkingPublic",
      dependencies: [
        "BasePublic",
      ],
      path: "Core/NetworkingPublic",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
          ]
        )
      ]
    ),

    .target(
      name: "DivKit",
      dependencies: [
        "CommonCorePublic",
        "LayoutKit",
        "NetworkingPublic",
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
            "-emit-module-interface",
            "-enable-library-evolution",
            "-warnings-as-errors",
          ]
        )
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
            "-emit-module-interface",
            "-enable-library-evolution",
            "-warnings-as-errors",
          ]
        )
      ]
    ),

    .target(
      name: "LayoutKit",
      dependencies: [
        "CommonCorePublic",
        "LayoutKitInterface",
      ],
      path: "LayoutKit/LayoutKit",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
            "-warnings-as-errors",
          ]
        )
      ]
    ),

    .target(
      name: "LayoutKitInterface",
      dependencies: [
        "BasePublic",
      ],
      path: "LayoutKit/Interface",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
            "-warnings-as-errors",
          ]
        )
      ]
    ),

    .target(
      name: "Serialization",
      dependencies: [
        "CommonCorePublic",
      ],
      path: "Serialization",
      swiftSettings: [
        .unsafeFlags(
          [
            "-emit-module-interface",
            "-enable-library-evolution",
            "-warnings-as-errors",
          ]
        )
      ]
    ),
  ]
)
