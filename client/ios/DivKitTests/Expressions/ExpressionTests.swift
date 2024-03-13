@testable import DivKit

import XCTest

import CommonCorePublic

final class ExpressionTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases(), test: runTest)
  }
}

private func makeTestCases() -> [(String, ExpressionTestCase)] {
  try! Bundle(for: DivKitTests.self)
    .urls(forResourcesWithExtension: "json", subdirectory: "expression_test_data")!
    .flatMap { url in
      let fileName = url.lastPathComponent
      let testCases = try JSONDecoder()
        .decode(TestCases.self, from: Data(contentsOf: url))
        .cases ?? []
      return testCases
        .filter { $0.platforms.contains(.ios) }
        .map { ("\(fileName): \($0.name)", $0) }
    }
}

private func runTest(_ testCase: ExpressionTestCase) {
  switch testCase.expected {
  case let .string(expectedValue):
    XCTAssertEqual(testCase.resolveValue(), expectedValue)
  case let .double(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric()!, expectedValue, accuracy: 1e-10)
  case let .integer(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .bool(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .color(expectedValue):
    XCTAssertEqual(testCase.resolveColor(), expectedValue)
  case let .datetime(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .array(expectedValue):
    XCTAssertEqual(testCase.resolveArray(), expectedValue)
  case let .dict(expectedValue):
    XCTAssertEqual(testCase.resolveDict(), expectedValue)
  case let .error(expectedMessage):
    var errorMessage = ""
    _ = testCase.resolveValue(errorTracker: { errorMessage = $0.message })
    if expectedMessage.isEmpty {
      // Can throw any message
      XCTAssertFalse(errorMessage.isEmpty, "Expected error")
    } else {
      XCTAssertEqual(errorMessage, expectedMessage)
    }
  }
}

private struct TestCases: Decodable {
  let cases: [ExpressionTestCase]?
}

private struct ExpressionTestCase: Decodable {
  let name: String
  let expression: String
  let variables: DivVariables
  let expected: ExpectedValue
  let platforms: [Platform]

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    name = try container.decode(String.self, forKey: .name)
    expression = try container.decode(String.self, forKey: .expression)
    expected = try container.decode(ExpectedValue.self, forKey: .expected)
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

  func resolveValue(
    errorTracker: ExpressionErrorTracker? = nil
  ) -> String? {
    let expression: Expression<String>? = ExpressionLink<String>(
      rawValue: expression,
      errorTracker: errorTracker
    ).map { .link($0) } ?? .value(expression)
    return makeExpressionResolver(errorTracker: errorTracker).resolveString(expression)
  }

  func resolveNumeric<T: Equatable>() -> T? {
    makeExpressionResolver().resolveNumeric(link(expression))
  }

  func resolveColor() -> Color? {
    makeExpressionResolver().resolveColor(link(expression))
  }

  func resolveArray() -> [AnyHashable]? {
    makeExpressionResolver().resolveArray(link(expression)) as? [AnyHashable]
  }

  func resolveDict() -> [String: AnyHashable]? {
    makeExpressionResolver().resolveDict(link(expression)) as? [String: AnyHashable]
  }

  private func makeExpressionResolver(
    errorTracker: ExpressionErrorTracker? = nil
  ) -> ExpressionResolver {
    ExpressionResolver(
      variables: variables,
      persistentValuesStorage: DivPersistentValuesStorage(),
      errorTracker: errorTracker ?? { XCTFail($0.message) }
    )
  }

  private func link<T>(_ expression: String) -> Expression<T> {
    .link(
      ExpressionLink<T>(
        rawValue: expression,
        errorTracker: { XCTFail($0.message) }
      )!
    )
  }
}

private enum ExpectedValue: Decodable {
  case string(String)
  case double(Double)
  case integer(Int)
  case bool(Bool)
  case color(Color)
  case datetime(Date)
  case array([AnyHashable])
  case dict([String: AnyHashable])
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
      self = .color(Color.color(withHexString: value)!)
    case "url":
      let value = try container.decode(String.self, forKey: .value)
      self = .string(value)
    case "datetime":
      let value = try container.decode(String.self, forKey: .value)
      self = .datetime(value.toDatetime()!)
    case "array":
      let value = try JSONObject(from: decoder).makeDictionary()
      guard let array = value?["value"] as? [AnyHashable] else {
        fallthrough
      }
      self = .array(array)
    case "dict":
      let value = try JSONObject(from: decoder).makeDictionary()
      guard let dict = value?["value"] as? [String: AnyHashable] else {
        fallthrough
      }
      self = .dict(dict)
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
