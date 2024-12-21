@testable import DivKit
import VGSL
import XCTest

final class ExpressionResolverAnyTests: XCTestCase {
  private var isErrorExpected = false
  private var error: String? = nil

  private var variables: DivVariables = [:]

  private lazy var expressionResolver = ExpressionResolver(
    functionsProvider: FunctionsProvider(
      persistentValuesStorage: DivPersistentValuesStorage()
    ),
    variableValueProvider: { [unowned self] in
      self.variables[DivVariableName(rawValue: $0)]?.typedValue()
    },
    errorTracker: { [unowned self] in
      error = $0.description
      if !self.isErrorExpected {
        XCTFail($0.description)
      }
    }
  )

  func test_resolve_String() {
    XCTAssertNil(expressionResolver.resolve("Some string"))
  }

  func test_resolve_BooleanConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{true}") as? Bool,
      true
    )
  }

  func test_resolve_IntegerConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{123}") as? Int,
      123
    )
  }

  func test_resolve_NumberConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{123.45}") as? Double,
      123.45
    )
  }

  func test_resolve_StringConst() {
    XCTAssertEqual(
      expressionResolver.resolve("@{'string value'}") as? String,
      "string value"
    )
  }

  func test_resolve_BooleanVar() {
    variables["var"] = .bool(true)

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? Bool,
      true
    )
  }

  func test_resolve_IntegerVar() {
    variables["var"] = .integer(123)

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? Int,
      123
    )
  }

  func test_resolve_NumberVar() {
    variables["var"] = .number(123.45)

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? Double,
      123.45
    )
  }

  func test_resolve_StringVar() {
    variables["var"] = .string("string value")

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? String,
      "string value"
    )
  }

  func test_resolve_ArrayVar() {
    variables["var"] = .array(["value", 123, true])

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? DivArray,
      ["value", 123, true]
    )
  }

  func test_resolve_DictVar() {
    variables["var"] = .dict(["boolean": true, "integer": 123, "string": "value"])

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? DivDictionary,
      ["boolean": true, "integer": 123, "string": "value"]
    )
  }

  func test_resolve_ColorVar() {
    variables["var"] = .color(color("#AABBCC"))

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? Color,
      color("#FFAABBCC")
    )
  }

  func test_resolve_ColorExpression() {
    XCTAssertEqual(
      expressionResolver.resolve("@{argb(1.0, 0.5, 0.5, 0.5)}") as? Color,
      color("#FF808080")
    )
  }

  func test_resolve_UrlVar() {
    variables["var"] = .url(url("https://some.url"))

    XCTAssertEqual(
      expressionResolver.resolve("@{var}") as? URL,
      url("https://some.url")
    )
  }

  func test_resolve_StringInterpolation() {
    variables["var"] = .integer(2)

    XCTAssertEqual(
      expressionResolver.resolve("@{var} + @{var} = @{var + var}") as? String,
      "2 + 2 = 4"
    )
  }

  func test_resolve_String_WithNestedExpression() {
    variables["var"] = .string("string value")

    XCTAssertEqual(
      expressionResolver.resolve("@{'Value: @{var}'}") as? String,
      "Value: string value"
    )
  }

  func test_resolve_Boolean_WithNestedExpression() {
    variables["var"] = .string("value")

    XCTAssertEqual(
      expressionResolver.resolve("@{var == '@{var}'}") as? Bool,
      true
    )
  }

  func test_resolve_InvalidExpression() {
    isErrorExpected = true

    XCTAssertNil(expressionResolver.resolve("@{unknown_var}"))
    XCTAssertEqual(error, "Variable 'unknown_var' is missing. Expression: @{unknown_var}")
  }
}
