import XCTest

@testable import DivKit

final class ExpressionResolverAnyTests: XCTestCase {
  private var isErrorExpected = false
  private var error: String? = nil

  private var variables: DivVariables = [:]

  private lazy var expressionResolver = ExpressionResolver(
    variables: variables,
    persistentValuesStorage: DivPersistentValuesStorage(),
    errorTracker: { [unowned self] in
      error = $0.message
      if !self.isErrorExpected {
        XCTFail($0.message)
      }
    }
  )

  func test_resolve_String() {
    XCTAssertNil(expressionResolver.resolve("Some string"))
  }

  func test_resolve_BooleanConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{true}") as? AnyHashable,
      true
    )
  }

  func test_resolve_IntegerConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{123}") as? AnyHashable,
      123
    )
  }

  func test_resolve_NumberConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{123.45}") as? AnyHashable,
      123.45
    )
  }

  func test_resolve_StringConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{'string value'}") as? AnyHashable,
      "string value"
    )
  }

  func test_resolve_BooleanVar() {
    variables["var"] = .bool(true)

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      true
    )
  }

  func test_resolve_IntegerVar() {
    variables["var"] = .integer(123)

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      123
    )
  }

  func test_resolve_NumberVar() {
    variables["var"] = .number(123.45)

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      123.45
    )
  }

  func test_resolve_StringVar() {
    variables["var"] = .string("string value")

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      "string value"
    )
  }

  func test_resolve_ArrayVar() {
    variables["var"] = .array(["value", 123, true])

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      ["value", 123, true] as [AnyHashable]
    )
  }

  func test_resolve_DictVar() {
    variables["var"] = .dict(["boolean": true, "integer": 123, "string": "value"])

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      ["boolean": true, "integer": 123, "string": "value"] as [String: AnyHashable]
    )
  }

  func test_resolve_ColorVar() {
    variables["var"] = .color(color("#AABBCC"))

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      color("#FFAABBCC")
    )
  }

  func test_resolve_ColorExpression() {
    XCTAssertEqual(
      expressionResolver.resolve("@{argb(1.0, 0.5, 0.5, 0.5)}") as? AnyHashable,
      color("#FF808080")
    )
  }

  func test_resolve_UrlVar() {
    variables["var"] = .url(url("https://some.url"))

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? AnyHashable,
      url("https://some.url")
    )
  }

  func test_resolve_StringInterpolation() {
    variables["var"] = .integer(2)

    XCTAssertEqual(
      expressionResolver.resolve("@{var} + @{var} = @{var + var}") as? AnyHashable,
      "2 + 2 = 4"
    )
  }

  func test_resolve_String_WithNestedExpression() {
    variables["var"] = .string("string value")

    XCTAssertEqual(
      expressionResolver.resolve("@{'Value: @{var}'}") as? AnyHashable,
      "Value: string value"
    )
  }

  func test_resolve_Boolean_WithNestedExpression() {
    variables["var"] = .string("value")

    XCTAssertEqual(
      expressionResolver.resolve("@{var == '@{var}'}") as? AnyHashable,
      true
    )
  }

  func test_resolve_InvalidExpression() {
    isErrorExpected = true

    XCTAssertNil(expressionResolver.resolve("@{unknown_var}"))
    XCTAssertEqual(error, "Variable 'unknown_var' is missing.")
  }
}
