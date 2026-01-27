@testable import DivKit
import DivKitTestsSupport
import Serialization
import Testing
import VGSL

@Suite
struct ExpressionResolverTests {
  private class Environment {
    var variables: DivVariables = [
      "array_var": .array(["value", [true, 123, 123.45] as DivArray]),
      "boolean_var": .bool(true),
      "color_var": .color(color("#AABBCC")),
      "dict_var": .dict(["boolean": true, "integer": 1, "number": 1.0, "string": "value"]),
      "enum_var": .string("first"),
      "integer_var": .integer(123),
      "number_var": .number(12.9),
      "string_var": .string("string value"),
      "url_var": .url(url("https://some.url")),
      "string_var_name": .string("string_var"),
    ]

    var isErrorExpected = false
    private(set) var error: String?
    private(set) var usedVariables: Set<DivVariableName> = []

    lazy var expressionResolver = ExpressionResolver(
      functionsProvider: FunctionsProvider(
        persistentValuesStorage: DivPersistentValuesStorage()
      ),
      customFunctionsStorageProvider: { _ in nil },
      variableValueProvider: { [unowned self] in
        let varibleName = DivVariableName(rawValue: $0)
        self.usedVariables.insert(varibleName)
        return self.variables[varibleName]?.typedValue()
      },
      errorTracker: { [unowned self] in
        error = $0.description
        if !self.isErrorExpected {
          Issue.record(Comment(rawValue: $0.description))
        }
      }
    )
  }

  private let environment = Environment()

  private var expressionResolver: ExpressionResolver {
    environment.expressionResolver
  }

  private var error: String? {
    environment.error
  }

  private var usedVariables: Set<DivVariableName> {
    environment.usedVariables
  }

  @Test
  func resolveString_Constant() {
    #expect(
      expressionResolver.resolveString(.value("Some string")) ==
        "Some string"
    )
  }

  @Test
  func resolveString_WithVariable() {
    #expect(
      expressionResolver.resolveString(expression("@{string_var}")) ==
        "string value"
    )
  }

  @Test
  func resolveString_WithNestedExpression() {
    #expect(
      expressionResolver.resolveString(expression("@{'Value: @{string_var}'}")) ==
        "Value: string value"
    )
  }

  @Test
  func resolveString_Boolean() {
    #expect(
      expressionResolver.resolveString(expression("@{boolean_var}")) ==
        "true"
    )
  }

  @Test
  func resolveString_Integer() {
    #expect(
      expressionResolver.resolveString(expression("@{integer_var}")) ==
        "123"
    )
  }

  @Test
  func resolveString_Number() {
    #expect(
      expressionResolver.resolveString(expression("@{number_var}")) ==
        "12.9"
    )
  }

  @Test
  func resolveString_Array() {
    #expect(
      expressionResolver.resolveString(expression("@{array_var}")) ==
        "[\"value\",[true,123,123.45]]"
    )
  }

  @Test
  func resolveString_Array_WithNumbers_0_1() {
    environment.variables["var"] = .array([0, 1, 0.0, 1.0])

    #expect(
      expressionResolver.resolveString(expression("@{var}")) ==
        "[0,1,0.0,1.0]"
    )
  }

  @Test
  func resolveString_Array_WithBooleans() {
    environment.variables["var"] = .array([true, false])

    #expect(
      expressionResolver.resolveString(expression("@{var}")) ==
        "[true,false]"
    )
  }

  @Test
  func resolveString_Dictionary() {
    #expect(
      expressionResolver.resolveString(expression("@{dict_var}")) ==
        "{\"boolean\":true,\"integer\":1,\"number\":1.0,\"string\":\"value\"}"
    )
  }

  @Test
  func resolveColor_Constant() {
    #expect(
      expressionResolver.resolveColor(.value(color("#CCBBAA"))) ==
        color("#CCBBAA")
    )
  }

  @Test
  func resolveColor_Expression() {
    #expect(
      expressionResolver.resolveColor(expression("@{'#CCBBAA'}")) ==
        color("#CCBBAA")
    )
  }

  @Test
  func resolveColor_Invalid() {
    environment.isErrorExpected = true

    #expect(expressionResolver.resolveColor(expression("@{'invalid'}")) == nil)
    #expect(error == "Failed to initialize Color from string: invalid. Expression: @{'invalid'}")
  }

  @Test
  func resolveUrl_Constant() {
    #expect(
      expressionResolver.resolveUrl(.value(url("https://some.url"))) ==
        url("https://some.url")
    )
  }

  @Test
  func resolveUrl_Expression() {
    #expect(
      expressionResolver.resolveUrl(expression("@{'https://some.url'}")) ==
        url("https://some.url")
    )
  }

  @Test
  func resolveNumeric_IntegerConstant() {
    let value: Int? = expressionResolver.resolveNumeric(expression("@{123}"))
    #expect(value == 123)
  }

  @Test
  func resolveNumeric_IntegerExpression() {
    let value: Int? = expressionResolver.resolveNumeric(expression("@{integer_var}"))
    #expect(value == 123)
  }

  @Test
  func resolveNumeric_NumberConstant() {
    let value: Double? = expressionResolver.resolveNumeric(expression("@{12.9}"))
    #expect(value == 12.9)
  }

  @Test
  func resolveNumeric_NumberExpression() {
    let value: Double? = expressionResolver.resolveNumeric(expression("@{number_var}"))
    #expect(value == 12.9)
  }

  @Test
  func resolveNumeric_BooleanConstant() {
    let value: Bool? = expressionResolver.resolveNumeric(expression("@{true}"))
    #expect(value == true)
  }

  @Test
  func resolveNumeric_BooleanExpression() {
    let value: Bool? = expressionResolver.resolveNumeric(expression("@{boolean_var}"))
    #expect(value == true)
  }

  @Test
  func resolveNumeric_NestedBooleanExpression() {
    let value: Bool? = expressionResolver.resolveNumeric(
      expression("@{'@{string_var}' == string_var}")
    )
    #expect(value == true)
  }

  @Test
  func resolveNumeric_BooleanFromInteger_False() {
    let value: Bool? = expressionResolver.resolveNumeric(expression("@{0}"))
    #expect(value == false)
  }

  @Test
  func resolveNumeric_BooleanFromInteger_True() {
    let value: Bool? = expressionResolver.resolveNumeric(expression("@{1}"))
    #expect(value == true)
  }

  @Test
  func resolveNumeric_BooleanFromInteger_Invalid() {
    environment.isErrorExpected = true

    let value: Bool? = expressionResolver.resolveNumeric(expression("@{3}"))

    #expect(value == nil)
    #expect(error == "Invalid result type: expected Boolean, got Integer. Expression: @{3}")
  }

  @Test
  func resolveNumeric_BooleanFromNumber_False() {
    let value: Bool? = expressionResolver.resolveNumeric(expression("@{0.0}"))
    #expect(value == false)
  }

  @Test
  func resolveNumeric_BooleanFromNumber_True() {
    let value: Bool? = expressionResolver.resolveNumeric(expression("@{1.0}"))
    #expect(value == true)
  }

  @Test
  func resolveNumeric_BooleanFromNumber_Invalid() {
    environment.isErrorExpected = true

    let value: Bool? = expressionResolver.resolveNumeric(expression("@{1.2}"))

    #expect(value == nil)
    #expect(error == "Invalid result type: expected Boolean, got Number. Expression: @{1.2}")
  }

  @Test
  func resolveNumeric_IntegerAsNumber() {
    let value: Double? = expressionResolver.resolveNumeric(expression("@{integer_var}"))
    #expect(value == 123.0)
  }

  @Test
  func resolveNumeric_NumberAsInteger() {
    let value: Int? = expressionResolver.resolveNumeric(expression("@{number_var}"))
    #expect(value == 12)
  }

  @Test
  func resolveEnum_WithInvalidValue() {
    environment.isErrorExpected = true

    let value: TestEnum? = expressionResolver.resolveEnum(expression("@{'invalid'}"))
    #expect(value == nil)
    #expect(error == "Failed to initialize TestEnum from string: invalid. Expression: @{'invalid'}")
  }

  @Test
  func resolveEnum_WithConstant() {
    #expect(
      expressionResolver.resolveEnum(.value(TestEnum.second)) ==
        TestEnum.second
    )
  }

  @Test
  func resolveEnum_WithVariable() {
    #expect(
      expressionResolver.resolveEnum(expression("@{enum_var}")) ==
        TestEnum.first
    )
  }

  @Test
  func resolveArray_WithVariable() throws {
    let value = expressionResolver.resolveArray(expression("@{array_var}")) as? DivArray

    #expect(value == ["value", [true, 123, 123.45] as DivArray])
  }

  @Test
  func resolveDict_WithVariable() throws {
    let value = expressionResolver.resolveDict(expression("@{dict_var}")) as? DivDictionary

    #expect(value == ["boolean": true, "integer": 1, "number": 1.0, "string": "value"])
  }

  @Test
  func resolveString_FromString_Constant() {
    #expect(
      expressionResolver.resolveString("Some string") ==
        "Some string"
    )
  }

  @Test
  func resolveString_FromString_Expression() {
    #expect(
      expressionResolver.resolveString("@{string_var}") ==
        "string value"
    )
  }

  @Test
  func resolveString_FromString_InvalidExpression() {
    environment.isErrorExpected = true

    #expect(expressionResolver.resolveString("@{invalid_expression}") == nil)
    #expect(error == "Variable 'invalid_expression' is missing. Expression: @{invalid_expression}")
  }

  @Test
  func resolveColor_FromString_Constant() {
    #expect(
      expressionResolver.resolveColor("#CCBBAA") ==
        color("#CCBBAA")
    )
  }

  @Test
  func resolveColor_FromString_Expression() {
    #expect(
      expressionResolver.resolveColor("@{'#CCBBAA'}") ==
        color("#CCBBAA")
    )
  }

  @Test
  func resolveColor_FromString_InvalidValue() {
    environment.isErrorExpected = true

    #expect(expressionResolver.resolveColor("@{'invalid'}") == nil)
    #expect(error == "Failed to initialize Color from string: invalid. Expression: @{'invalid'}")
  }

  @Test
  func resolveNumeric_FromString_NumberExpression() {
    let value: Double? = expressionResolver.resolveNumeric("@{number_var}")

    #expect(value == 12.9)
  }

  @Test
  func resolveNumeric_FromString_InvalidValueType() {
    environment.isErrorExpected = true

    let value: Double? = expressionResolver.resolveNumeric("@{'invalid'}")

    #expect(value == nil)
    #expect(error == "Invalid result type: expected Number, got String. Expression: @{'invalid'}")
  }

  @Test
  func tracksVariable_InSimpleExpression() {
    _ = expressionResolver.resolveString(expression("@{string_var}"))

    #expect(usedVariables == ["string_var"])
  }

  @Test
  func tracksVariable_InNestedExpression() {
    _ = expressionResolver.resolveString(expression("@{'@{string_var}'}"))

    #expect(usedVariables == ["string_var"])
  }

  @Test
  func tracksUnknownVariable() {
    environment.isErrorExpected = true

    _ = expressionResolver.resolveString(expression("@{unknown_var}"))

    #expect(usedVariables == ["unknown_var"])
  }

  @Test
  func tracksVariable_InGetValueFunction() {
    _ = expressionResolver.resolveString(
      expression("@{getStringValue('string_var', 'fallback value')}")
    )

    #expect(usedVariables == ["string_var"])
  }

  @Test
  func getStringValue_ReturnsVariableValue() {
    let value = expressionResolver.resolveString(
      expression("@{getStringValue('string_var', 'fallback value')}")
    )

    #expect(value == "string value")
  }

  @Test
  func getStringValue_ReturnsFallbackValue() {
    let value = expressionResolver.resolveString(
      expression("@{getStringValue('unknown_var', 'fallback value')}")
    )

    #expect(value == "fallback value")
  }

  @Test
  func resolveString_WithValidatorValid() {
    let value: String? = expressionResolver.resolveString(
      expressionWithValidator("@{string_var}", isValid: true)
    )

    #expect(value == "string value")
  }

  @Test
  func resolveString_WithValidatorInvalid() {
    environment.isErrorExpected = true

    let value: String? = expressionResolver.resolveString(
      expressionWithValidator("@{string_var}", isValid: false)
    )

    #expect(value == nil)
    #expect(error == "Failed to validate value: string value. Expression: @{string_var}")
  }

  @Test
  func resolveNumeric_WithValidatorValid() {
    let value: Double? = expressionResolver.resolveNumeric(
      expressionWithValidator("@{number_var}", isValid: true)
    )

    #expect(value == 12.9)
  }

  @Test
  func resolveNumeric_WithValidatorInvalid() {
    environment.isErrorExpected = true

    let value: Double? = expressionResolver.resolveNumeric(
      expressionWithValidator("@{number_var}", isValid: false)
    )

    #expect(value == nil)
    #expect(error == "Failed to validate value: 12.9. Expression: @{number_var}")
  }

  @Test
  func resolveNumeric_WithNestedExpressionAndValidatorInvalid() {
    environment.isErrorExpected = true

    let value: Bool? = expressionResolver.resolveNumeric(
      expressionWithValidator("@{'@{string_var}' == string_var}", isValid: false)
    )

    #expect(value == nil)
    #expect(error == "Failed to validate value: true. Expression: @{'@{string_var}' == string_var}")
  }

  @Test
  func extractDynamicVariables_WithGetStringValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getStringValue('var_' + string_var, '')}")
    )

    #expect(variables == ["var_string value"])
  }

  @Test
  func extractDynamicVariables_WithGetIntegerValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getIntegerValue('var_' + string_var, '')}")
    )

    #expect(variables == ["var_string value"])
  }

  @Test
  func extractDynamicVariables_WithGetNumberValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getNumberValue('var_' + string_var, '')}")
    )

    #expect(variables == ["var_string value"])
  }

  @Test
  func extractDynamicVariables_WithGetBooleanValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getBooleanValue('var_' + string_var, '')}")
    )

    #expect(variables == ["var_string value"])
  }

  @Test
  func extractDynamicVariables_WithGetColorValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getColorValue('var_' + string_var, '')}")
    )

    #expect(variables == ["var_string value"])
  }

  @Test
  func extractDynamicVariables_WithGetUrlValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getUrlValue('var_' + string_var, '')}")
    )

    #expect(variables == ["var_string value"])
  }

  @Test
  func extractDynamicVariables_WithNestedGetValueFunctions() {
    let variables = expressionResolver.extractDynamicVariables(
      expression("@{getStringValue('outer_var_' + getStringValue(string_var_name, ''), '')}")
    )

    #expect(variables == ["outer_var_string value", "string_var"])
  }

  @Test
  func extractDynamicVariables_WithNestedGetValueFunctionsAndUnknownVars() {
    let variables = expressionResolver.extractDynamicVariables(
      expression(
        "@{getStringValue('outer_var_' + getStringValue('inner_var_' + string_var, ''), '')}"
      )
    )

    #expect(variables == ["outer_var_", "inner_var_string value"])
  }

  @Test
  func extractDynamicVariables_WithGetValueFunctionsWithFallbackValue() {
    let variables = expressionResolver.extractDynamicVariables(
      expression(
        "@{getStringValue('outer_var_' + getStringValue('inner_var_' + string_var, 'value'), '')}"
      )
    )

    #expect(variables == ["outer_var_value", "inner_var_string value"])
  }
}

private func expression(_ expression: String) -> ExpressionLink<String> {
  ExpressionLink<String>(rawValue: expression)!
}

private func expressionWithValidator<T>(
  _ expression: String,
  isValid: Bool
) -> DivKit.Expression<T> {
  .link(ExpressionLink(rawValue: expression, validator: AnyValueValidator { _ in isValid })!)
}

private enum TestEnum: String {
  case first
  case second
}
