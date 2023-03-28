import XCTest

@testable import DivKit

import CommonCorePublic

final class ExpressionResolverTests: XCTestCase {
  private let expressionResolver = ExpressionResolver(
    variables: [
      "string_var": .string("string value"),
      "color_var": .color(Color.color(withHexString: "#AABBCC")!),
      "enum_var": .string("first"),
      "url_var": .url(URL(string: "https://some.url")!),
    ]
  )

  func test_ResolveString_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression: "Some string"),
      "Some string"
    )
  }

  func test_ResolveString_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression: "@{string_var}"),
      "string value"
    )
  }

  func test_ResolveColor_WithInvalidValue() {
    XCTAssertNil(expressionResolver.resolveColor(expression: "not color"))
  }

  func test_ResolveColor_WithConstant() {
    XCTAssertEqual(
      expressionResolver.resolveColor(expression: "#CCBBAA"),
      Color.color(withHexString: "#CCBBAA")!
    )
  }

  func test_ResolveColor_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveColor(expression: "@{color_var}"),
      Color.color(withHexString: "#AABBCC")!
    )
  }

  func test_ResolveEnum_WithInvalidValue() {
    let value: TestEnum? = expressionResolver.resolveEnum(expression: "invalid")
    XCTAssertNil(value)
  }

  func test_ResolveEnum_WithConstant() {
    XCTAssertEqual(
      expressionResolver.resolveEnum(expression: "second"),
      TestEnum.second
    )
  }

  func test_ResolveEnum_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveEnum(expression: "@{enum_var}"),
      TestEnum.first
    )
  }

  func test_ResolveUrl_WithInvalidValue() {
    XCTAssertNil(expressionResolver.resolveUrl(expression: "not URL"))
  }

  func test_ResolveUrl_WithConstant() {
    XCTAssertEqual(
      expressionResolver.resolveUrl(expression: "https://constant.url"),
      URL(string: "https://constant.url")!
    )
  }

  func test_ResolveUrl_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveUrl(expression: "@{url_var}"),
      URL(string: "https://some.url")!
    )
  }
}

private enum TestEnum: String {
  case first
  case second
}
