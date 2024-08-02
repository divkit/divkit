@testable import DivKit

import XCTest
import LayoutKit

final class DivTriggerTests: XCTestCase {
  private let variablesStorage = DivVariablesStorage()

  private lazy var actionHandler = DivActionHandler(
    stateUpdater: FakeDivStateUpdater(),
    patchProvider: FakeDivPatchDownloader(),
    variablesStorage: variablesStorage,
    updateCard: { _ in },
    urlHandler: DivUrlHandlerDelegate { [unowned self] _, _ in
      self.triggersCount += 1
    },
    persistentValuesStorage: DivPersistentValuesStorage()
  )

  private lazy var triggerStorage = DivTriggersStorage(
    variablesStorage: variablesStorage,
    actionHandler: actionHandler,
    persistentValuesStorage: DivPersistentValuesStorage()
  )

  private var triggersCount = 0

  func test_set_DoesNoTrigger_WhenConditionHasNoVariables() throws {
    let trigger = DivTrigger(
      actions: [action],
      condition: .value(true),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    XCTAssertEqual(triggersCount, 0)
  }

  func test_set_DoesNotTrigger_WhenConditionIsFalse() {
    setVariable("should_trigger", false)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    XCTAssertEqual(triggersCount, 0)
  }

  func test_set_Triggers_WhenConditionIsTrue_OnConditionMode() {
    setVariable("should_trigger", true)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    XCTAssertEqual(triggersCount, 1)
  }

  func test_set_Triggers_WhenConditionIsTrue_OnVariableMode() {
    setVariable("should_trigger", true)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onVariable)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    XCTAssertEqual(triggersCount, 1)
  }

  func test_Triggers_WhenVariableIsSet() {
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("should_trigger", true)

    XCTAssertEqual(triggersCount, 1)
  }

  func test_Triggers_WhenVariableIsChanged() {
    setVariable("should_trigger", false)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("should_trigger", true)

    XCTAssertEqual(triggersCount, 1)
  }

  func test_DoesNotTrigger_WhenUnusedVariableIsChanged() {
    setVariable("should_trigger", true)
    setVariable("unused_variable", false)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onVariable)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    XCTAssertEqual(triggersCount, 1)

    setVariable("unused_variable", true)

    XCTAssertEqual(triggersCount, 1)
  }

  func test_TriggersMultipleTimes_WhenConditionsIsChangedMultipleTimes() {
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("should_trigger", true)
    setVariable("should_trigger", false)
    setVariable("should_trigger", true)

    XCTAssertEqual(triggersCount, 2)
  }

  func test_TriggersOnce_WhenVariableIsChangedMultipleTimes_OnConditionMode() {
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{var != 'Hello'}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("var", "H")
    setVariable("var", "He")
    setVariable("var", "Hel")

    XCTAssertEqual(triggersCount, 1)
  }

  func test_TriggersMultipleTimes_WhenVariableIsChangedMultipleTimes_OnVariableMode() {
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{var != 'Hello'}"),
      mode: .value(.onVariable)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("var", "H")
    setVariable("var", "He")
    setVariable("var", "Hel")

    XCTAssertEqual(triggersCount, 3)
  }

  func test_Triggers_WhenVariableInNestedExpressionIsChanged() throws {
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{'@{var}' == 'OK'}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("var", "OK")

    XCTAssertEqual(triggersCount, 1)
  }

  func test_DoesNotTrigger_WhenLocalVariableIsChanged() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let parentPath = UIElementPath("card_id")
    let childPath = parentPath + "element_id"

    variablesStorage.initializeIfNeeded(path: childPath, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: parentPath, triggers: [trigger])

    variablesStorage.update(path: childPath, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  func test_DoesNotLocalTrigger_WhenParentAndChildVariablesWithSameNameAndParentVariableIsChanged() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let parentPath = UIElementPath("card_id")
    let childPath = parentPath + "element_id"

    variablesStorage.initializeIfNeeded(path: parentPath, variables: variables)
    variablesStorage.initializeIfNeeded(path: childPath, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: childPath, triggers: [trigger])

    variablesStorage.update(path: parentPath, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  func test_LocalTriggers_WhenLocalVariableIsChanged() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let path = UIElementPath("card_id") + "element_id"

    variablesStorage.initializeIfNeeded(path: path, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: path, triggers: [trigger])

    variablesStorage.update(path: path, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 1)
  }

  func test_LocalTriggers_WhenVariableInParentScopeIsChanged() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let cardID = DivCardID(rawValue: "card_id")
    let path = UIElementPath(cardID.rawValue) + "element_id"

    variablesStorage.set(cardId: cardID, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: path, triggers: [trigger])

    variablesStorage.update(path: UIElementPath(cardID.rawValue), name: "should_trigger", value: "1")

    XCTAssertEqual(triggersCount, 1)
  }

  func test_LocalTriggers_WhenHaveParentAndChildVariablesAndParentVariableIsChanged() throws {
    let parentVariables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let childVariables: DivVariables = [
      "other_var": .bool(false),
    ]
    let parentPath = UIElementPath("card_id")
    let childPath = parentPath + "child_id"

    variablesStorage.initializeIfNeeded(path: parentPath, variables: parentVariables)
    variablesStorage.initializeIfNeeded(path: childPath, variables: childVariables)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: childPath, triggers: [trigger])

    variablesStorage.update(path: parentPath, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 1)
  }

  func test_DoesNotLocalTrigger_WhenTriggerIsInactive() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let path = UIElementPath("card_id") + "element_id"

    variablesStorage.initializeIfNeeded(path: path, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: path, triggers: [trigger])
    triggerStorage.disableTriggers(path: path)

    variablesStorage.update(path: path, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  private func setVariable(_ name: DivVariableName, _ value: Bool) {
    variablesStorage.append(variables: [name: .bool(value)], triggerUpdate: true)
  }

  private func setVariable(_ name: DivVariableName, _ value: String) {
    variablesStorage.append(variables: [name: .string(value)], triggerUpdate: true)
  }
}

private class FakeDivStateUpdater: DivStateUpdater {
  func set(
    path _: DivStatePath,
    cardId _: DivCardID,
    lifetime _: DivStateLifetime
  ) {}
}

private class FakeDivPatchDownloader: DivPatchProvider {
  func getPatch(
    url _: URL,
    completion _: @escaping DivPatchProviderCompletion
  ) {}

  func cancelRequests() {}
}

private let action = divAction(logId: "1", url: "action://host")
