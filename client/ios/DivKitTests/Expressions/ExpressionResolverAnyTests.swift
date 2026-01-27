@testable import DivKit
import DivKitTestsSupport
import Foundation
import Testing
import VGSL

@Suite
struct ExpressionResolverAnyTests {
  private class Environment {
    var isErrorExpected = false
    private(set) var error: String?
    var variables: DivVariables = [:]

    lazy var expressionResolver = ExpressionResolver(
      functionsProvider: FunctionsProvider(
        persistentValuesStorage: DivPersistentValuesStorage()
      ),
      customFunctionsStorageProvider: { _ in nil },
      variableValueProvider: { [unowned self] in
        self.variables[DivVariableName(rawValue: $0)]?.typedValue()
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

  @Test
  func resolve_String() {
    #expect(expressionResolver.resolve("Some string") == nil)
  }

  @Test
  func resolve_BooleanConst() {
    #expect(expressionResolver.resolve("@{true}") as? Bool == true)
  }

  @Test
  func resolve_IntegerConst() {
    #expect(expressionResolver.resolve("@{123}") as? Int == 123)
  }

  @Test
  func resolve_NumberConst() {
    #expect(expressionResolver.resolve("@{123.45}") as? Double == 123.45)
  }

  @Test
  func resolve_StringConst() {
    #expect(expressionResolver.resolve("@{'string value'}") as? String == "string value")
  }

  @Test
  func resolve_BooleanVar() {
    environment.variables["var"] = .bool(true)

    #expect(expressionResolver.resolve("@{var}") as? Bool == true)
  }

  @Test
  func resolve_IntegerVar() {
    environment.variables["var"] = .integer(123)

    #expect(expressionResolver.resolve("@{var}") as? Int == 123)
  }

  @Test
  func resolve_NumberVar() {
    environment.variables["var"] = .number(123.45)

    #expect(expressionResolver.resolve("@{var}") as? Double == 123.45)
  }

  @Test
  func resolve_StringVar() {
    environment.variables["var"] = .string("string value")

    #expect(expressionResolver.resolve("@{var}") as? String == "string value")
  }

  @Test
  func resolve_ArrayVar() {
    environment.variables["var"] = .array(["value", 123, true])

    let value = expressionResolver.resolve("@{var}")

    #expect(value as? DivArray == ["value", 123, true])
  }

  @Test
  func resolve_DictVar() {
    environment.variables["var"] = .dict(["boolean": true, "integer": 123, "string": "value"])

    let value = expressionResolver.resolve("@{var}")

    #expect(value as? DivDictionary == ["boolean": true, "integer": 123, "string": "value"])
  }

  @Test
  func resolve_ColorVar() {
    environment.variables["var"] = .color(color("#AABBCC"))

    #expect(expressionResolver.resolve("@{var}") as? Color == color("#FFAABBCC"))
  }

  @Test
  func resolve_ColorExpression() {
    let value = expressionResolver.resolve("@{argb(1.0, 0.5, 0.5, 0.5)}")

    #expect(value as? Color == color("#FF808080"))
  }

  @Test
  func resolve_UrlVar() {
    environment.variables["var"] = .url(url("https://some.url"))

    #expect(expressionResolver.resolve("@{var}") as? URL == url("https://some.url"))
  }

  @Test
  func resolve_StringInterpolation() {
    environment.variables["var"] = .integer(2)

    let value = expressionResolver.resolve("@{var} + @{var} = @{var + var}")

    #expect(value as? String == "2 + 2 = 4")
  }

  @Test
  func resolve_String_WithNestedExpression() {
    environment.variables["var"] = .string("string value")

    let value = expressionResolver.resolve("@{'Value: @{var}'}")

    #expect(value as? String == "Value: string value")
  }

  @Test
  func resolve_Boolean_WithNestedExpression() {
    environment.variables["var"] = .string("value")

    #expect(expressionResolver.resolve("@{var == '@{var}'}") as? Bool == true)
  }

  @Test
  func resolve_InvalidExpression() {
    environment.isErrorExpected = true

    #expect(expressionResolver.resolve("@{unknown_var}") == nil)
    #expect(environment.error == "Variable 'unknown_var' is missing. Expression: @{unknown_var}")
  }
}
