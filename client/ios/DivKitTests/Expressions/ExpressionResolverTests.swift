import XCTest

@testable import DivKit

import Serialization
import VGSL

final class ExpressionResolverTests: XCTestCase {
  private var isErrorExpected = false
  private var error: String?

  private var variables: DivVariables = [
    "array_var": .array(["value", [true, 123, 123.45] as [AnyHashable]]),
    "boolean_var": .bool(true),
    "color_var": .color(color("#AABBCC")),
    "dict_var": .dict(["boolean": true, "integer": 1, "number": 1.0, "string": "value"]),
    "enum_var": .string("first"),
    "integer_var": .integer(123),
    "number_var": .number(12.9),
    "string_var": .string("string value"),
    "url_var": .url(url("https://some.url")),
  ]

  private lazy var expressionResolver = ExpressionResolver(
    variableValueProvider: { [unowned self] in
      let varibleName = DivVariableName(rawValue: $0)
      self.usedVariables.insert(varibleName)
      return self.variables[varibleName]?.typedValue()
    },
    persistentValuesStorage: DivPersistentValuesStorage(),
    errorTracker: { [unowned self] in
      error = $0.description
      if !self.isErrorExpected {
        XCTFail($0.description)
      }
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

  func test_ResolveString_Boolean() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{boolean_var}")),
      "true"
    )
  }

  func test_ResolveString_Integer() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{integer_var}")),
      "123"
    )
  }

  func test_ResolveString_Number() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{number_var}")),
      "12.9"
    )
  }

  func test_ResolveString_Array() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{array_var}")),
      "[\"value\",[true,123,123.45]]"
    )
  }

  func test_ResolveString_Array_WithNumbers_0_1() {
    variables["var"] = .array([0, 1, 0.0, 1.0])

    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{var}")),
      "[0,1,0.0,1.0]"
    )
  }

  func test_ResolveString_Array_WithBooleans() {
    variables["var"] = .array([true, false])

    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{var}")),
      "[true,false]"
    )
  }

  func test_ResolveString_Dictionary() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{dict_var}")),
      "{\"boolean\":true,\"integer\":1,\"number\":1.0,\"string\":\"value\"}"
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

    XCTAssertEqual(
      error,
      "Failed to initialize Color from string: invalid. Expression: @{'invalid'}"
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
    let value: Bool = expressionResolver
      .resolveNumeric(expression("@{'@{string_var}' == string_var}"))!
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

    XCTAssertEqual(
      error,
      "Failed to initialize TestEnum from string: invalid. Expression: @{'invalid'}"
    )
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
      ["value", [true, 123, 123.45] as [AnyHashable]]
    )
  }

  func test_ResolveDict_WithVariable() throws {
    XCTAssertEqual(
      expressionResolver.resolveDict(expression("@{dict_var}")) as! DivDictionary,
      ["boolean": true, "integer": 1, "number": 1.0, "string": "value"]
    )
  }

  func test_resolveString_FromString_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveString("Some string"),
      "Some string"
    )
  }

  func test_resolveString_FromString_Expression() {
    XCTAssertEqual(
      expressionResolver.resolveString("@{string_var}"),
      "string value"
    )
  }

  func test_resolveString_FromString_InvalidExpression() {
    isErrorExpected = true

    XCTAssertNil(
      expressionResolver.resolveString("@{invalid_expression}")
    )

    XCTAssertEqual(
      error,
      "Variable 'invalid_expression' is missing. Expression: @{invalid_expression}"
    )
  }

  func test_resolveColor_FromString_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveColor("#CCBBAA"),
      color("#CCBBAA")
    )
  }

  func test_resolveColor_FromString_Expression() {
    XCTAssertEqual(
      expressionResolver.resolveColor("@{'#CCBBAA'}"),
      color("#CCBBAA")
    )
  }

  func test_resolveColor_FromString_InvalidValue() {
    isErrorExpected = true

    XCTAssertNil(
      expressionResolver.resolveColor("@{'invalid'}")
    )

    XCTAssertEqual(
      error,
      "Failed to initialize Color from string: invalid. Expression: @{'invalid'}"
    )
  }

  func test_resolveNumeric_FromString_NumberExpression() {
    let value: Double? = expressionResolver.resolveNumeric("@{number_var}")
    XCTAssertEqual(value, 12.9)
  }

  func test_resolveNumeric_FromString_InvalidValueType() {
    isErrorExpected = true

    let value: Double? = expressionResolver.resolveNumeric("@{'invalid'}")
    XCTAssertNil(value)

    XCTAssertEqual(
      error,
      "Invalid result type: expected Number, got String. Expression: @{'invalid'}"
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

  func test_resolveString_WithValidatorValid() {
    XCTAssertEqual(
      expressionResolver.resolveString(
        expressionWithValidator("@{string_var}", isValid: true)
      ),
      "string value"
    )
  }

  func test_resolveString_WithValidatorInvalid() {
    isErrorExpected = true

    XCTAssertNil(
      expressionResolver.resolveString(
        expressionWithValidator("@{string_var}", isValid: false)
      )
    )

    XCTAssertEqual(error, "Failed to validate value: string value. Expression: @{string_var}")
  }

  func test_resolveNumeric_WithValidatorValid() {
    XCTAssertEqual(
      expressionResolver.resolveNumeric(
        expressionWithValidator("@{number_var}", isValid: true)
      ),
      12.9
    )
  }

  func test_resolveNumeric_WithValidatorInvalid() {
    isErrorExpected = true

    XCTAssertNil(
      expressionResolver.resolveNumeric(
        expressionWithValidator("@{number_var}", isValid: false)
      )
    )

    XCTAssertEqual(error, "Failed to validate value: 12.9. Expression: @{number_var}")
  }

  func test_resolveNumeric_WithNestedExpressionAndValidatorInvalid() {
    isErrorExpected = true

    XCTAssertNil(
      expressionResolver.resolveNumeric(
        expressionWithValidator("@{'@{string_var}' == string_var}", isValid: false)
      )
    )

    XCTAssertEqual(
      error,
      "Failed to validate value: true. Expression: @{'@{string_var}' == string_var}"
    )
  }
}

private func expressionWithValidator<T>(_ expression: String, isValid: Bool) -> Expression<T> {
  .link(ExpressionLink(rawValue: expression, validator: AnyValueValidator { _ in isValid })!)
}

private enum TestEnum: String {
  case first
  case second
}
