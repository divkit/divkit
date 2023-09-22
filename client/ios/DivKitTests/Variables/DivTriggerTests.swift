@testable import DivKit

import XCTest

final class DivTriggerTests: XCTestCase {
  private let variablesStorage = DivVariablesStorage()

  private lazy var actionHandler = DivActionHandler(
    stateUpdater: FakeDivStateUpdater(),
    patchProvider: FakeDivPatchDownloader(),
    variablesStorage: variablesStorage,
    updateCard: { _ in },
    showTooltip: { _ in },
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
    try XCTSkipIf(
      true,
      "Test is broken. Numeric/Bool properties does not support nested expressions."
    )

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{'@{var}' == 'OK'}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("var", "OK")

    XCTAssertEqual(triggersCount, 1)
  }

  private func setVariable(_ name: DivVariableName, _ value: Bool) {
    variablesStorage.set(variables: [name: .bool(value)], triggerUpdate: true)
  }

  private func setVariable(_ name: DivVariableName, _ value: String) {
    variablesStorage.set(variables: [name: .string(value)], triggerUpdate: true)
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

private let action = DivAction(logId: "1", url: .value(URL(string: "action://host")!))

private func expression(_ expression: String) -> Expression<Bool> {
  .link(try! ExpressionLink(rawValue: expression)!)
}
