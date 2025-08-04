@_spi(Internal) @testable @preconcurrency import DivKit
import DivKitTestsSupport
import LayoutKit
import VGSL
import XCTest

final class IntegrationTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases()) { data in
      let expectation = XCTestExpectation(description: "Async operation completed")

      Task { @MainActor in
        await runTest(data)
        expectation.fulfill()
      }
      XCTWaiter().wait(for: [expectation])
    }
  }
}

private func makeTestCases() -> [(String, IntegrationTestData)] {
  getFiles(
    "integration_test_data",
    forBundle: Bundle(for: IntegrationTests.self)
  ).flatMap { url in
    let fileName = url.lastPathComponent
    let test = try! IntegrationTest(Data(contentsOf: url))

    return test.cases
      .enumerated()
      .filter { $1.platforms.contains(.ios) }
      .map { index, testCase in
        (
          "\(fileName): test case index - [\(index)]",
          IntegrationTestData(divData: test.divData, testCase: testCase)
        )
      }
  }
}

@MainActor
private func runTest(_ testData: IntegrationTestData) async {
  let testCase = testData.testCase
  let reporter = MockReporter()
  DivKitLogger.isEnabled = true
  DivKitLogger.setLogger { reporter.insertErrorMessage($0) }
  let globalStorage = DivVariableStorage()
  globalStorage.put(
    testCase.expected.makeDefaultVariables(),
    notifyObservers: false
  )
  let divkitComponents = DivKitComponents(
    flagsInfo: DivFlagsInfo(initializeTriggerOnSet: false),
    reporter: reporter,
    variablesStorage: DivVariablesStorage(outerStorage: globalStorage)
  )

  let divView = DivView(divKitComponents: divkitComponents)
  await divView.setSource(DivViewSource(kind: .divData(testData.divData), cardId: cardId))

  testCase.divActions?.forEach {
    divkitComponents.actionHandler.handle(
      $0,
      path: cardId.path,
      source: .tap,
      sender: nil
    )
  }

  let task = Task { @MainActor in
    for item in testCase.expected {
      switch item {
      case let .variable(name, value):
        let variableValue = divkitComponents.variablesStorage.getVariableValue(
          cardId: cardId,
          name: DivVariableName(rawValue: name)
        )
        if case let .unorderedArray(expectedArray) = value {
          let variableArray: DivArray? = if case let .array(arr) = variableValue {
            arr
          } else {
            nil
          }
          XCTAssertTrue(
            expectedArray.isEqualUnordered(variableArray),
            "Variable name - '\(name)'"
          )
        } else {
          XCTAssertEqual(
            value.divVariableValue,
            variableValue,
            "Variable name - '\(name)'"
          )
        }

      case let .error(message):
        XCTAssert(
          reporter.errorMessages.contains(message),
          "Error: [\(message)] is not found in errors: \(reporter.errorMessages)"
        )
      }
    }
  }

  await task.value
}

private final class MockReporter: @unchecked Sendable, DivReporter {
  private(set) var errorMessages = Set<String>()

  func reportError(cardId _: DivCardID, error: DivError) {
    errorMessages.insert(error.message)
  }

  func insertErrorMessage(_ message: String) {
    errorMessages.insert(message)
  }
}

private struct IntegrationTestData {
  let divData: DivData
  let testCase: IntegrationTestCase
}

private struct IntegrationTest: Decodable, @unchecked Sendable {
  let description: String
  let divData: DivData
  let cases: [IntegrationTestCase]

  init(_ data: Data) throws {
    let json = try JSONSerialization.jsonObject(with: data) as! [String: Any]
    let casesJson = json["cases"]!
    let divDataJson = json["div_data"] as! [String: Any]
    let card = divDataJson["card"] as! [String: Any]
    let templates = divDataJson["templates"] as? [String: Any] ?? [:]

    self.description = json["description"] as! String
    self.divData = try DivData.resolve(card: card, templates: templates).unwrap()
    self.cases = try! JSONDecoder().decode(
      [IntegrationTestCase].self,
      from: try JSONSerialization.data(withJSONObject: casesJson)
    )
  }

}

private struct IntegrationTestCase: Decodable {
  private enum CodingKeys: String, CodingKey {
    case divActions = "div_actions", expected, platforms
  }

  let divActions: [DivAction]?
  let expected: [Expected]
  let platforms: [Platform]

}

private enum Expected: Decodable {
  case variable(String, ExpectedValue)
  case error(String)

  private enum CodingKeys: String, CodingKey {
    case variableName = "variable_name", type, value
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)

    let type = try container.decode(String.self, forKey: .type)
    switch type {
    case "variable":
      let variable = try container.decode(ExpectedValue.self, forKey: .value)
      let variableName = try container.decode(String.self, forKey: .variableName)
      self = .variable(variableName, variable)
    case "error":
      self = try .error(container.decode(String.self, forKey: .value))
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

}

extension DivAction: Swift.Decodable {
  public convenience init(from decoder: Decoder) throws {
    let action = try DivTemplates(dictionary: [:]).parseValue(
      type: DivActionTemplate.self,
      from: JSONDictionary(from: decoder).untypedJSON()
    ).unwrap()

    self.init(
      downloadCallbacks: action.downloadCallbacks,
      isEnabled: action.isEnabled,
      logId: action.logId,
      logUrl: action.logUrl,
      menuItems: action.menuItems,
      payload: action.payload,
      referer: action.referer,
      scopeId: action.scopeId,
      typed: action.typed,
      url: action.url
    )
  }
}

extension DivData: Swift.Decodable {
  public convenience init(from decoder: Decoder) throws {
    let divData = try DivTemplates(dictionary: [:]).parseValue(
      type: DivDataTemplate.self,
      from: JSONDictionary(from: decoder).untypedJSON()
    ).unwrap()

    self.init(
      functions: divData.functions,
      logId: divData.logId,
      states: divData.states,
      timers: divData.timers,
      transitionAnimationSelector: divData.transitionAnimationSelector,
      variableTriggers: divData.variableTriggers,
      variables: divData.variables
    )
  }
}

extension ExpectedValue {
  fileprivate var divVariableValue: DivVariableValue? {
    switch self {
    case let .string(value):
      .string(value)
    case let .double(value):
      .number(value)
    case let .integer(value):
      .integer(value)
    case let .bool(value):
      .bool(value)
    case let .color(value):
      .color(value)
    case let .url(value):
      .url(value)
    case let .datetime(value):
      .string(value.formatString)
    case let .array(value):
      .array(value)
    case let .dict(value):
      .dict(value)
    case let .unorderedArray(value):
      .array(value)
    case .error: nil
    }
  }
}

extension [Expected] {
  fileprivate func makeDefaultVariables() -> DivVariables {
    reduce(into: DivVariables()) {
      switch $1 {
      case let .variable(name, value):
        if let defaultValue = makeDefault(value) {
          $0[DivVariableName(rawValue: name)] = defaultValue
        }
      case .error: break
      }
    }
  }
}

private func makeDefault(_ value: ExpectedValue) -> DivVariableValue? {
  switch value {
  case .string, .datetime:
    .string("")
  case .integer:
    .integer(0)
  case .double:
    .number(0.0)
  case .bool:
    .bool(false)
  case .color:
    .color(.black)
  case .url:
    .url(URL(string: "empty://")!)
  case .array:
    .array([])
  case .dict:
    .dict([:])
  case .unorderedArray:
    .array([])
  case .error:
    nil
  }
}

private let cardId: DivCardID = "test_card"
