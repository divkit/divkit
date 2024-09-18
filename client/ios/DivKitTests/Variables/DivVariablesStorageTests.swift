import XCTest

@testable import DivKit
import LayoutKit
import VGSL

final class DivVariablesStorageTest: XCTestCase {
  private let storage = DivVariablesStorage()
  private let disposePool = AutodisposePool()

  private var event: DivVariablesStorage.ChangeEvent?

  override func setUp() {
    storage.addObserver { [unowned self] in self.event = $0 }
      .dispose(in: disposePool)
  }

  func test_makeVariables_ReturnsVariablesByCardId() {
    storage.set(cardId: cardId, variables: variables)

    let anotherVariables: DivVariables = [
      "string_var": .string("another value"),
      "int_var": .integer(200),
    ]
    storage.set(cardId: "another_card", variables: anotherVariables)

    XCTAssertEqual(variables, storage.makeVariables(for: cardId))
    XCTAssertEqual(anotherVariables, storage.makeVariables(for: "another_card"))
  }

  func test_makeVariables_ReturnsNilForUnknownCardId() {
    storage.set(cardId: cardId, variables: variables)

    XCTAssertEqual([:], storage.makeVariables(for: "unknown"))
  }

  func test_makeVariables_MergesGlobalVariables() {
    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)
    storage.set(cardId: cardId, variables: variables)

    XCTAssertEqual(globalVariables + variables, storage.makeVariables(for: cardId))
  }

  func test_set_RewritesLocalVariables() {
    storage.set(cardId: cardId, variables: variables)

    let newVariables: DivVariables = [
      "string_var": .string("new value"),
      "int_var": .integer(200),
    ]
    storage.set(cardId: cardId, variables: newVariables)

    XCTAssertEqual(newVariables, makeVariables())
  }

  func test_append_ReplaceExistingVariables() {
    storage.set(cardId: cardId, variables: [
      "string_var": .string("old_value"),
      "int_var": .integer(777),
    ])

    let newVariables: DivVariables = [
      "string_var": .string("new_value"),
      "bool_var": .bool(true),
    ]
    storage.append(variables: newVariables, for: cardId, replaceExisting: true)

    let expectedVariables: DivVariables = [
      "string_var": .string("new_value"),
      "int_var": .integer(777),
      "bool_var": .bool(true),
    ]
    XCTAssertEqual(expectedVariables, makeVariables())
  }

  func test_append_NotReplaceExistingVariables() {
    storage.set(cardId: cardId, variables: [
      "string_var": .string("old_value"),
      "int_var": .integer(777),
    ])

    let newVariables: DivVariables = [
      "string_var": .string("new_value"),
      "bool_var": .bool(true),
    ]
    storage.append(variables: newVariables, for: cardId, replaceExisting: false)

    let expectedVariables: DivVariables = [
      "string_var": .string("old_value"),
      "bool_var": .bool(true),
      "int_var": .integer(777),
    ]
    XCTAssertEqual(expectedVariables, makeVariables())
  }

  func test_LocalVaraibleShadowsGlobalVariable_LocalsSetAfterGlobals() {
    let globalVariables: DivVariables = [
      "string_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)
    storage.set(cardId: cardId, variables: variables)

    XCTAssertEqual(.string("value"), getVariable("string_var"))
  }

  func test_LocalVaraibleShadowsGlobalVariable_LocalsSetBeforeGlobals() {
    storage.set(cardId: cardId, variables: variables)

    let globalVariables: DivVariables = [
      "string_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    XCTAssertEqual(.string("value"), getVariable("string_var"))
  }

  func test_update_UpdatesStringVariable() {
    storage.set(cardId: cardId, variables: variables)
    storage.update(cardId: cardId, name: "string_var", value: "new value")

    XCTAssertEqual(.string("new value"), getVariable("string_var"))
  }

  func test_update_UpdatesNumberVariable() {
    storage.set(cardId: cardId, variables: variables)
    storage.update(cardId: cardId, name: "number_var", value: "234.567")

    XCTAssertEqual(.number(234.567), getVariable("number_var"))
  }

  func test_update_UpdatesBoolVariable_BoolValue() {
    storage.set(cardId: cardId, variables: ["bool_var": .bool(false)])
    storage.update(cardId: cardId, name: "bool_var", value: "true")

    XCTAssertEqual(.bool(true), getVariable("bool_var"))
  }

  func test_update_UpdatesBoolVariable_MixedCase() {
    storage.set(cardId: cardId, variables: ["bool_var": .bool(false)])
    storage.update(cardId: cardId, name: "bool_var", value: "tRuE")

    XCTAssertEqual(.bool(true), getVariable("bool_var"))
  }

  func test_update_UpdatesBoolVariable_IntValue() {
    storage.set(cardId: cardId, variables: ["bool_var": .bool(false)])
    storage.update(cardId: cardId, name: "bool_var", value: "1")

    XCTAssertEqual(.bool(true), getVariable("bool_var"))
  }

  func test_update_DoesNothing_ForUnknownVariable() {
    storage.update(cardId: cardId, name: "string_var", value: "new value")

    XCTAssertEqual([:], makeVariables())
  }

  func test_update_DoesNothing_ForInvalidValue() {
    storage.set(cardId: cardId, variables: variables)
    storage.update(cardId: cardId, name: "int_var", value: "not a number")

    XCTAssertEqual(variables, makeVariables())
  }

  func test_update_SendsUpdateEvent_LocalVariable() {
    storage.set(cardId: cardId, variables: variables)
    storage.update(cardId: cardId, name: "string_var", value: "new value")

    XCTAssertEqual(.local(cardId, ["string_var"]), event?.kind)

    let newValues = event?.newValues
    XCTAssertEqual(makeVariables(), (newValues?.global ?? [:]) + (newValues?.local[cardId] ?? [:]))
  }

  func test_update_SendsUpdateEvent_GlobalVariable() {
    storage.set(
      variables: ["global_var": .string("global value")],
      triggerUpdate: false
    )

    storage.update(cardId: cardId, name: "global_var", value: "new value")

    XCTAssertEqual(.global(["global_var"]), event?.kind)

    let newValues = event?.newValues
    XCTAssertEqual(makeVariables(), (newValues?.global ?? [:]) + (newValues?.local[cardId] ?? [:]))
  }

  func test_set_SendsUpdateEvent() {
    storage.set(cardId: cardId, variables: variables)
    XCTAssertEqual(.local(cardId, Set(variables.keys)), event?.kind)
  }

  func test_update_DoesNotSendUpdateEventForSameValue() {
    storage.set(cardId: cardId, variables: variables)
    event = nil
    storage.update(cardId: cardId, name: "string_var", value: "value")

    XCTAssertNil(event)
  }

  func test_update_UpdatesGlobalVariable() {
    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    storage.update(cardId: cardId, name: "global_var", value: "new value")

    XCTAssertEqual(.string("new value"), getVariable("global_var"))
  }

  func test_update_DoesNotUpdateShadowedGlobalVariable() {
    let globalVariables: DivVariables = [
      "string_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)
    storage.set(cardId: cardId, variables: variables)

    storage.update(cardId: cardId, name: "string_var", value: "new value")

    XCTAssertEqual(.string("global value"), makeVariables(cardId: "unknown")["string_var"])
  }

  func test_update_UpdatesShadowedInAnotherCardGlobalVariable() {
    let globalVariables: DivVariables = [
      "string_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)
    storage.set(cardId: cardId, variables: variables)

    storage.update(cardId: "another_card", name: "string_var", value: "new value")

    XCTAssertEqual(.string("new value"), makeVariables(cardId: "another_card")["string_var"])
  }

  func test_update_UpdatesLocalVariable() {
    let path = cardId.path + "element_id"
    storage.initializeIfNeeded(path: path, variables: variables)

    storage.update(path: path, name: "string_var", value: "new value")

    XCTAssertEqual(
      "new value",
      storage.getVariableValue(path: path, name: "string_var")
    )
  }

  func test_update_UpdatesParentLocalVariable() {
    let parentPath = cardId.path + "parent_id"
    storage.initializeIfNeeded(path: parentPath, variables: variables)

    storage.update(path: parentPath + "element_id", name: "string_var", value: "new value")

    XCTAssertEqual(
      "new value",
      storage.getVariableValue(path: parentPath, name: "string_var")
    )
  }

  func test_update_UpdatesParentLocalVariable_FromInnerScope_SendsUpdateEvent() {
    let parentPath = cardId.path + "parent_id"
    storage.initializeIfNeeded(path: parentPath, variables: variables)
    storage.initializeIfNeeded(
      path: parentPath + "element_id",
      variables: ["another_var": .integer(2)]
    )

    storage.update(path: parentPath + "element_id", name: "string_var", value: "new value")

    XCTAssertEqual(.local(cardId, ["string_var"]), event?.kind)
  }

  func test_setGlobalVariables_SendsUpdateEvent() {
    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: true)

    XCTAssertEqual(.global(["global_var"]), event?.kind)
    XCTAssertEqual(globalVariables, event?.newValues.global)
  }

  func test_setGlobalVariables_SendsUpdateEvent_ForUpdatedVariablesOnly() {
    storage.set(variables: variables, triggerUpdate: false)

    var newVariables = variables
    newVariables["string_var"] = .string("new value")
    newVariables["int_var"] = .integer(200)
    storage.set(variables: newVariables, triggerUpdate: true)

    XCTAssertEqual(.global(["string_var", "int_var"]), event?.kind)
    XCTAssertEqual(newVariables, event?.newValues.global)
  }

  func test_setGlobalVariables_DoesNotSendUpdateEvent_WhenTriggerUpdateIsFalse() {
    storage.set(variables: variables, triggerUpdate: false)

    XCTAssertNil(event)
  }

  func test_append_AppendsGlobalVariables() {
    storage.set(variables: variables, triggerUpdate: false)

    let newVariables: DivVariables = [
      "int_var": .integer(0),
      "new_var": .string("arbitrary"),
    ]
    storage.append(variables: newVariables, triggerUpdate: true)

    let expectedVariables = variables + newVariables
    XCTAssertEqual(expectedVariables, makeVariables())
    XCTAssertEqual(.global(["int_var", "new_var"]), event?.kind)
  }

  func test_getVariableValue_ReturnsCardVariableValue() {
    storage.set(cardId: cardId, variables: variables)

    let expectedValue: String? = storage.getVariableValue(cardId: cardId, name: "string_var")
    XCTAssertEqual("value", expectedValue)
  }

  func test_getVariableValue_ReturnsGlobalVariableValue() {
    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    let expectedValue: String? = storage.getVariableValue(cardId: cardId, name: "global_var")
    XCTAssertEqual("global value", expectedValue)
  }

  func test_getVariableValue_ReturnsCardVariableValue_WhenHasGlobalVariable() {
    storage.set(cardId: cardId, variables: variables)

    let globalVariables: DivVariables = [
      "string_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    let expectedValue: String? = storage.getVariableValue(cardId: cardId, name: "string_var")
    XCTAssertEqual("value", expectedValue)
  }

  func test_getVariableValue_ReturnsNilForUnknownVariable() {
    storage.set(cardId: cardId, variables: variables)

    XCTAssertNil(storage.getVariableValue(cardId: cardId, name: "unknown_var"))
  }

  func test_getVariableValue_ReturnsLocalVariableValue() {
    let path = UIElementPath("card_id") + "element_id"
    storage.initializeIfNeeded(path: path, variables: variables)

    XCTAssertEqual(
      "value",
      storage.getVariableValue(path: path, name: "string_var")
    )
  }

  func test_getVariableValue_ReturnsShadowedLocalVariableValue() {
    storage.set(cardId: "card_id", variables: variables)

    let path = UIElementPath("card_id") + "element_id"
    storage.initializeIfNeeded(path: path, variables: ["string_var": .string("local value")])

    XCTAssertEqual(
      "local value",
      storage.getVariableValue(path: path, name: "string_var")
    )
  }

  func test_getVariableValue_ReturnsParentVariableValueIfNoLocalVariables() {
    let parentPath = UIElementPath("card_id") + "parent_id"
    storage.initializeIfNeeded(path: parentPath, variables: variables)

    XCTAssertEqual(
      "value",
      storage.getVariableValue(path: parentPath + "element_id", name: "string_var")
    )
  }

  func test_getVariableValue_ReturnsParentVariableValueIfHasAnotherLocalVariables() {
    let parentPath = UIElementPath("card_id") + "parent_id"
    storage.initializeIfNeeded(path: parentPath, variables: variables)
    storage.initializeIfNeeded(
      path: parentPath + "element_id",
      variables: ["local_var": .string("local value")]
    )

    XCTAssertEqual(
      "value",
      storage.getVariableValue(path: parentPath + "element_id", name: "string_var")
    )
  }

  func test_hasValue_ReturnsTrueIfContainsLocalVariable() {
    storage.set(cardId: cardId, variables: variables)

    XCTAssertTrue(storage.hasValue(cardId: cardId, name: "string_var"))
  }

  func test_hasValue_ReturnsTrueIfContainsGlobalVariable() {
    storage.set(
      variables: ["global_var": .string("global value")],
      triggerUpdate: false
    )

    XCTAssertTrue(storage.hasValue(cardId: cardId, name: "global_var"))
  }

  func test_hasValue_ReturnsFalseIfVariableIsNotAvailable() {
    storage.set(cardId: cardId, variables: variables)

    XCTAssertFalse(storage.hasValue(cardId: cardId, name: "unknown_var"))
  }

  func test_reset_ResetsVariables() {
    storage.set(cardId: cardId, variables: variables)

    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    storage.reset()

    XCTAssertTrue(storage.makeVariables(for: cardId).isEmpty)
  }

  func test_reset_ResetsVariablesByCardId() {
    storage.set(cardId: cardId, variables: variables)

    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    storage.reset(cardId: cardId)

    XCTAssertEqual(globalVariables, storage.makeVariables(for: cardId))
  }

  func test_reset_ResetsLocalVariablesByCardId() {
    storage.initializeIfNeeded(path: cardId.path + "element_id", variables: variables)

    let globalVariables: DivVariables = [
      "global_var": .string("global value"),
    ]
    storage.set(variables: globalVariables, triggerUpdate: false)

    storage.reset(cardId: cardId)

    XCTAssertEqual(globalVariables, storage.makeVariables(for: cardId))
  }

  private func makeVariables(cardId: DivCardID = cardId) -> DivVariables {
    storage.makeVariables(for: cardId)
  }

  private func getVariable(_ name: DivVariableName) -> DivVariableValue? {
    storage.makeVariables(for: cardId)[name]
  }
}

private let cardId = DivCardID(rawValue: "test_card")

private let variables: DivVariables = [
  "string_var": .string("value"),
  "int_var": .integer(100),
  "number_var": .number(123.34),
  "bool_var": .bool(true),
  "url_var": .url(url("https://test.url")),
]
