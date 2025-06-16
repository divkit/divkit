@_spi(Internal) @testable @preconcurrency import DivKit
import LayoutKit
import VGSL
import XCTest

final class IntegrationTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: makeTestCases()) { data in
      let expectation = XCTestExpectation(description: "Async operation completed")
      let reporter = MockReporter()
      let divkitComponents = DivKitComponents(reporter: reporter)

      Task {
        await createDivView(data.divData, components: divkitComponents)
        Task { @MainActor in
          runTest(data, divkitComponents: divkitComponents, reporter: reporter)
          expectation.fulfill()
        }
      }
      XCTWaiter().wait(for: [expectation])
    }
  }
}

private func makeTestCases() -> [(String, IntegrationTestData)] {
  getFiles(
    "integration_test_data",
    forBundle: Bundle(for: IntegrationTests.self)
  ).map { url in
    let fileName = url.lastPathComponent
    let testData = try! JSONDecoder()
      .decode(IntegrationTestData.self, from: Data(contentsOf: url))
    return ("\(fileName): \(testData.description)", testData)
  }
}

private func createDivView(_ divData: DivData, components: DivKitComponents) async {
  let divView = await DivView(divKitComponents: components)
  await divView.setSource(DivViewSource(kind: .divData(divData), cardId: cardId))
}

private func runTest(
  _ testData: IntegrationTestData,
  divkitComponents: DivKitComponents,
  reporter: MockReporter
) {
  testData.cases.filter { $0.platforms.contains(.ios) }.forEach { testCase in
    testCase.expected.forEach {
      switch $0 {
      case let .variable(variableName, variable):
        if let variable = variable.divVariableValue {
          divkitComponents.variablesStorage.createDefaultVariable(
            path: cardId.path,
            name: DivVariableName(rawValue: variableName),
            value: variable
          )
        }
      case .error: break
      }
    }

    testCase.divActions?.forEach {
      divkitComponents.actionHandler.handle(
        $0,
        path: cardId.path,
        source: .tap,
        sender: nil
      )
    }

    testCase.expected.forEach {
      if case let .variable(variableName, variable) = $0 {
        XCTAssertEqual(
          variable.divVariableValue,
          divkitComponents.variablesStorage.getVariableValue(
            cardId: cardId,
            name: DivVariableName(rawValue: variableName)
          )
        )
      }
    }

    let expectedErrors = testCase.expected.compactMap {
      if case let .error(message) = $0 {
        return message
      }
      return nil
    }
    XCTAssertEqual(expectedErrors, reporter.errorMessages)
  }
}

private final class MockReporter: @unchecked Sendable, DivReporter {
  private(set) var errorMessages = [String]()

  func reportError(cardId _: DivCardID, error: DivError) {
    errorMessages.append(error.message)
  }
}

private struct IntegrationTestData: Decodable, @unchecked Sendable {
  init(from decoder: any Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    let cardContainer = try container.nestedContainer(
      keyedBy: DivDataCodingKeys.self,
      forKey: .divData
    )

    self.description = try container.decode(String.self, forKey: .description)
    self.divData = try cardContainer.decode(DivData.self, forKey: .card)
    self.cases = try container.decode([IntegrationTestCase].self, forKey: .cases)
  }

  let description: String
  let divData: DivData
  let cases: [IntegrationTestCase]

  private enum CodingKeys: String, CodingKey {
    case description
    case divData = "div_data"
    case cases
  }

  private enum DivDataCodingKeys: String, CodingKey {
    case card
  }
}

private struct IntegrationTestCase: Decodable {
  let divActions: [DivAction]?
  let expected: [Expected]
  let platforms: [Platform]

  private enum CodingKeys: String, CodingKey {
    case divActions = "div_actions", expected, platforms
  }
}

private enum Expected: Decodable {
  case variable(String, ExpectedValue)
  case error(String)

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)

    let type = try container.decode(String.self, forKey: .type)
    switch type {
    case "variable":
      let variable = try container.decode(ExpectedValue.self, forKey: .value)
      let variableName = try container.decode(String.self, forKey: .variableName)
      self = .variable(variableName, variable)
    case "error":
      self = .error(try container.decode(String.self, forKey: .value))
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
    case variableName = "variable_name", type, value
  }
}

extension ExpectedValue {
  fileprivate var divVariableValue: DivVariableValue? {
    switch self {
    case let .string(value):
      return .string(value)
    case let .double(value):
      return .number(value)
    case let .integer(value):
      return .integer(value)
    case let .bool(value):
      return .bool(value)
    case let .color(value):
      return .color(value)
    case let .datetime(value):
      return .string(value.formatString)
    case let .array(value):
      return .array(value)
    case let .dict(value):
      return .dict(value)
    case .error: return nil
    }
  }
}

extension DivVariablesStorage {
  fileprivate func createDefaultVariable(
    path: UIElementPath,
    name: DivVariableName,
    value: DivVariableValue
  ) {
    let defaultValue: DivVariableValue = switch value {
    case .string:
      .string("")
    case .integer:
      .integer(0)
    case .number:
      .number(0.0)
    case .bool:
      .bool(false)
    case .color:
      .color(.black)
    case .url:
      .url(url("empty://"))
    case .array:
      .array([])
    case .dict:
      .dict([:])
    }
    append(variables: [name: defaultValue], for: path.cardId, replaceExisting: false)
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

private let cardId: DivCardID = "test_card"
