@testable import DivKit

import XCTest

import CommonCorePublic

final class ExpressionTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    let files = [
      "functions_arithmetic_integer",
      "functions_arithmetic_number",
      "functions_array",
      "functions_color_argb",
      "functions_color_getters",
      "functions_color_setters",
      "functions_comparison_integer",
      "functions_comparison_number",
      "functions_datetime",
      "functions_dict",
      "functions_interval",
      "functions_stored_values",
      "functions_string",
      "functions_to_boolean",
      "functions_to_color",
      "functions_to_integer",
      "functions_to_number",
      "functions_to_string",
      "functions_to_url",
      "functions_unsupported",
      "functions_variables",
      "literals_boolean",
      "literals_integer",
      "literals_number",
      "literals_number_scientific",
      "literals_string",
//      "operations_comparison_boolean",
      "operations_comparison_datetime",
      "operations_comparison_integer",
      "operations_comparison_number",
//      "operations_comparison_with_different_types",
      "operations_div_integer",
      "operations_div_number",
      "operations_equality_boolean",
      "operations_equality_color",
      "operations_equality_datetime",
      "operations_equality_integer",
      "operations_equality_number",
      "operations_equality_string",
      "operations_equality_url",
//      "operations_equality_with_different_types",
      "operations_logical_boolean",
      "operations_logical_with_different_types",
      "operations_mod_integer",
      "operations_mod_number",
      "operations_mul_integer",
      "operations_mul_number",
      "operations_sub_integer",
      "operations_sub_number",
      "operations_sum_integer",
      "operations_sum_number",
//      "operations_sum_string",
      "operations_ternary",
//      "operations_try",
//      "operations_unsupported",
      "operations_unsupported_datetime",
      "string_escape",
      "string_regex",
      "string_templates",
//      "variables_names",
//      "variables_values",
      "whitespace",
    ]

    var testCases: [(name: String, data: ExpressionTestCase)] = []
    try! files.forEach { fileName in
      try makeTestCases(fileName: fileName).forEach {
        testCases.append(("\(fileName): \($0.name)", $0))
      }
    }

    return makeSuite(input: testCases, test: runTest)
  }
}

private func makeTestCases(fileName: String) throws -> [ExpressionTestCase] {
  let url = Bundle(for: DivKitTests.self)
    .url(forResource: fileName, withExtension: "json", subdirectory: "expression_test_data")!
  return try JSONDecoder()
    .decode(TestCases.self, from: Data(contentsOf: url))
    .cases
    .filter { $0.platforms.contains(.ios) }
}

private func runTest(_ testCase: ExpressionTestCase) {
  switch testCase.expected {
  case let .string(expectedValue):
    XCTAssertEqual(testCase.resolveValue(), expectedValue)
  case let .double(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .integer(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .bool(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .datetime(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .error(expectedMessage):
    var errorMessage = ""
    _ = testCase.resolveValue(errorTracker: { errorMessage = $0.message })
    if expectedMessage.isEmpty {
      // Can throw any message
      XCTAssertFalse(errorMessage.isEmpty)
    } else {
      XCTAssertEqual(errorMessage, expectedMessage)
    }
  case .none:
    XCTAssertNil(testCase.resolveValue())
  }
}

private struct TestCases: Decodable {
  let cases: [ExpressionTestCase]
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
  fileprivate func resolveValue(
    errorTracker: ExpressionErrorTracker? = nil
  ) -> String? {
    let expression: Expression<String>? = try? ExpressionLink<String>(
      rawValue: expression,
      errorTracker: errorTracker
    ).map { .link($0) } ?? .value(expression)
    let resolver = ExpressionResolver(
      variables: variables,
      persistentValuesStorage: DivPersistentValuesStorage(),
      errorTracker: errorTracker
    )
    return resolver.resolveString(expression)
  }

  fileprivate func resolveNumeric<T: Equatable>() -> T? {
    let expression: Expression<T>? = try? ExpressionLink<T>(rawValue: expression)
      .map { .link($0) }
    let resolver = ExpressionResolver(
      variables: variables,
      persistentValuesStorage: DivPersistentValuesStorage()
    )
    return resolver.resolveNumeric(expression)
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
    self = try DivTemplates(dictionary: [:]).parseValue(
      type: DivVariableTemplate.self,
      from: JSONDictionary(from: decoder).untypedJSON()
    ).unwrap()
  }
}
