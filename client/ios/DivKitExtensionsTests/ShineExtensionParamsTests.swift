import XCTest

@testable import DivKitExtensions

import DivKitTestsSupport
import LayoutKit
import DivKit
import VGSL

final class ShineExtensionParamsTests: XCTestCase {
  let tester = ShimmerStyleTester { dict, context in
    try ShineExtensionParams(dictionary: dict, context: context)
  }

  func test_WhenDecodingEmptyShineExtensionParams_DecodesWithDefaultValues() throws {
    try tester.assertStyle(ShineExtensionParams.default, .init())
  }

  func test_WhenDecodingShineExtensionParamsWithExactValues_DecodesCorrectly() throws {
    try tester.assertStyle(expectedShineExtensionParams, exactValuesShineExtensionParams)
  }

  func test_WhenDecodingShineExtensionParamsWithExpressionValues_DecodesCorrectly() throws {
    try tester.assertStyle(expectedShineExtensionParams, expressionValuesShineExtensionParams)
  }
}

private let expectedShineExtensionParams = ShineExtensionParams(
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
  ],
  onCycleStartActions: [
    UserInterfaceAction(
      payload: .divAction(
        params: UserInterfaceAction.DivActionParams(
          action: [
            "is_enabled": .bool(true),
            "log_id": .string("some_log_id"),
            "url": .string("div-action://test-action")
          ],
          path: .parse(DivBlockModelingContext.testCardId.rawValue),
          source: .tap,
          url: URL(string: "div-action://test-action")!
        )
      ),
      path: .parse("\(DivBlockModelingContext.testCardId)/some_log_id")
    )
  ]
)
private let exactValuesShineExtensionParams: [String: Any] = [
  "is_enabled": true,
  "cycle_count": 1,
  "interval": 2,
  "delay": 3,
  "duration": 4,
  "angle": 5,
  "colors": ["#ffffff00", "#ffffff", "#ffffff00"],
  "locations": [0.1, 0.5, 0.9],
  "on_cycle_start_actions": [
    [
      "log_id": "some_log_id",
      "url": "div-action://test-action"
    ]
  ],
]
private let expressionValuesShineExtensionParams: [String: Any] = [
  "is_enabled": "@{true}",
  "cycle_count": "@{1}",
  "interval": "@{2}",
  "delay": "@{3}",
  "duration": "@{4}",
  "angle": "@{5}",
  "colors": [
    "@{'#ffffff00'}",
    "@{'#ffffff'}",
    "@{'#ffffff00'}",
  ],
  "locations": ["@{0.1}", "@{0.5}", "@{0.9}"],
  "on_cycle_start_actions": [
    [
      "log_id": "some_log_id",
      "url": "div-action://test-action"
    ]
  ],
]
