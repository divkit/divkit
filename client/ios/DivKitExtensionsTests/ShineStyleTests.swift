import XCTest

import DivKit
@testable import DivKitExtensions
import VGSL

final class ShineStyleTests: XCTestCase {
  let tester = ShimmerStyleTester { dict, expressionResolver in
    try ShineStyle(
      dictionary: dict,
      expressionResolver: expressionResolver
    )
  }

  func test_WhenDecodingEmptyShineStyle_DecodesWithDefaultValues() throws {
    try tester.assertStyle(ShineStyle.default, .init())
  }

  func test_WhenDecodingShineStyleWithExactValues_DecodesCorrectly() throws {
    try tester.assertStyle(expectedShineStyle, exactValuesShineStyle)
  }

  func test_WhenDecodingShineStyleWithExpressionValues_DecodesCorrectly() throws {
    try tester.assertStyle(expectedShineStyle, expressionValuesShineStyle)
  }
}

private let expectedShineStyle = ShineStyle(
  isEnabled: true,
  repetitions: 1,
  interval: 2,
  beginAfter: 3,
  duration: 4,
  angle: 5,
  colorsAndLocations: [
    ColorAndLocation(color: .color(withHexString: "#ffffff00")!, location: 0.1),
    ColorAndLocation(color: .color(withHexString: "#ffffff")!, location: 0.5),
    ColorAndLocation(color: .color(withHexString: "#ffffff00")!, location: 0.9),
  ]
)
private let exactValuesShineStyle: [String: Any] = [
  "enabled": true,
  "cycle_count": 1,
  "interval": 2000,
  "delay": 3000,
  "duration": 4000,
  "angle": 5,
  "colors": ["#ffffff00", "#ffffff", "#ffffff00"],
  "locations": [0.1, 0.5, 0.9],
]
private let expressionValuesShineStyle: [String: Any] = [
  "enabled": "@{true}",
  "cycle_count": "@{1}",
  "interval": "@{2000}",
  "delay": "@{3000}",
  "duration": "@{4000}",
  "angle": "@{5}",
  "colors": [
    "@{'#ffffff00'}",
    "@{'#ffffff'}",
    "@{'#ffffff00'}",
  ],
  "locations": ["@{0.1}", "@{0.5}", "@{0.9}"],
]
