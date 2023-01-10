@testable import DivKit

import XCTest

import CommonCore

final class ExpressionResolvingTests: XCTestCase {
  private var testCase: ExpressionTestCase!

  func test_Literals_Boolean() throws {
    let testCases = try makeTestCases(for: "literals_boolean")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Literals_Integer() throws {
    let testCases = try makeTestCases(for: "literals_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Literals_Number() throws {
    let testCases = try makeTestCases(for: "literals_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Literals_Number_Scientific() throws {
    let testCases = try makeTestCases(for: "literals_number_scientific")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Literals_String() throws {
    let testCases = try makeTestCases(for: "literals_string")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Functions_To_Boolean() throws {
    let testCases = try makeTestCases(for: "functions_to_boolean")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_To_String() throws {
    let testCases = try makeTestCases(for: "functions_to_string")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Functions_To_Number() throws {
    let testCases = try makeTestCases(for: "functions_to_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_To_Integer() throws {
    let testCases = try makeTestCases(for: "functions_to_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_Datetime() throws {
    let testCases = try makeTestCases(for: "functions_datetime")
    let type: ExpressionType<String> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_String() throws {
    let testCases = try makeTestCases(for: "functions_string")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Functions_Interval() throws {
    let testCases = try makeTestCases(for: "functions_interval")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Functions_Color() throws {
    let testCases = try makeTestCases(for: "functions_color_argb")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Functions_Color_Setters() throws {
    let testCases = try makeTestCases(for: "functions_color_setters")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Functions_Color_Getters() throws {
    let testCases = try makeTestCases(for: "functions_color_getters")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Comparison_Datetime() throws {
    let testCases = try makeTestCases(for: "operations_comparison_datetime")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Comparison_Integer() throws {
    let testCases = try makeTestCases(for: "operations_comparison_integer")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Comparison_Number() throws {
    let testCases = try makeTestCases(for: "operations_comparison_number")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_Comparison_Number() throws {
    let testCases = try makeTestCases(for: "functions_comparison_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_Comparison_Integer() throws {
    let testCases = try makeTestCases(for: "functions_comparison_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_Arithmetic_Number() throws {
    let testCases = try makeTestCases(for: "functions_arithmetic_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Functions_Arithmetic_Integer() throws {
    let testCases = try makeTestCases(for: "functions_arithmetic_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_Integer() throws {
    let testCases = try makeTestCases(for: "operations_equality_integer")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_Color() throws {
    let testCases = try makeTestCases(for: "operations_equality_color")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_Datetime() throws {
    let testCases = try makeTestCases(for: "operations_equality_datetime")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_Number() throws {
    let testCases = try makeTestCases(for: "operations_equality_number")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_String() throws {
    let testCases = try makeTestCases(for: "operations_equality_string")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_Boolean() throws {
    let testCases = try makeTestCases(for: "operations_equality_boolean")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Equality_Url() throws {
    let testCases = try makeTestCases(for: "operations_equality_url")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Logical_Boolean() throws {
    let testCases = try makeTestCases(for: "operations_logical_boolean")
    let type: ExpressionType<Bool> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Div_Integer() throws {
    let testCases = try makeTestCases(for: "operations_div_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Div_Number() throws {
    let testCases = try makeTestCases(for: "operations_div_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Mod_Integer() throws {
    let testCases = try makeTestCases(for: "operations_mod_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Mod_Number() throws {
    let testCases = try makeTestCases(for: "operations_mod_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Mul_Integer() throws {
    let testCases = try makeTestCases(for: "operations_mul_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Mul_Number() throws {
    let testCases = try makeTestCases(for: "operations_mul_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Sub_Integer() throws {
    let testCases = try makeTestCases(for: "operations_sub_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Sub_Number() throws {
    let testCases = try makeTestCases(for: "operations_sub_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Sum_Integer() throws {
    let testCases = try makeTestCases(for: "operations_sum_integer")
    let type: ExpressionType<Int> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Sum_Number() throws {
    let testCases = try makeTestCases(for: "operations_sum_number")
    let type: ExpressionType<Double> = .singleItem
    perform(on: testCases, type: type)
  }

  func test_Operations_Ternary() throws {
    let testCases = try makeTestCases(for: "operations_ternary")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_Operations_Unsupported_Datetime() throws {
    let testCases = try makeTestCases(for: "operations_unsupported_datetime")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_String_Escape() throws {
    let testCases = try makeTestCases(for: "string_escape")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  func test_String_Templates() throws {
    let testCases = try makeTestCases(for: "string_templates")
    let type: ExpressionType<String> = .stringBased(initializer: { $0 })
    perform(on: testCases, type: type)
  }

  private func perform<T: Equatable>(on testCases: TestCases, type: ExpressionType<T>) {
    testCases.cases.filter { $0.platforms.contains(.ios) }.forEach {
      testCase = $0
      testCase.check(type: type)
    }
  }
}

private func makeTestCases(for name: String) throws -> TestCases {
  let bundle = Bundle(for: DivKitTests.self)
  let subdirectory = "expression_test_data"
  let url = bundle.url(forResource: name, withExtension: "json", subdirectory: subdirectory)!
  let data = try Data(contentsOf: url)
  return try JSONDecoder().decode(TestCases.self, from: data)
}

private struct TestCases: Decodable {
  let cases: [ExpressionTestCase]
}

private enum ExpressionType<T> {
  case stringBased(initializer: (String) -> T?)
  case singleItem
}

private struct ExpressionTestCase: Decodable {
  let name: String
  let expression: String
  let variables: DivVariables
  let expected: ExpectedValue?
  let platforms: [Platform]

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    name = try container.decode(String.self, forKey: .name)
    expression = try container.decode(String.self, forKey: .expression)
    expected = try? container.decode(ExpectedValue.self, forKey: .expected)
    platforms = try container.decode([Platform].self, forKey: .platforms)

    guard var variablesContainer = try? container.nestedUnkeyedContainer(
      forKey: .variables
    ) else {
      self.variables = [:]
      return
    }

    var variables: [DivVariable] = []
    while !variablesContainer.isAtEnd {
      let variable = try variablesContainer.decode(DivVariable.self)
      variables.append(variable)
    }
    self.variables = variables.extractDivVariableValues()
  }

  private enum CodingKeys: String, CodingKey {
    case name, expression, variables, expected, platforms
  }
}

extension ExpressionTestCase {
  fileprivate func check<T: Equatable>(type: ExpressionType<T>) {
    switch expected {
    case let .string(expectedValue):
      testResolveStringBasedValue(
        expectedValue: expectedValue,
        initializer: { $0 }
      )
    case let .double(expectedValue):
      testResolveSingleItem(expectedValue: expectedValue)
    case let .integer(expectedValue):
      testResolveSingleItem(expectedValue: expectedValue)
    case let .bool(expectedValue):
      testResolveSingleItem(expectedValue: expectedValue)
    case let .datetime(expectedValue):
      testResolveSingleItem(expectedValue: expectedValue)
    case let .error(expectedMessage):
      testErrorResult(type: type, expectedMessage: expectedMessage)
    case .none:
      testNilResult(type: type)
    }
  }

  private func testErrorResult<T: Equatable>(type: ExpressionType<T>, expectedMessage: String) {
    var resultMessage = ""
    _ = resolveValue(type: type, errorTracker: {
      resultMessage = $0.description
    })
    if expectedMessage.isEmpty {
      // Can throw any message
      XCTAssertFalse(resultMessage.isEmpty)
    } else {
      XCTAssertEqual(resultMessage, expectedMessage, "test: \(name)")
    }
  }

  private func testNilResult<T: Equatable>(type: ExpressionType<T>) {
    let result = resolveValue(type: type)
    XCTAssertNil(result, "test: \(name)")
  }

  private func resolveValue<T: Equatable>(
    type: ExpressionType<T>,
    errorTracker: ExpressionErrorTracker? = nil
  ) -> T? {
    let expression: Expression<T>? = try? ExpressionLink<T>(
      rawValue: expression,
      validator: nil,
      errorTracker: errorTracker
    ).map { .link($0) } ?? .value(expression as! T)
    let resolver = ExpressionResolver(variables: variables, errorTracker: errorTracker)
    switch type {
    case .singleItem:
      return resolver.resolveNumericValue(expression: expression)
    case let .stringBased(initializer):
      return resolver.resolveStringBasedValue(
        expression: expression,
        initializer: initializer
      )
    }
  }

  private func testResolveSingleItem<T: Equatable>(expectedValue: T) {
    let expression: Expression<T>? = try? ExpressionLink<T>(
      rawValue: expression,
      validator: nil
    ).map { .link($0) }
    let resolver = ExpressionResolver(variables: variables)
    let result = resolver.resolveNumericValue(expression: expression)
    XCTAssertEqual(result, expectedValue, "test: \(name)")
  }

  private func testResolveStringBasedValue<T: Equatable>(
    expectedValue: T,
    initializer: (String) -> T?
  ) {
    let expression: Expression<T>? = try? ExpressionLink<T>(
      rawValue: expression,
      validator: nil
    ).map { .link($0) } ?? .value(expression as! T)
    let resolver = ExpressionResolver(variables: variables)
    let result = resolver.resolveStringBasedValue(
      expression: expression,
      initializer: initializer
    )
    XCTAssertEqual(result, expectedValue, "test: \(name)")
  }
}

private enum ExpectedValue: Decodable {
  case string(String)
  case double(Double)
  case integer(Int)
  case bool(Bool)
  case datetime(Date)
  case error(String)

  public init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    let type = try container.decode(String.self, forKey: .type)
    switch type {
    case "string":
      let value = try container.decode(String.self, forKey: .value)
      self = .string(value)
    case "number":
      let value = try container.decode(Double.self, forKey: .value)
      self = .double(value)
    case "integer":
      let value = try container.decode(Int.self, forKey: .value)
      self = .integer(value)
    case "boolean":
      let value = try container.decode(Bool.self, forKey: .value)
      self = .bool(value)
    case "color":
      let value = try container.decode(String.self, forKey: .value)
      self = .string(value)
    case "url":
      let value = try container.decode(String.self, forKey: .value)
      self = .string(value)
    case "datetime":
      let value = try container.decode(String.self, forKey: .value)
      self = .datetime(value.toDatetime()!)
    case "error":
      let value = try container.decode(String.self, forKey: .value)
      self = .error(value)
    default:
      throw DecodingError.valueNotFound(
        String.self,
        DecodingError.Context(
          codingPath: container.codingPath,
          debugDescription: "incorrect type: \(type)"
        )
      )
    }
  }

  private enum CodingKeys: String, CodingKey {
    case type, value
  }
}

extension DivVariable: Decodable {
  public init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    let type = try container.decode(String.self, forKey: .type)
    let name = try container.decode(String.self, forKey: .name)
    switch type {
    case StringVariable.type:
      let value = try container.decode(String.self, forKey: .value)
      self = .stringVariable(StringVariable(name: name, value: value))
    case NumberVariable.type:
      let value = try container.decode(Double.self, forKey: .value)
      self = .numberVariable(NumberVariable(name: name, value: value))
    case IntegerVariable.type:
      let value = try container.decode(Int.self, forKey: .value)
      self = .integerVariable(IntegerVariable(name: name, value: value))
    case BooleanVariable.type:
      let boolValue = try? container.decode(Bool.self, forKey: .value)
      let intValue = try? container.decode(Int.self, forKey: .value)
      if boolValue == true || intValue == 1 {
        self = .booleanVariable(BooleanVariable(name: name, value: true))
      } else if boolValue == false || intValue == 0 {
        self = .booleanVariable(BooleanVariable(name: name, value: false))
      } else {
        throw DecodingError.typeMismatch(
          Bool.self,
          DecodingError.Context(
            codingPath: container.codingPath,
            debugDescription: "incorrect boolean value"
          )
        )
      }
    case ColorVariable.type:
      let value = try container.decode(String.self, forKey: .value)
      self = .colorVariable(ColorVariable(name: name, value: Color.color(withHexString: value)!))
    case UrlVariable.type:
      let value = try container.decode(String.self, forKey: .value)
      self = .urlVariable(UrlVariable(name: name, value: URL(string: value)!))
    default:
      throw DecodingError.valueNotFound(
        String.self,
        DecodingError.Context(
          codingPath: container.codingPath,
          debugDescription: "incorrect type: \(type)"
        )
      )
    }
  }

  private enum CodingKeys: String, CodingKey {
    case type, name, value
  }
}
