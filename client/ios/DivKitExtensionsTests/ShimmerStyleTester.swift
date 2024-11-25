import XCTest

import DivKit
@testable import DivKitExtensions
import VGSL

final class ShimmerStyleTester<Style: Equatable> {
  private let styleFactory: ([String: Any], ExpressionResolver) throws -> Style
  private let expressionResolver = ExpressionResolver(
    variables: [:],
    persistentValuesStorage: DivPersistentValuesStorage(),
    errorTracker: { XCTFail($0.description) }
  )

  init(styleFactory: @escaping ([String: Any], ExpressionResolver) throws -> Style) {
    self.styleFactory = styleFactory
  }

  func assertStyle(
    _ style: Style,
    _ dictionary: [String: Any]
  ) throws {
    XCTAssertEqual(style, try styleFactory(dictionary, expressionResolver))
  }
}
