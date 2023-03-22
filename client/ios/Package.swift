// swift-tools-version:5.5

import PackageDescription

let package = Package(
  name: "DivKit",
  platforms: [
    .iOS(.v11),
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
      name: "Base",
      dependencies: [
        "BaseTiny",
        "BaseUI",
      ],
      path: "Core/Base",
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
      name: "BaseTiny",
      path: "Core/BaseTiny",
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
      name: "BaseUI",
      dependencies: [
        "BaseTiny",
      ],
      path: "Core/BaseUI",
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
      name: "CommonCore",
      dependencies: [
        "Base",
      ],
      path: "Core/CommonCore",
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
      name: "Networking",
      dependencies: [
        "Base",
      ],
      path: "Core/Networking",
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
        "CommonCore",
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
        "Base",
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
        "CommonCore",
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

    .target(
      name: "TemplatesSupport",
      dependencies: [
        "CommonCore",
        "Serialization",
      ],
      path: "TemplatesSupport",
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
