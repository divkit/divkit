@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import UIKit
import XCTest

final class DivTriggerTests: XCTestCase {
  private var flags: DivFlagsInfo = .default
  private let variablesStorage = DivVariablesStorage()
  private let persistentValuesStorage = DivPersistentValuesStorage()

  private lazy var actionHandler = DivActionHandler(
    persistentValuesStorage: persistentValuesStorage,
    urlHandler: DivUrlHandlerDelegate { [unowned self] url, _ in
      self.triggersCount += 1
      self.firedUrls.append(url.absoluteString)
    },
    variablesStorage: variablesStorage
  )

  private lazy var blockStateStorage = DivBlockStateStorage()

  private lazy var triggerStorage = DivTriggersStorage(
    variablesStorage: variablesStorage,
    functionsStorage: DivFunctionsStorage(),
    blockStateStorage: blockStateStorage,
    actionHandler: actionHandler,
    persistentValuesStorage: persistentValuesStorage,
    flagsInfo: flags
  )

  private var triggersCount = 0
  private var firedUrls = [String]()

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

  func test_set_Triggers_WhenConditionIsTrue_OnConditionMode_WithSeparateTriggerSetAndInit() {
    flags = DivFlagsInfo(initializeTriggerOnSet: false)

    setVariable("should_trigger", true)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    XCTAssertEqual(triggersCount, 0)

    triggerStorage.initialize(cardId: "id")

    XCTAssertEqual(triggersCount, 1)
  }

  func test_set_Triggers_WhenConditionIsTrue_OnConditionMode_WithModifiedVariableAndSeparateTriggerSetAndInit(
  ) {
    flags = DivFlagsInfo(initializeTriggerOnSet: false)

    setVariable("should_trigger", true)

    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.set(cardId: "id", triggers: [trigger])

    setVariable("should_trigger", false)
    setVariable("should_trigger", true)

    XCTAssertEqual(triggersCount, 0)

    triggerStorage.initialize(cardId: "id")

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

  func test_Triggers_WhenVariableAppendForCardIdInStorage() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let path = UIElementPath("card_id")

    variablesStorage.initializeIfNeeded(path: path, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)

    variablesStorage.append(variables: ["should_trigger": .bool(true)], for: "card_id")
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
    triggerStorage.setIfNeeded(path: parentPath, triggers: [trigger], viewId: hostViewId)

    variablesStorage.update(path: childPath, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  func test_DoesNotLocalTrigger_WhenParentAndChildVariablesWithSameNameAndParentVariableIsChanged(
  ) throws {
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
    triggerStorage.setIfNeeded(path: childPath, triggers: [trigger], viewId: hostViewId)

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
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)

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
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)

    variablesStorage.update(
      path: UIElementPath(cardID.rawValue),
      name: "should_trigger",
      value: "1"
    )

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
    triggerStorage.setIfNeeded(path: childPath, triggers: [trigger], viewId: hostViewId)

    variablesStorage.update(path: parentPath, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 1)
  }

  func test_LocalTriggers_WhenLocalVariableIsChanged_BeforeInitialization() throws {
    flags = DivFlagsInfo(initializeTriggerOnSet: false)

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
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)

    variablesStorage.update(path: path, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)

    triggerStorage.initialize(cardId: path.cardId)

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
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)
    triggerStorage.disableTriggers(path: path)

    variablesStorage.update(path: path, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  func test_DoesNotLocalTrigger_WhenElementTriggersReset() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let elementId = "element_id"
    let path = UIElementPath("card_id") + elementId

    variablesStorage.initializeIfNeeded(path: path, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)
    triggerStorage.reset(elementId: elementId)

    variablesStorage.update(path: path, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  func test_DoesNotLocalTrigger_WhenParentTriggersReset() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let elementId = "element_id"
    let state1 = "state_1"
    let state2 = "state_2"
    let pathState1 = UIElementPath("card_id") + elementId + state1
    let pathState2 = UIElementPath("card_id") + elementId + state2

    variablesStorage.initializeIfNeeded(path: pathState1, variables: variables)
    variablesStorage.initializeIfNeeded(path: pathState2, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: pathState1, triggers: [trigger], viewId: hostViewId)
    triggerStorage.setIfNeeded(path: pathState2, triggers: [trigger], viewId: hostViewId)
    triggerStorage.reset(elementId: elementId)

    variablesStorage.update(path: pathState1, name: "should_trigger", value: .bool(true))
    variablesStorage.update(path: pathState2, name: "should_trigger", value: .bool(true))

    XCTAssertEqual(triggersCount, 0)
  }

  func test_DeferredTabsScroll_ActivatesOnlyDestinationTabTrigger() throws {
    let tabsPath = UIElementPath("card_id") + "tabs_id"
    registerTabTrigger(at: tabsPath + "0", url: "https://example.com/initial")
    registerTabTrigger(at: tabsPath + "1", url: "https://example.com/destination")
    blockStateStorage.setState(
      path: tabsPath, state: TabViewState(selectedPageIndex: 0, countOfPages: 5)
    )
    let (view, model) = try makeScrollTestTabs(
      path: tabsPath, clock: TestScrollAnimationClock(), observer: blockStateStorage, frame: .zero
    )
    view.configure(
      model: model,
      state: TabViewState(
        selectedPageIndex: 0, countOfPages: 5, animated: false,
        scrollRequest: ScrollRequest(delta: .items(1, wraps: false))
      ),
      observer: blockStateStorage, overscrollDelegate: nil, renderingDelegate: nil
    )
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    view.layoutIfNeeded()

    variablesStorage.update(path: tabsPath + "0", name: "should_trigger", value: .bool(true))
    variablesStorage.update(path: tabsPath + "1", name: "should_trigger", value: .bool(true))

    XCTAssertEqual(firedUrls, ["https://example.com/destination"])
  }

  private func registerTabTrigger(at path: UIElementPath, url: String) {
    variablesStorage.initializeIfNeeded(path: path, variables: ["should_trigger": .bool(false)])
    triggerStorage.setIfNeeded(path: path, triggers: [DivTrigger(
      actions: [DivAction(logId: .value("tab_trigger"), url: .value(URL(string: url)!))],
      condition: expression("@{should_trigger}"), mode: .value(.onCondition)
    )], viewId: hostViewId)
  }

  func test_TimedTabsScroll_DoesNotActivateIntermediateTabTrigger() throws {
    let tabsPath = UIElementPath("card_id") + "tabs_id"
    let tabPath = tabsPath + "1"
    variablesStorage.initializeIfNeeded(path: tabPath, variables: ["should_trigger": .bool(false)])
    triggerStorage.setIfNeeded(path: tabPath, triggers: [DivTrigger(
      actions: [action], condition: expression("@{should_trigger}"), mode: .value(.onCondition)
    )], viewId: hostViewId)
    blockStateStorage.setState(
      path: tabsPath,
      state: TabViewState(selectedPageIndex: 0, countOfPages: 5)
    )
    variablesStorage.update(path: tabPath, name: "should_trigger", value: .bool(true))
    let clock = TestScrollAnimationClock()
    let (view, model) = try makeScrollTestTabs(
      path: tabsPath, clock: clock, observer: blockStateStorage
    )

    view.configure(
      model: model,
      state: TabViewState(
        selectedPageIndex: 4, countOfPages: 5, scrollRequest: ScrollRequest(duration: 2)
      ),
      observer: blockStateStorage,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    clock.advance(to: 0.75)
    clock.advance(to: 1.25)
    clock.advance(to: 2)

    XCTAssertEqual(triggersCount, 0)
    let state: TabViewState? = blockStateStorage.getState(tabsPath)
    XCTAssertEqual(state?.selectedPageIndex, 4)
  }

  func test_DoesLocalTrigger_WhenTabIsActive() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let tabsPath = UIElementPath("card_id") + "tabs_id"
    let tabPath = tabsPath + "0"

    variablesStorage.initializeIfNeeded(path: tabPath, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: tabPath, triggers: [trigger], viewId: hostViewId)
    blockStateStorage.setState(
      path: tabsPath,
      state: TabViewState(selectedPageIndex: 0, countOfPages: 2)
    )

    variablesStorage.update(path: tabPath, name: "should_trigger", value: .bool(true))
    XCTAssertEqual(triggersCount, 1)
  }

  func test_DoesNotLocalTrigger_WhenTabIsInactive() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let tabsPath = UIElementPath("card_id") + "tabs_id"
    let tabPath = tabsPath + "0"

    variablesStorage.initializeIfNeeded(path: tabPath, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: tabPath, triggers: [trigger], viewId: hostViewId)
    blockStateStorage.setState(
      path: tabsPath,
      state: TabViewState(selectedPageIndex: 1, countOfPages: 2)
    )

    variablesStorage.update(path: tabPath, name: "should_trigger", value: .bool(true))
    XCTAssertEqual(triggersCount, 0)
  }

  func test_DoesNotLocalTrigger_WhenTabIsInactiveAndTriggersInChildElement() throws {
    let variables: DivVariables = [
      "should_trigger": .bool(false),
    ]
    let tabsPath = UIElementPath("card_id") + "tabs_id"
    let path = tabsPath + "0" + "5"

    variablesStorage.initializeIfNeeded(path: path, variables: variables)
    let trigger = DivTrigger(
      actions: [action],
      condition: expression("@{should_trigger}"),
      mode: .value(.onCondition)
    )
    triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: hostViewId)
    blockStateStorage.setState(
      path: tabsPath,
      state: TabViewState(selectedPageIndex: 1, countOfPages: 2)
    )

    variablesStorage.update(path: path, name: "should_trigger", value: .bool(true))
    XCTAssertEqual(triggersCount, 0)
  }

  func test_resetViewId_host_leavesTooltipAndOtherCardTriggers() {
    registerHostTooltipAndOtherCardTriggers()

    triggerStorage.reset(viewId: hostViewId)
    fireAllTriggers()

    XCTAssertEqual(Set(firedUrls), ["action://tooltip", "action://other"])
  }

  func test_resetViewId_tooltip_leavesHostAndOtherCardTriggers() {
    registerHostTooltipAndOtherCardTriggers()

    triggerStorage.reset(viewId: tooltipViewId)
    fireAllTriggers()

    XCTAssertEqual(Set(firedUrls), ["action://host", "action://other"])
  }

  func test_resetCardId_dropsHostAndTooltipTriggers_leavesOtherCard() {
    registerHostTooltipAndOtherCardTriggers()

    triggerStorage.reset(cardId: "card_id")
    fireAllTriggers()

    XCTAssertEqual(firedUrls, ["action://other"])
  }

  func test_set_ReplacesHostTriggers_KeepsOpenTooltipTriggers() {
    registerHostTooltipAndOtherCardTriggers()

    // A new DivData of the host: its triggers are replaced, the open tooltip's keep running.
    triggerStorage.set(cardId: "card_id", triggers: [])
    fireAllTriggers()

    XCTAssertEqual(Set(firedUrls), ["action://tooltip", "action://other"])
  }

  private func registerHostTooltipAndOtherCardTriggers() {
    let hostPath = UIElementPath("card_id")
    let tooltipPath = tooltipViewId.path
    let otherPath = UIElementPath("other_card")
    for path in [hostPath, otherPath] {
      variablesStorage.initializeIfNeeded(path: path, variables: ["should_trigger": .bool(false)])
    }
    for (path, viewId, url) in [
      (hostPath, hostViewId, "action://host"),
      (tooltipPath, tooltipViewId, "action://tooltip"),
      (otherPath, otherViewId, "action://other"),
    ] {
      let trigger = DivTrigger(
        actions: [divAction(logId: url, url: url)],
        condition: expression("@{should_trigger}"),
        mode: .value(.onCondition)
      )
      triggerStorage.setIfNeeded(path: path, triggers: [trigger], viewId: viewId)
    }
  }

  private func fireAllTriggers() {
    variablesStorage.append(variables: ["should_trigger": .bool(true)], for: "card_id")
    variablesStorage.append(variables: ["should_trigger": .bool(true)], for: "other_card")
  }

  private func setVariable(_ name: DivVariableName, _ value: Bool) {
    variablesStorage.append(variables: [name: .bool(value)], triggerUpdate: true)
  }

  private func setVariable(_ name: DivVariableName, _ value: String) {
    variablesStorage.append(variables: [name: .string(value)], triggerUpdate: true)
  }
}

private let action = divAction(logId: "1", url: "action://host")
private let hostViewId = DivViewId(cardId: "card_id")
private let otherViewId = DivViewId(cardId: "other_card")
private let tooltipViewId = DivViewId(
  cardId: "card_id",
  tooltip: DivViewId.Tooltip(id: "hint", anchorPath: UIElementPath("card_id") + "anchor")
)
