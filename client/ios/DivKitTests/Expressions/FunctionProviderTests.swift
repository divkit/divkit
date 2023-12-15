@testable import DivKit

import XCTest

import CommonCorePublic

final class FunctionProviderTests: XCTestCase {
  lazy var function = {
    let cardId = DivCardID(rawValue: "card")
    let variablesStorage = DivVariablesStorage()
    variablesStorage.set(
      cardId: cardId,
      variables: [DivVariableName(rawValue: "variable"): .string("value1")]
    )
    let prototypesData: (String, [String: AnyHashable]) = ("it.", ["variable": "value2"])
    let prototypesStorage = PrototypesValueStorage()
    prototypesStorage.insert(prefix: prototypesData.0, data: prototypesData.1)
    let functionsProvider = makeFunctionsProvider(
      cardId: cardId,
      variablesStorage: variablesStorage,
      variableTracker: { self.trackedResult = Array($0.map(\.rawValue)) },
      persistentValuesStorage: DivPersistentValuesStorage(),
      prototypesStorage: prototypesStorage
    )
    return functionsProvider.functions[.function("getStringValue", arity: .exactly(2))]!
  }()

  var trackedResult: [String] = []

  func test_takingValue() throws {
    XCTAssertEqual(getValue("variable"), "value1")
    XCTAssertEqual(getValue("it.variable"), "value2")
  }

  func test_takingMissingValue() throws {
    XCTAssertEqual(getValue("missing_value"), "fallback_value")
  }

  func test_trackingUsingVariables() throws {
    let _: Any? = getValue("variable")
    XCTAssertEqual(trackedResult, ["variable"])
  }

  func test_missingVariableTracked() throws {
    let _: Any? = getValue("missing_variable")
    XCTAssertEqual(trackedResult, ["missing_variable"])
  }

  func test_prototypeDataNotTracked() throws {
    let _: Any? = getValue("it.variable")
    XCTAssertEqual(trackedResult, [])
  }

  private func getValue<T>(_ argument: String) -> T? {
    try? function.invoke(args: [argument, "fallback_value"]) as? T
  }
}
