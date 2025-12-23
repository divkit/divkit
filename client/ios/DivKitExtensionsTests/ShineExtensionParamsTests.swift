import DivKit
@testable import DivKitExtensions
import Foundation
import LayoutKit
import Testing
import VGSL

@MainActor
@Suite
struct ShineExtensionParamsTests {
  private let tester = ShimmerStyleTester { dict, context in
    try ShineExtensionParams(dictionary: dict, context: context)
  }

  @Test
  func whenDecodingEmptyShineExtensionParams_DecodesWithDefaultValues() {
    tester.assertStyle(ShineExtensionParams.default, .init())
  }

  @Test
  func whenDecodingShineExtensionParamsWithExactValues_DecodesCorrectly() {
    tester.assertStyle(
      expectedParams,
      [
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
            "url": "div-action://test-action",
          ],
        ],
      ]
    )
  }

  @Test
  func whenDecodingShineExtensionParamsWithExpressionValues_DecodesCorrectly() {
    tester.assertStyle(
      expectedParams,
      [
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
            "url": "div-action://test-action",
          ],
        ],
      ]
    )
  }
}

private let expectedParams = ShineExtensionParams(
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
            "url": .string("div-action://test-action"),
          ],
          path: .parse(DivBlockModelingContext.testCardId.rawValue),
          source: .tap,
          url: URL(string: "div-action://test-action")!
        )
      ),
      path: .parse("\(DivBlockModelingContext.testCardId)/some_log_id")
    ),
  ]
)
