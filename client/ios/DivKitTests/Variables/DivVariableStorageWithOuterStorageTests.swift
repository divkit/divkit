import DivKit
import DivKitTestsSupport
import VGSL
import XCTest

final class DivVariableStorageWithOuterStorageTest: XCTestCase {
  private let outerStorage = DivVariableStorage()
  private var storage: DivVariableStorage!
  private let disposePool = AutodisposePool()

  private var event: DivVariableStorage.ChangeEvent?

  override func setUp() {
    storage = DivVariableStorage(outerStorage: outerStorage)
    storage.addObserver { [unowned self] in self.event = $0 }
      .dispose(in: disposePool)
  }

  func test_getValue_ReturnsValueFromOuterStorage() {
    storage.put(variables)
    outerStorage.put(outerVariables)

    XCTAssertEqual("outer value", storage.getValue("outer_string_var"))
  }

  func test_getValue_ReturnsLocalValue_ForShadowedVariable() {
    storage.put(variables)
    outerStorage.put(name: "string_var", value: .string("outer value"))

    XCTAssertEqual("value", storage.getValue("string_var"))
  }

  func test_hasValue_ReturnsTrueIfOuterStorageContainsVariable() {
    storage.put(variables)
    outerStorage.put(outerVariables)

    XCTAssertTrue(storage.hasValue("outer_string_var"))
  }

  func test_put_DoesNotSendEvent_WhenShadowingEqualOuterValue() {
    outerStorage.put(["int_var": .integer(1)])
    event = nil

    storage.put(["int_var": .integer(1)])

    XCTAssertNil(event)
    XCTAssertEqual(.integer(1), storage.getVariableValue("int_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_put_SendsEvent_WhenShadowingDifferentOuterValue() {
    outerStorage.put(["int_var": .integer(1)])
    event = nil

    storage.put(["int_var": .integer(2)])

    XCTAssertEqual(["int_var"], event?.changedVariables)
    XCTAssertEqual(.integer(2), storage.getVariableValue("int_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_put_UsesLocalValue_WhenOuterValueIsShadowed() {
    outerStorage.put(["int_var": .integer(1)])
    storage.put(["int_var": .integer(2)])
    event = nil

    storage.put(["int_var": .integer(2)])

    XCTAssertNil(event)
    XCTAssertEqual(.integer(2), storage.getVariableValue("int_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_put_UsesNearestOuterValue_InStorageChain() {
    outerStorage.put(["int_var": .integer(1)])
    storage.put(["int_var": .integer(2)])
    let innerStorage = DivVariableStorage(outerStorage: storage)
    innerStorage.addObserver { [unowned self] in self.event = $0 }
      .dispose(in: disposePool)
    event = nil

    innerStorage.put(["int_var": .integer(2)])

    XCTAssertNil(event)
    XCTAssertEqual(.integer(2), innerStorage.getVariableValue("int_var"))

    innerStorage.put(["int_var": .integer(1)])

    XCTAssertEqual(["int_var"], event?.changedVariables)
    XCTAssertEqual(.integer(1), innerStorage.getVariableValue("int_var"))
    XCTAssertEqual(.integer(2), storage.getVariableValue("int_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_put_UsesInheritedValue_ThroughMultipleOuterStorages() {
    outerStorage.put(["int_var": .integer(1)])
    let innerStorage = DivVariableStorage(outerStorage: storage)
    innerStorage.addObserver { [unowned self] in self.event = $0 }
      .dispose(in: disposePool)
    event = nil

    innerStorage.put(["int_var": .integer(1)])

    XCTAssertNil(event)
    XCTAssertEqual(.integer(1), innerStorage.getVariableValue("int_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_put_SendsEventOnlyForNewVariable_WhenOuterValueIsEqual() {
    outerStorage.put(["int_var": .integer(1)])
    event = nil

    storage.put([
      "int_var": .integer(1),
      "new_var": .string("new value"),
    ])

    XCTAssertEqual(["new_var"], event?.changedVariables)
    XCTAssertEqual(.integer(1), storage.getVariableValue("int_var"))
    XCTAssertEqual(.string("new value"), storage.getVariableValue("new_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_put_DoesNotSendEvent_ForEmptyVariablesWithOuterStorage() {
    outerStorage.put(outerVariables)
    storage.put(variables)
    event = nil

    storage.put([:])

    XCTAssertNil(event)
    XCTAssertEqual(outerVariables + variables, storage.allValues)
    XCTAssertEqual(outerVariables, outerStorage.allValues)
  }

  func test_put_DoesNotSendEvent_WhenNotificationsDisabledWithOuterStorage() {
    outerStorage.put(["int_var": .integer(1)])
    event = nil

    storage.put([
      "int_var": .integer(2),
      "new_var": .string("new value"),
    ], notifyObservers: false)

    XCTAssertNil(event)
    XCTAssertEqual(.integer(2), storage.getVariableValue("int_var"))
    XCTAssertEqual(.string("new value"), storage.getVariableValue("new_var"))
    XCTAssertEqual(["int_var": .integer(1)], outerStorage.allValues)
  }

  func test_update_UpdatesOuterVariable() {
    storage.put(variables)
    outerStorage.put(outerVariables)

    storage.update(name: "outer_string_var", value: .string("new value"))

    XCTAssertEqual("new value", outerStorage.getValue("outer_string_var"))
  }

  func test_update_DoesNotUpdateShadowedVariable() {
    storage.put(variables)
    outerStorage.put(name: "string_var", value: .string("outer value"))

    storage.update(name: "string_var", value: .string("new value"))

    XCTAssertEqual("outer value", outerStorage.getValue("string_var"))
  }

  func test_update_InOuterStorage_TriggersEventInLocalStorage() {
    storage.put(variables)
    outerStorage.put(outerVariables)

    outerStorage.update(name: "outer_string_var", value: .string("new value"))

    XCTAssertEqual(["outer_string_var"], event?.changedVariables)
  }

  func test_clear_DoesNotClearOuterStorage() {
    storage.put(variables)
    outerStorage.put(outerVariables)

    storage.clear()

    XCTAssertEqual(outerVariables, storage.allValues)
  }

  func test_remove_DoesNotAffectOuterStorage() {
    storage.put(variables)
    outerStorage.put(outerVariables)

    storage.remove(variableNames: Set(variables.keys))
    storage.remove(variableNames: Set(outerVariables.keys))

    XCTAssertEqual(outerVariables, storage.allValues)
  }
}

private let variables: DivVariables = [
  "string_var": .string("value"),
  "int_var": .integer(100),
  "number_var": .number(123.34),
  "bool_var": .bool(true),
  "url_var": .url(url("https://test.url")),
]

private let outerVariables: DivVariables = [
  "outer_string_var": .string("outer value"),
  "outer_int_var": .integer(200),
]
