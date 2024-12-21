import DivKit
import DivKitTestsSupport
import XCTest

final class ShimmerStyleTester<Style: Equatable> {
  private let styleFactory: ([String: Any], DivBlockModelingContext) throws -> Style

  init(styleFactory: @escaping ([String: Any], DivBlockModelingContext) throws -> Style) {
    self.styleFactory = styleFactory
  }

  func assertStyle(
    _ style: Style,
    _ dictionary: [String: Any]
  ) throws {
    XCTAssertEqual(
      style,
      try styleFactory(dictionary, .default)
    )
  }
}
