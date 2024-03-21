import XCTest

@testable import DivKit

import CommonCorePublic

final class ExpressionResolverTests: XCTestCase {
  private var isErrorExpected = false

  private lazy var expressionResolver = ExpressionResolver(
    variables: [
      "array_var": .array([1]),
      "boolean_var": .bool(true),
      "color_var": .color(color("#AABBCC")),
      "enum_var": .string("first"),
      "integer_var": .integer(123),
      "number_var": .number(12.9),
      "string_var": .string("string value"),
      "url_var": .url(url("https://some.url")),
    ],
    persistentValuesStorage: DivPersistentValuesStorage(),
    errorTracker: { [unowned self] in
      if !self.isErrorExpected {
        XCTFail($0.message)
      }
    },
    variableTracker: { [unowned self] in
      self.usedVariables = self.usedVariables.union($0)
    }
  )

  private var usedVariables: Set<DivVariableName> = []

  func test_ResolveString_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveString(.value("Some string")),
      "Some string"
    )
  }

  func test_ResolveString_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{string_var}")),
      "string value"
    )
  }

  func test_ResolveString_WithNestedExpression() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{'Value: @{string_var}'}")),
      "Value: string value"
    )
  }

  func test_ResolveColor_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveColor(.value(color("#CCBBAA"))),
      color("#CCBBAA")
    )
  }

  func test_ResolveColor_Expression() {
    XCTAssertEqual(
      expressionResolver.resolveColor(expression("@{'#CCBBAA'}")),
      color("#CCBBAA")
    )
  }

  func test_ResolveColor_Invalid() {
    isErrorExpected = true

    XCTAssertNil(
      expressionResolver.resolveColor(expression("@{'invalid'}"))
    )
  }

  func test_ResolveUrl_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveUrl(.value(url("https://some.url"))),
      url("https://some.url")
    )
  }

  func test_ResolveUrl_Expression() {
    XCTAssertEqual(
      expressionResolver.resolveUrl(expression("@{'https://some.url'}")),
      url("https://some.url")
    )
  }

  func test_ResolveNumeric_IntegerConstant() {
    let value: Int? = expressionResolver.resolveNumeric(expression("@{123}"))
    XCTAssertEqual(value, 123)
  }

  func test_ResolveNumeric_IntegerExpression() {
    let value: Int? = expressionResolver.resolveNumeric(expression("@{integer_var}"))
    XCTAssertEqual(value, 123)
  }

  func test_ResolveNumeric_NumberConstant() {
    let value: Double? = expressionResolver.resolveNumeric(expression("@{12.9}"))
    XCTAssertEqual(value, 12.9)
  }

  func test_ResolveNumeric_NumberExpression() {
    let value: Double? = expressionResolver.resolveNumeric(expression("@{number_var}"))
    XCTAssertEqual(value, 12.9)
  }

  func test_ResolveNumeric_BooleanConstant() {
    let value: Bool = expressionResolver.resolveNumeric(expression("@{true}"))!
    XCTAssertTrue(value)
  }

  func test_ResolveNumeric_BooleanExpression() {
    let value: Bool = expressionResolver.resolveNumeric(expression("@{boolean_var}"))!
    XCTAssertTrue(value)
  }

  func test_ResolveNumeric_NestedBooleanExpression() {
    let value: Bool = expressionResolver.resolveNumeric(expression("@{'@{string_var}' == string_var}"))!
    XCTAssertTrue(value)
  }

  func test_ResolveNumeric_IntegerAsNumber() {
    let value: Double? = expressionResolver.resolveNumeric(expression("@{integer_var}"))
    XCTAssertEqual(value, 123.0)
  }

  func test_ResolveNumeric_NumberAsInteger() {
    let value: Int? = expressionResolver.resolveNumeric(expression("@{number_var}"))
    XCTAssertEqual(value, 12)
  }

  func test_ResolveEnum_WithInvalidValue() {
    isErrorExpected = true

    let value: TestEnum? = expressionResolver.resolveEnum(expression("@{'invalid'}"))
    XCTAssertNil(value)
  }

  func test_ResolveEnum_WithConstant() {
    XCTAssertEqual(
      expressionResolver.resolveEnum(.value(TestEnum.second)),
      TestEnum.second
    )
  }

  func test_ResolveEnum_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveEnum(expression("@{enum_var}")),
      TestEnum.first
    )
  }

  func test_ResolveArray_WithVariable() throws {
    XCTAssertEqual(
      expressionResolver.resolveArray(expression("@{array_var}")) as! [AnyHashable],
      [1]
    )
  }

  func test_TracksVariable_InSimpleExpression() {
    let _ = expressionResolver.resolveString(expression("@{string_var}"))

    XCTAssertEqual(usedVariables, ["string_var"])
  }

  func test_TracksVariable_InNestedExpression() {
    let _ = expressionResolver.resolveString(expression("@{'@{string_var}'}"))

    XCTAssertEqual(usedVariables, ["string_var"])
  }

  func test_TracksUnknownVariable() {
    isErrorExpected = true

    let _ = expressionResolver.resolveString(expression("@{unknown_var}"))

    XCTAssertEqual(usedVariables, ["unknown_var"])
  }

  func test_TracksVariable_InGetValueFunction() {
    let _ = expressionResolver.resolveString(
      expression("@{getStringValue('string_var', 'fallback value')}")
    )

    XCTAssertEqual(usedVariables, ["string_var"])
  }

  func test_getStringValue_ReturnsVariableValue() {
    let value = expressionResolver.resolveString(
      expression("@{getStringValue('string_var', 'fallback value')}")
    )

    XCTAssertEqual(value, "string value")
  }

  func test_getStringValue_ReturnsFallbackValue() {
    let value = expressionResolver.resolveString(
      expression("@{getStringValue('unknown_var', 'fallback value')}")
    )

    XCTAssertEqual(value, "fallback value")
  }
}

private enum TestEnum: String {
  case first
  case second
}
