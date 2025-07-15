@testable import DivKit
import enum DivKit.Expression
import VGSL
import XCTest

final class ExpressionTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases(), test: runTest)
  }
}

private func makeTestCases() -> [(String, ExpressionTestCase)] {
  Bundle(for: DivKitTests.self)
    .urls(forResourcesWithExtension: "json", subdirectory: "expression_test_data")!
    .flatMap { url in
      let fileName = url.lastPathComponent
      let testCases = try! JSONDecoder()
        .decode(TestCases.self, from: Data(contentsOf: url))
        .cases ?? []
      return testCases
        .filter { $0.platforms.contains(.ios) }
        .map { ("\(fileName): \($0.description)", $0) }
    }
}

private func runTest(_ testCase: ExpressionTestCase) {
  switch testCase.expected {
  case let .string(expectedValue):
    XCTAssertEqual(testCase.resolveValue(), expectedValue)
  case let .double(expectedValue):
    let value: Double? = testCase.resolveNumeric()
    XCTAssertNotNil(value)
    if let value {
      XCTAssertEqual(value, expectedValue, accuracy: 1e-10)
    }
  case let .integer(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .bool(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .color(expectedValue):
    XCTAssertEqual(testCase.resolveColor(), expectedValue)
  case let .datetime(expectedValue):
    XCTAssertEqual(testCase.resolveNumeric(), expectedValue)
  case let .url(expectedValue):
    XCTAssertEqual(testCase.resolveUrl(), expectedValue)
  case let .array(expectedValue):
    XCTAssertEqual(testCase.resolveArray(), expectedValue)
  case let .dict(expectedValue):
    XCTAssertEqual(testCase.resolveDict(), expectedValue)
  case let .unorderedArray(expectedValue):
    XCTAssertTrue(expectedValue.isEqualUnordered(testCase.resolveArray()))
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
  let expression: String
  let variables: DivVariables
  let expected: ExpectedValue
  let platforms: [Platform]

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
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
    self.variables = variables.extractDivVariableValues(
      ExpressionResolver(
        functionsProvider: FunctionsProvider(persistentValuesStorage: DivPersistentValuesStorage()),
        variableValueProvider: { _ in nil },
        errorTracker: { XCTFail($0.description) }
      )
    )
  }

  var description: String {
    let formattedExpression = if expression.starts(with: "@{"),
                                 expression.last == "}",
                                 !expression.dropFirst(2).contains("@{") {
      String(expression.dropFirst(2).dropLast())
    } else {
      expression
    }
    return "\(formattedExpression) -> \(expected.description): \(hashValue)"
  }

  private enum CodingKeys: String, CodingKey {
    case expression, variables, expected, platforms
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

  func resolveUrl() -> URL? {
    makeExpressionResolver().resolveUrl(link(expression))
  }

  func resolveColor() -> Color? {
    makeExpressionResolver().resolveColor(link(expression))
  }

  func resolveArray() -> DivArray? {
    makeExpressionResolver().resolveArray(link(expression)) as? DivArray
  }

  func resolveDict() -> DivDictionary? {
    makeExpressionResolver().resolveDict(link(expression)) as? DivDictionary
  }

  private func makeExpressionResolver(
    errorTracker: ExpressionErrorTracker? = nil
  ) -> ExpressionResolver {
    ExpressionResolver(
      functionsProvider: FunctionsProvider(
        persistentValuesStorage: DivPersistentValuesStorage()
      ),
      variableValueProvider: {
        variables[DivVariableName(rawValue: $0)]?.typedValue()
      },
      errorTracker: errorTracker ?? { XCTFail($0.description) }
    )
  }

  private func link<T>(_ expression: String) -> Expression<T> {
    .link(
      ExpressionLink<T>(
        rawValue: expression,
        errorTracker: { XCTFail($0.description) }
      )!
    )
  }
}

extension ExpressionTestCase: Hashable {
  static func == (lhs: ExpressionTestCase, rhs: ExpressionTestCase) -> Bool {
    return lhs.expression == rhs.expression &&
    lhs.variables == rhs.variables &&
    lhs.expected.description == rhs.expected.description
  }
  
  func hash(into hasher: inout Hasher) {
    hasher.combine(expression)
    hasher.combine(variables)
    hasher.combine(expected.description)
  }
}

enum ExpectedValue: Decodable {
  case string(String)
  case double(Double)
  case integer(Int)
  case bool(Bool)
  case color(Color)
  case datetime(Date)
  case url(URL)
  case array(DivArray)
  case dict(DivDictionary)
  case unorderedArray(DivArray)
  case error(String)

  init(from decoder: Decoder) throws {
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
      let value = try container.decode(URL.self, forKey: .value)
      self = .url(value)
    case "datetime":
      let value = try container.decode(String.self, forKey: .value)
      self = .datetime(value.toDate()!)
    case "array":
      let value = try JSONObject(from: decoder).makeDictionary()
      guard let rawValue = value?["value"] as? [Any],
            let array = DivArray.fromAny(rawValue) else {
        fallthrough
      }
      self = .array(array)
    case "unordered_array":
      let value = try JSONObject(from: decoder).makeDictionary()
      guard let rawValue = value?["value"] as? [Any],
            let array = DivArray.fromAny(rawValue) else {
        fallthrough
      }
      self = .unorderedArray(array)
    case "dict":
      let value = try JSONObject(from: decoder).makeDictionary()
      guard let rawValue = value?["value"] as? [String: Any],
            let dict = DivDictionary.fromAny(rawValue) else {
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

  var description: String {
    switch self {
    case let .string(value):
      formatArgForError(value)
    case let .double(value):
      formatArgForError(value)
    case let .integer(value):
      formatArgForError(value)
    case let .bool(value):
      formatArgForError(value)
    case let .color(value):
      formatArgForError(value)
    case let .datetime(value):
      formatArgForError(value)
    case let .url(value):
      formatArgForError(value)
    case let .array(value):
      formatArgForError(value)
    case let .dict(value):
      formatArgForError(value)
    case let .unorderedArray(value):
      formatArgForError(value)
    case .error:
      "error"
    }
  }

  private enum CodingKeys: String, CodingKey {
    case type, value
  }
}

extension DivVariable: Swift.Decodable {
  public init(from decoder: Decoder) throws {
    self = try DivTemplates(dictionary: [:]).parseValue(
      type: DivVariableTemplate.self,
      from: JSONDictionary(from: decoder).untypedJSON()
    ).unwrap()
  }
}
