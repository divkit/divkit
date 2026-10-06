@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import Foundation
import LayoutKit
import Testing
import VGSL

#if os(iOS)
@Suite
@MainActor
struct TooltipViewLifecycleTests {
  private let cardId: DivCardID = "card"
  private let components = DivKitComponents()

  private var firedValue: String? {
    components.variablesStorage.getVariableValue(cardId: cardId, name: "fired")
  }

  private var cancelCount: Int? {
    components.variablesStorage.getVariableValue(cardId: cardId, name: "cancel_count")
  }

  private var resultValue: Int? {
    components.variablesStorage.getVariableValue(cardId: cardId, name: "result")
  }

  @Test
  func close_withoutStartingAnimator_doesNotRunCancelActions() async {
    components.variablesStorage.set(
      cardId: cardId,
      variables: [
        "animation_value": .number(0),
        "cancel_count": .integer(0),
      ]
    )
    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: tooltipContentWithCancelAction()
    )

    view.tooltipDidClose()

    #expect(cancelCount == 0)
    withExtendedLifetime(view) {}
  }

  @Test
  func close_whileAnimatorIsRunning_runsCancelActionsOnce() async {
    components.variablesStorage.set(
      cardId: cardId,
      variables: [
        "animation_value": .number(0),
        "cancel_count": .integer(0),
      ]
    )
    let tooltip = tooltipId(anchor: "item")
    let view = await makeTooltipView(
      tooltip: tooltip,
      content: tooltipContentWithCancelAction()
    )
    startTooltipAnimator(tooltip: tooltip)

    view.tooltipDidClose()

    #expect(cancelCount == 1)
    withExtendedLifetime(view) {}
  }

  @Test
  func close_removesIdsTriggersAndAnimatorsWhileViewIsAlive() async {
    components.variablesStorage.set(
      cardId: cardId,
      variables: [
        "animation_value": .number(0),
        "counter": .integer(0),
        "fired": .string("no"),
      ]
    )
    let content = divContainer(
      id: "tooltip_content",
      animators: [
        .divNumberAnimator(DivNumberAnimator(
          duration: .value(100),
          endValue: .value(1),
          id: "tooltip_animator",
          variableName: "animation_value"
        )),
      ],
      variableTriggers: [makeTrigger()]
    )
    let tooltip = tooltipId(anchor: "item")
    let view = await makeTooltipView(tooltip: tooltip, content: content)

    setCounter(1)
    components.flushUpdateActions()
    #expect(firedValue == "yes")
    #expect(paths(forId: "tooltip_content").count == 1)
    #expect(animatorDefinition(id: "tooltip_animator", viewId: tooltip) != nil)

    view.tooltipDidClose()

    components.variablesStorage.update(cardId: cardId, name: "fired", value: "no")
    setCounter(2)
    components.flushUpdateActions()
    #expect(paths(forId: "tooltip_content").isEmpty)
    #expect(firedValue == "no")
    #expect(animatorDefinition(id: "tooltip_animator", viewId: tooltip) == nil)
    withExtendedLifetime(view) {}
  }

  @Test
  func cardUpdate_afterClose_doesNotRestoreRegistrations() async {
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["counter": .integer(0), "fired": .string("no")]
    )
    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: divContainer(id: "tooltip_content", variableTriggers: [makeTrigger()])
    )
    view.tooltipDidClose()

    setCounter(1)
    components.flushUpdateActions()

    #expect(paths(forId: "tooltip_content").isEmpty)
    #expect(firedValue == "no")
    withExtendedLifetime(view) {}
  }

  @Test
  func closingOneAnchor_keepsOnlyOtherAnchorsRegistrations() async {
    let first = await makeTooltipView(
      tooltip: tooltipId(anchor: "item_a"),
      content: divSeparator(id: "tooltip_content")
    )
    let second = await makeTooltipView(
      tooltip: tooltipId(anchor: "item_b"),
      content: divSeparator(id: "tooltip_content")
    )
    #expect(paths(forId: "tooltip_content").count == 2)

    first.tooltipDidClose()

    #expect(paths(forId: "tooltip_content").count == 1)
    second.tooltipDidClose()
  }

  @Test
  func closedTooltip_doesNotMakeHostIdAmbiguous() async {
    _ = makeBlock(
      divSeparator(id: "shared"),
      context: components.makeContext(cardId: cardId, cachedImageHolders: [])
    )
    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: divSeparator(id: "shared")
    )
    #expect(paths(forId: "shared").count == 2)

    view.tooltipDidClose()

    #expect(paths(forId: "shared").count == 1)
  }

  @Test
  func twoOpenViewsWithSameViewId_areRemovedAfterBothClose() async {
    let tooltip = tooltipId(anchor: "item")
    let first = await makeTooltipView(
      tooltip: tooltip,
      content: divSeparator(id: "tooltip_content")
    )
    let second = await makeTooltipView(
      tooltip: tooltip,
      content: divSeparator(id: "tooltip_content")
    )

    first.tooltipDidClose()
    #expect(paths(forId: "tooltip_content").count == 1)

    second.tooltipDidClose()
    #expect(paths(forId: "tooltip_content").isEmpty)
  }

  @Test
  func deinitWithoutClose_usesRegistryFallback() async {
    var view: DivView? = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: divSeparator(id: "tooltip_content")
    )
    #expect(paths(forId: "tooltip_content").count == 1)

    view = nil

    #expect(await waitUntil { paths(forId: "tooltip_content").isEmpty })
    #expect(view == nil)
  }

  @Test
  func stateIsPreservedBetweenShows() async {
    let content = stateContent()
    let tooltip = tooltipId(anchor: "item")
    let first = await makeTooltipView(tooltip: tooltip, content: content)
    let statePath = tooltipStatePath()
    let stateManager = components.stateManagement.getStateManagerForCard(cardId: cardId)
    stateManager.setState(stateBlockPath: statePath, stateID: "selected")
    first.tooltipDidClose()

    let second = await makeTooltipView(tooltip: tooltip, content: content)

    #expect(stateManager.items[statePath]?.currentStateID == "selected")
    #expect(paths(forId: "selected").count == 1)
    second.tooltipDidClose()
  }

  @Test
  func stateSetBeforeShow_isUsedByTooltip() async {
    let stateManager = components.stateManagement.getStateManagerForCard(cardId: cardId)
    stateManager.setState(stateBlockPath: tooltipStatePath(), stateID: "selected")

    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: stateContent()
    )

    #expect(paths(forId: "selected").count == 1)
    view.tooltipDidClose()
  }

  @Test
  func showTooltipThenSetStateInSameBatch_opensSelectedState() async throws {
    let createdView = CreatedTooltipView()
    let tooltip = tooltipId(anchor: "item")
    let performer = MockTooltipActionPerformer { _ in
      Task { @MainActor in
        createdView.view = await makeTooltipView(tooltip: tooltip, content: stateContent())
      }
    }
    let handler = DivActionHandler(
      stateManagement: components.stateManagement,
      tooltipActionPerformer: performer
    )

    handler.handle(
      divAction(
        logId: "show",
        typed: .divActionShowTooltip(DivActionShowTooltip(
          id: .value("hint"),
          multiple: .value(false)
        ))
      ),
      path: cardId.path + "0" + "button",
      source: .tap,
      sender: nil
    )
    handler.handle(
      divAction(
        logId: "set_state",
        typed: .divActionSetState(DivActionSetState(
          stateId: .value("hint/0/state/selected")
        ))
      ),
      path: cardId.path + "0" + "button",
      source: .tap,
      sender: nil
    )
    #expect(await waitUntil { createdView.view != nil })
    let view = try #require(createdView.view)

    #expect(paths(forId: "selected").count == 1)
    view.tooltipDidClose()
  }

  @Test
  func interruptedShow_removesRegistrationsAndPreservesState() async {
    let stateManager = components.stateManagement.getStateManagerForCard(cardId: cardId)
    stateManager.setState(stateBlockPath: tooltipStatePath(), stateID: "selected")
    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: stateContent()
    )

    view.tooltipDidClose()

    #expect(paths(forId: "selected").isEmpty)
    #expect(stateManager.items[tooltipStatePath()]?.currentStateID == "selected")
    withExtendedLifetime(view) {}
  }

  @Test
  func onConditionTrigger_firesOnEveryShow() async {
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["enabled": .bool(true), "fired": .string("no")]
    )
    let content = divContainer(variableTriggers: [
      DivTrigger(
        actions: [setVariableAction(name: "fired", value: "yes")],
        condition: expression("@{enabled}"),
        mode: .value(.onCondition)
      ),
    ])
    let tooltip = tooltipId(anchor: "item")
    let first = await makeTooltipView(tooltip: tooltip, content: content)
    await drainMainQueue()
    #expect(firedValue == "yes")
    first.tooltipDidClose()

    components.variablesStorage.update(cardId: cardId, name: "fired", value: "no")
    let second = await makeTooltipView(tooltip: tooltip, content: content)
    await drainMainQueue()

    #expect(firedValue == "yes")
    second.tooltipDidClose()
  }

  @Test
  func hostSetSource_keepsOpenTooltipTriggersRunning() async {
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["counter": .integer(0), "fired": .string("no")]
    )
    let host = makeHostProvider(hostData())
    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: divContainer(id: "tooltip_content", variableTriggers: [makeTrigger()])
    )

    setHostSource(hostData(), in: host)
    setCounter(1)
    components.flushUpdateActions()

    #expect(firedValue == "yes")
    view.tooltipDidClose()
    withExtendedLifetime(host) {}
  }

  @Test
  func hostSetSource_keepsOpenTooltipFunctions() async throws {
    components.variablesStorage.set(cardId: cardId, variables: ["result": .integer(0)])
    let host = makeHostProvider(hostData())
    let view = await makeTooltipView(
      tooltip: tooltipId(anchor: "item"),
      content: divContainer(
        id: "tooltip_content",
        functions: [makeFunction(name: "tooltip_fn", result: 42)]
      )
    )
    let contentPath = try #require(paths(forId: "tooltip_content").first)

    setHostSource(hostData(), in: host)
    storeInResult("@{tooltip_fn()}", at: contentPath)

    #expect(resultValue == 42)
    view.tooltipDidClose()
    withExtendedLifetime(host) {}
  }

  @Test
  func cardFunctionChange_reachesTooltipOnlyAfterItIsShownAgain() async throws {
    components.variablesStorage.set(cardId: cardId, variables: ["result": .integer(0)])
    let host = makeHostProvider(
      hostData(functions: [makeFunction(name: "card_fn", result: 1)])
    )
    let tooltip = tooltipId(anchor: "item")
    let content = divContainer(id: "tooltip_content")
    let first = await makeTooltipView(tooltip: tooltip, content: content)
    try storeInResult("@{card_fn()}", at: #require(paths(forId: "tooltip_content").first))
    #expect(resultValue == 1)

    setHostSource(hostData(functions: [makeFunction(name: "card_fn", result: 2)]), in: host)
    first.tooltipDidClose()
    let second = await makeTooltipView(tooltip: tooltip, content: content)
    try storeInResult("@{card_fn()}", at: #require(paths(forId: "tooltip_content").first))

    #expect(resultValue == 2)
    second.tooltipDidClose()
    withExtendedLifetime(host) {}
  }

  @Test
  func hostSetSource_dropsTriggersAndFunctionsOfPreviousHostData() {
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["counter": .integer(0), "fired": .string("no")]
    )
    let host = makeHostProvider(
      hostData(
        functions: [makeFunction(name: "old_fn", result: 1)],
        variableTriggers: [makeTrigger()]
      )
    )

    setHostSource(hostData(), in: host)
    setCounter(1)
    components.flushUpdateActions()

    let context = components.makeContext(cardId: cardId, cachedImageHolders: [])
    #expect(firedValue == "no")
    #expect(context.functionsStorage?.getStorage(path: cardId.path, contains: "old_fn") == nil)
    withExtendedLifetime(host) {}
  }

  private func hostData(
    functions: [DivFunction]? = nil,
    variableTriggers: [DivTrigger]? = nil
  ) -> DivData {
    DivData(
      functions: functions,
      logId: cardId.rawValue,
      states: [.init(div: divSeparator(id: "host"), stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: variableTriggers,
      variables: nil
    )
  }

  private func makeHostProvider(_ data: DivData) -> DivBlockProvider {
    let provider = DivBlockProvider(
      id: DivViewId(cardId: cardId),
      divKitComponents: components,
      onCardSizeChanged: { _, _ in }
    )
    setHostSource(data, in: provider)
    return provider
  }

  private func setHostSource(_ data: DivData, in provider: DivBlockProvider) {
    provider.setSource(
      DivViewSource(kind: .divData(data), cardId: cardId),
      debugParams: DebugParams()
    )
  }

  private func makeFunction(name: String, result: Int) -> DivFunction {
    DivFunction(arguments: [], body: "@{\(result)}", name: name, returnType: .integer)
  }

  private func storeInResult(_ expressionString: String, at path: UIElementPath) {
    components.actionHandler.handle(
      divAction(
        logId: "store_result",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: .integerValue(IntegerValue(value: expression(expressionString))),
          variableName: .value("result")
        ))
      ),
      path: path,
      source: .tap,
      sender: nil
    )
  }

  private func makeTooltipView(tooltip: DivViewId, content: Div) async -> DivView {
    await DivTooltipViewFactory(
      divKitComponents: components,
      cardId: cardId
    ).makeView(div: content, tooltip: tooltip.tooltip!) as! DivView
  }

  private func tooltipId(anchor: String) -> DivViewId {
    DivViewId(
      cardId: cardId,
      tooltip: DivViewId.Tooltip(
        id: "hint",
        anchorPath: cardId.path + "0" + anchor
      )
    )
  }

  private func tooltipStatePath() -> DivStatePath {
    DivStatePath.tooltipRoot(id: "hint") + DivStateID(rawValue: "0")
      + DivStateID(rawValue: "state")
  }

  private func stateContent() -> Div {
    divState(
      divId: "state",
      defaultStateId: .value("default"),
      states: [
        divStateState(div: divSeparator(id: "default"), stateId: "default"),
        divStateState(div: divSeparator(id: "selected"), stateId: "selected"),
      ]
    )
  }

  private func makeTrigger() -> DivTrigger {
    DivTrigger(
      actions: [setVariableAction(name: "fired", value: "yes")],
      condition: expression("@{counter > 0}"),
      mode: .value(.onVariable)
    )
  }

  private func tooltipContentWithCancelAction() -> Div {
    divContainer(
      id: "tooltip_content",
      animators: [
        .divNumberAnimator(DivNumberAnimator(
          cancelActions: [incrementVariableAction(name: "cancel_count")],
          duration: .value(10000),
          endValue: .value(1),
          id: "tooltip_animator",
          variableName: "animation_value"
        )),
      ]
    )
  }

  private func startTooltipAnimator(tooltip: DivViewId) {
    components.actionHandler.handle(
      divAction(
        typed: .divActionAnimatorStart(DivActionAnimatorStart(
          animatorId: "tooltip_animator"
        ))
      ),
      path: tooltip.path,
      source: .tap,
      sender: nil
    )
  }

  private func setVariableAction(name: String, value: String) -> DivAction {
    divAction(
      logId: "set_\(name)",
      typed: .divActionSetVariable(DivActionSetVariable(
        value: .stringValue(StringValue(value: .value(value))),
        variableName: .value(name)
      ))
    )
  }

  private func incrementVariableAction(name: String) -> DivAction {
    divAction(
      logId: "inc_\(name)",
      typed: .divActionSetVariable(DivActionSetVariable(
        value: .integerValue(IntegerValue(value: expression("@{\(name) + 1}"))),
        variableName: .value(name)
      ))
    )
  }

  private func setCounter(_ value: Int) {
    components.variablesStorage.update(cardId: cardId, name: "counter", value: String(value))
  }

  private func paths(forId id: String) -> [UIElementPath] {
    components.makeContext(cardId: cardId, cachedImageHolders: [])
      .idToPath.paths(forId: id, cardId: cardId)
  }

  private func animatorDefinition(id: String, viewId: DivViewId) -> DivAnimator? {
    components.makeContext(viewId: viewId, cachedImageHolders: [])
      .animatorController?.definition(path: viewId.path, id: id)
  }

  private func drainMainQueue() async {
    await withCheckedContinuation { continuation in
      onMainThreadAsync {
        continuation.resume()
      }
    }
  }

  private func waitUntil(
    timeout: TimeInterval = 1,
    condition: @escaping () -> Bool
  ) async -> Bool {
    let deadline = Date().addingTimeInterval(timeout)
    while !condition(), Date() < deadline {
      try? await Task.sleep(nanoseconds: 10_000_000)
    }
    return condition()
  }
}

@MainActor
private final class CreatedTooltipView {
  var view: DivView?
}

private final class MockTooltipActionPerformer: TooltipActionPerformer {
  private let onShow: (TooltipInfo) -> Void

  init(onShow: @escaping (TooltipInfo) -> Void) {
    self.onShow = onShow
  }

  func showTooltip(info: TooltipInfo) {
    onShow(info)
  }

  func hideTooltip(identity _: TooltipIdentity) {}
}
#endif
