import DivKit
@testable import DivKitExtensions
import LayoutKit
import Testing
import VGSL

@MainActor
@Suite
struct ShimmerImagePreviewStyleTests {
  private let tester = ShimmerStyleTester { dict, context in
    try ShimmerImagePreviewStyle(
      dictionary: dict,
      expressionResolver: context.expressionResolver
    )
  }

  @Test
  func whenDecodingEmptyImagePreviewStyle_DecodesWithDefaultValues() {
    tester.assertStyle(ShimmerImagePreviewStyle.default, .init())
  }

  @Test
  func whenDecodingImagePreviewStyleWithExactValues_DecodesCorrectly() {
    tester.assertStyle(
      expectedStyle,
      [
        "angle": 15.0,
        "duration": 1.6,
        "colors": ["#ffffff", "#ffffff00", "#ffffff"],
        "locations": [0.1, 0.5, 0.9],
        "corner_radius": [
          "top-left": 0,
          "top-right": 8,
          "bottom-left": 16,
          "bottom-right": 32,
        ],
      ]
    )
  }

  @Test
  func whenDecodingImagePreviewStyleWithExpressionValues_DecodesCorrectly() {
    tester.assertStyle(
      expectedStyle,
      [
        "angle": "@{15}",
        "colors": [
          "@{'#ffffff'}",
          "@{'#ffffff00'}",
          "@{'#ffffff'}",
        ],
        "duration": "@{1.6}",
        "locations": ["@{0.1}", "@{0.5}", "@{0.9}"],
        "corner_radius": [
          "top-left": "@{0}",
          "top-right": "@{8}",
          "bottom-left": "@{16}",
          "bottom-right": "@{32}",
        ],
      ]
    )
  }
}

private let expectedStyle = ShimmerImagePreviewStyle(
  colorsAndLocations: [
    ColorAndLocation(color: .color(withHexString: "#ffffff")!, location: 0.1),
    ColorAndLocation(color: .color(withHexString: "#ffffff00")!, location: 0.5),
    ColorAndLocation(color: .color(withHexString: "#ffffff")!, location: 0.9),
  ],
  angle: 15,
  duration: 1.6,
  cornerRadius: CornerRadii(topLeft: 0, topRight: 8, bottomLeft: 16, bottomRight: 32)
)
