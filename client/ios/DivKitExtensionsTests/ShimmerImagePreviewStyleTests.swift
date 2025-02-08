import DivKit
@testable import DivKitExtensions
import LayoutKit
import VGSL
import XCTest

final class ShimmerImagePreviewStyleTests: XCTestCase {
  let tester = ShimmerStyleTester { dict, context in
    try ShimmerImagePreviewStyle(
      dictionary: dict,
      expressionResolver: context.expressionResolver
    )
  }

  func test_WhenDecodingEmptyImagePreviewStyle_DecodesWithDefaultValues() throws {
    try tester.assertStyle(ShimmerImagePreviewStyle.default, .init())
  }

  func test_WhenDecodingImagePreviewStyleWithExactValues_DecodesCorrectly() throws {
    try tester.assertStyle(imagePreviewStyle, exactValuesShimmerImagePreviewStyle)
  }

  func test_WhenDecodingImagePreviewStyleWithExpressionValues_DecodesCorrectly() throws {
    try tester.assertStyle(imagePreviewStyle, expressionValuesShimmerImagePreviewStyle)
  }
}

private let imagePreviewStyle = ShimmerImagePreviewStyle(
  colorsAndLocations: [
    ColorAndLocation(color: .color(withHexString: "#ffffff")!, location: 0.1),
    ColorAndLocation(color: .color(withHexString: "#ffffff00")!, location: 0.5),
    ColorAndLocation(color: .color(withHexString: "#ffffff")!, location: 0.9),
  ],
  angle: 15,
  duration: 1.6,
  cornerRadius: CornerRadii(topLeft: 0, topRight: 8, bottomLeft: 16, bottomRight: 32)
)
private let exactValuesShimmerImagePreviewStyle: [String: Any] = [
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
private let expressionValuesShimmerImagePreviewStyle: [String: Any] = [
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
