import XCTest

@testable import DivKitExtensions
import DivKit
import BasePublic

final class ShimmerStyleTests: XCTestCase {
  private let expressionResolver = ExpressionResolver(
    variables: [:],
    persistentValuesStorage: DivPersistentValuesStorage(),
    errorTracker: nil
  )

  func test_WhenDecodingEmptyShimmerStyle_DecodesWithDefaultValues() throws {
    XCTAssertEqual(
      ShimmerStyle.default,
      try ShimmerStyle(dictionary: emptyShimmerStyle, expressionResolver: expressionResolver)
    )
  }

  func test_WhenDecodingShimmerStyleWithExactValues_DecodesCorrectly() throws {
    XCTAssertEqual(
      expectedShimmerStyle,
      try ShimmerStyle(dictionary: exactValuesShimmerStyle, expressionResolver: expressionResolver)
    )
  }

  func test_WhenDecodingShimmerStyleWithExpressionValues_DecodesCorrectly() throws {
    XCTAssertEqual(
      expectedShimmerStyle,
      try ShimmerStyle(dictionary: expressionValuesShimmerStyle, expressionResolver: expressionResolver)
    )
  }
}

private let emptyShimmerStyle = [String: Any]()
private let exactValuesShimmerStyle: [String: Any] = [
  "angle": 15.0,
  "duration": 1.6,
  "colors": ["#ffffff", "#ffffff00", "#ffffff"],
  "locations": [0.1, 0.5, 0.9]
]

private let expressionValuesShimmerStyle: [String: Any] = [
  "angle": "@{15}",
  "colors": [
    "@{'#ffffff'}",
    "@{'#ffffff00'}",
    "@{'#ffffff'}"
  ],
  "duration": "@{1.6}",
  "locations": ["@{0.1}", "@{0.5}", "@{0.9}"]
]


private let expectedShimmerStyle = ShimmerStyle(
  colorsAndLocations: [
    (Color.color(withHexString: "#ffffff")!, 0.1),
    (Color.color(withHexString: "#ffffff00")!, 0.5),
    (Color.color(withHexString: "#ffffff")!, 0.9)
  ],
  angle: 15,
  duration: 1.6
)

extension ShimmerStyle: Equatable {
  public static func == (lhs: DivKitExtensions.ShimmerStyle, rhs: DivKitExtensions.ShimmerStyle) -> Bool {
    lhs.angle == rhs.angle && lhs.duration == rhs.duration && lhs.colorsAndLocations == rhs.colorsAndLocations
  }
}
