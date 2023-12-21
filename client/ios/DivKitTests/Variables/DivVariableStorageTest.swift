import XCTest

import BasePublic
import DivKit

final class DivVariableSorageTest: XCTestCase {
  private let storage = DivVariableStorage()
  private let disposePool = AutodisposePool()

  private var event: DivVariableStorage.ChangeEvent?

  override func setUp() {
    storage.addObserver { [unowned self] in self.event = $0 }
      .dispose(in: disposePool)
  }

  func test_put_PutsVariable() {
    storage.put(name: "string_var", value: .string("value"))

    XCTAssertEqual(["string_var": .string("value")], storage.allValues)
  }

  func test_put_PutsVariables() {
    storage.put(variables)

    XCTAssertEqual(variables, storage.allValues)
  }

  func test_put_AddsVariable() {
    storage.put(variables)

    storage.put(name: "new_var", value: .string("new value"))

    XCTAssertEqual(variables + ["new_var": .string("new value")], storage.allValues)
  }

  func test_put_RewritesVariable() {
    storage.put(variables)

    storage.put(name: "string_var", value: .string("new value"))

    XCTAssertEqual(variables + ["string_var": .string("new value")], storage.allValues)
  }

  func test_put_RewritesVariables() {
    storage.put(variables)

    let newVariables: DivVariables = [
      "string_var": .string("new value"),
      "int_var": .integer(200),
    ]
    storage.put(newVariables)

    XCTAssertEqual(variables + newVariables, storage.allValues)
  }

  func test_getValue_GetsValue() {
    storage.put(variables)

    XCTAssertEqual("value", storage.getValue("string_var"))
  }

  func test_getValue_ReturnsNilForUnknownVariable() {
    storage.put(variables)

    XCTAssertNil(storage.getValue("unknown_var"))
  }

  func test_update_UpdatesStringVariable() {
    storage.put(variables)

    storage.update(name: "string_var", value: .string("new value"))

    XCTAssertEqual("new value", storage.getValue("string_var"))
  }

  func test_update_UpdatesNumberVariable() {
    storage.put(variables)

    storage.update(name: "number_var", value: .number(234.567))

    XCTAssertEqual(234.567, storage.getValue("number_var"))
  }

  func test_update_DoesNothing_ForUnknownVariable() {
    storage.update(name: "string_var", value: .string("new value"))

    XCTAssertEqual([:], storage.allValues)
  }

  func test_putVaraible_SendsUpdateEvent_WhenVariableAdded() {
    storage.put(name: "string_var", value: .string("string value"))

    XCTAssertEqual(["string_var"], event?.changedVariables)
    XCTAssertEqual([:], event?.oldValues)
  }

  func test_putVaraible_SendsUpdateEvent_WhenVariableChanged() {
    storage.put(variables)
    event = nil

    storage.put(name: "string_var", value: .string("new value"))

    XCTAssertEqual(["string_var"], event?.changedVariables)
    XCTAssertEqual(variables, event?.oldValues)
  }

  func test_putVaraible_DoesNotSendUpdateEvent_WhenVariableWasNotChanged() {
    storage.put(variables)
    event = nil

    storage.put(name: "string_var", value: .string("value"))

    XCTAssertNil(event)
  }

  func test_putVaraibles_SendsUpdateEvent_WhenVariablesAdded() {
    storage.put(variables)

    XCTAssertEqual(Set(variables.keys), event?.changedVariables)
    XCTAssertEqual([:], event?.oldValues)
  }

  func test_putVaraible_SendsUpdateEvent_ForChangedVariablesOnly() {
    storage.put(variables)
    event = nil

    storage.put([
      "string_var": .string("value"),
      "int_var": .integer(200),
    ])

    XCTAssertEqual(["int_var"], event?.changedVariables)
    XCTAssertEqual(variables, event?.oldValues)
  }

  func test_putVaraibles_DoesNotSendUpdateEvent_WhenVariablesWasNotChanged() {
    storage.put(variables)
    event = nil

    storage.put([
      "string_var": .string("value"),
      "int_var": .integer(100),
    ])

    XCTAssertNil(event)
  }

  func test_update_SendsUpdateEvent() {
    storage.put(variables)

    storage.update(name: "string_var", value: .string("new value"))

    XCTAssertEqual(["string_var"], event?.changedVariables)
    XCTAssertEqual(variables, event?.oldValues)
  }

  func test_update_DoesNotSendUpdateEventForSameValue() {
    storage.put(variables)
    event = nil

    storage.update(name: "string_var", value: .string("value"))

    XCTAssertNil(event)
  }

  func test_replaceAll_ReplacesAllVariables() {
    storage.put(variables)
    event = nil

    let newVariables: DivVariables = [
      "string_var": .string("new value"),
      "new_var": .string("value"),
    ]
    storage.replaceAll(newVariables)

    XCTAssertEqual(newVariables, storage.allValues)
  }

  func test_replaceAll_SendsUpdateEvent() {
    storage.put(variables)
    event = nil

    storage.replaceAll([
      "string_var": .string("new value"),
      "new_var": .string("value"),
    ])

    XCTAssertEqual(
      ["string_var", "int_var", "number_var", "bool_var", "url_var", "new_var"],
      event?.changedVariables
    )
    XCTAssertEqual(variables, event?.oldValues)
  }

  func test_replaceAll_SendsUpdateEvent_ForChangedVariablesOnly() {
    storage.put(variables)
    event = nil

    storage.replaceAll([
      "string_var": .string("value"),
      "int_var": .integer(200),
      "number_var": .number(123.34),
      "new_var": .string("value"),
    ])

    XCTAssertEqual(
      ["int_var", "bool_var", "url_var", "new_var"],
      event?.changedVariables
    )
    XCTAssertEqual(variables, event?.oldValues)
  }

  func test_replaceAll_DoesNotSendUpdateEvent_WhenVariablesWasNotChanged() {
    storage.put(variables)
    event = nil

    storage.replaceAll(variables)

    XCTAssertNil(event)
  }

  func test_clear_ClearsStorage() {
    storage.put(variables)

    storage.clear()

    XCTAssertEqual([:], storage.allValues)
  }
}

private let variables: DivVariables = [
  "string_var": .string("value"),
  "int_var": .integer(100),
  "number_var": .number(123.34),
  "bool_var": .bool(true),
  "url_var": .url(url("https://test.url")),
]
