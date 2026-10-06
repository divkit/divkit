@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing
import VGSL

@Suite
@MainActor
struct PendingActionsTickTests {
  private let cardId: DivCardID = "card"

  #if os(iOS)
  @Test(arguments: [true, false])
  func actionResolvesAfterAllProvidersRegardlessOfSubscriptionOrder(
    tooltipFirst: Bool
  ) {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(cardId: cardId, variables: ["show_target": .integer(0)])
    let providers = makeProviders(
      components: components,
      tooltipFirst: tooltipFirst,
      host: conditionalTarget(),
      tooltip: divSeparator(id: "source")
    )

    setShowTargetAndEnqueueFocus(
      components: components,
      sourceViewId: providers.tooltip.id
    )
    components.flushUpdateActions()

    let targetPath = onlyPath(forId: "target", components: components)
    #expect(reporter.lastError == nil)
    #expect(components.blockStateStorage.isFocused(path: targetPath))
    withExtendedLifetime(providers) {}
  }

  @Test
  func hostActionResolvesTargetRevealedByTooltipRemodel() {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(cardId: cardId, variables: ["show_target": .integer(0)])
    let providers = makeProviders(
      components: components,
      tooltipFirst: false,
      host: divSeparator(id: "source"),
      tooltip: conditionalTarget()
    )

    setShowTargetAndEnqueueFocus(
      components: components,
      sourceViewId: providers.host.id
    )
    components.flushUpdateActions()

    let targetPath = onlyPath(forId: "target", components: components)
    #expect(reporter.lastError == nil)
    #expect(components.blockStateStorage.isFocused(path: targetPath))
    withExtendedLifetime(providers) {}
  }

  @Test
  func closedTooltipActionStillResolvesAgainstRemodeledHost() async {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(cardId: cardId, variables: ["show_target": .integer(0)])
    let hostProvider = provider(components: components, id: DivViewId(cardId: cardId))
    await hostProvider.setSource(source(conditionalTarget()), debugParams: DebugParams())

    let tooltipId = DivViewId(
      cardId: cardId,
      tooltip: DivViewId.Tooltip(id: "tooltip", anchorPath: cardId.path + "anchor")
    )
    components.tooltipViewRegistry.open(viewId: tooltipId)
    var tooltipProvider: DivBlockProvider? = provider(components: components, id: tooltipId)
    await tooltipProvider?.setSource(
      source(divSeparator(id: "source"), viewId: tooltipId),
      debugParams: DebugParams()
    )

    setShowTargetAndEnqueueFocus(components: components, sourceViewId: tooltipId)
    tooltipProvider?.invalidate()
    components.tooltipViewRegistry.close(viewId: tooltipId)
    tooltipProvider = nil
    components.flushUpdateActions()

    let targetPath = onlyPath(forId: "target", components: components)
    #expect(reporter.lastError == nil)
    #expect(components.blockStateStorage.isFocused(path: targetPath))
    withExtendedLifetime(hostProvider) {}
  }

  @Test
  func missingTargetReportsExactlyOnceAndIsRemovedFromQueue() {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    let hostProvider = provider(components: components, id: DivViewId(cardId: cardId))
    hostProvider.setSource(source(divSeparator(id: "source")), debugParams: DebugParams())

    enqueueFocus(components: components, sourceViewId: hostProvider.id)
    components.flushUpdateActions()
    components.flushUpdateActions()

    #expect(reporter.errors.count == 1)
    withExtendedLifetime(hostProvider) {}
  }

  @Test
  func targetInTwoViewsReportsAmbiguousAndDoesNotFocus() {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(cardId: cardId, variables: ["show_target": .integer(0)])
    let providers = makeProviders(
      components: components,
      tooltipFirst: false,
      host: conditionalTarget(),
      tooltip: conditionalTarget()
    )

    setShowTargetAndEnqueueFocus(
      components: components,
      sourceViewId: providers.host.id
    )
    components.flushUpdateActions()

    let paths = paths(forId: "target", components: components)
    #expect(paths.count == 2)
    #expect(reporter.errors.count == 1)
    #expect(reporter.errors.first?.message.contains("ambiguous") == true)
    #expect(paths.allSatisfy { !components.blockStateStorage.isFocused(path: $0) })
    withExtendedLifetime(providers) {}
  }

  @Test
  func actionEnqueuedDuringSendWaitsUntilNextFlush() {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(cardId: cardId, variables: ["show_target": .integer(0)])
    let hostProvider = provider(components: components, id: DivViewId(cardId: cardId))
    hostProvider.setSource(source(conditionalTarget()), debugParams: DebugParams())

    var shouldEnqueue = true
    var wasDeferredDuringSend = false
    let disposePool = AutodisposePool()
    components.updateCardSignal.addObserver { _ in
      guard shouldEnqueue else { return }
      shouldEnqueue = false
      components.variablesStorage.update(cardId: cardId, name: "show_target", value: "1")
      enqueueFocus(components: components, sourceViewId: hostProvider.id)
      wasDeferredDuringSend = components.blockStateStorage.focusedElement == nil
        && reporter.lastError == nil
    }.dispose(in: disposePool)

    components.forceUpdate()
    #expect(wasDeferredDuringSend)
    #expect(reporter.lastError == nil)

    components.flushUpdateActions()
    let targetPath = onlyPath(forId: "target", components: components)
    #expect(reporter.lastError == nil)
    #expect(components.blockStateStorage.isFocused(path: targetPath))
    withExtendedLifetime((hostProvider, disposePool)) {}
  }

  @Test
  func actionEnqueuedWhileUpdatesAreDisabledStillSchedulesFlush() async {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    let hostProvider = provider(components: components, id: DivViewId(cardId: cardId))
    let focusAction = divAction(
      logId: "focus_from_initial_trigger",
      typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("target")))
    )
    let data = DivData(
      functions: nil,
      logId: cardId.rawValue,
      states: [.init(div: divSeparator(id: "target"), stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: [
        DivTrigger(
          actions: [focusAction],
          condition: expression("@{true}"),
          mode: .value(.onCondition)
        ),
      ],
      variables: nil
    )

    await hostProvider.setSource(
      DivViewSource(kind: .divData(data), cardId: cardId),
      debugParams: DebugParams()
    )
    await drainMainQueue()

    let targetPath = onlyPath(forId: "target", components: components)
    #expect(reporter.lastError == nil)
    #expect(components.blockStateStorage.isFocused(path: targetPath))
    withExtendedLifetime(hostProvider) {}
  }

  private func makeProviders(
    components: DivKitComponents,
    tooltipFirst: Bool,
    host: Div,
    tooltip: Div
  ) -> (host: DivBlockProvider, tooltip: DivBlockProvider) {
    let tooltipId = DivViewId(
      cardId: cardId,
      tooltip: DivViewId.Tooltip(id: "tooltip", anchorPath: cardId.path + "anchor")
    )
    let hostProvider: DivBlockProvider
    let tooltipProvider: DivBlockProvider
    if tooltipFirst {
      tooltipProvider = provider(components: components, id: tooltipId)
      hostProvider = provider(components: components, id: DivViewId(cardId: cardId))
    } else {
      hostProvider = provider(components: components, id: DivViewId(cardId: cardId))
      tooltipProvider = provider(components: components, id: tooltipId)
    }
    hostProvider.setSource(source(host), debugParams: DebugParams())
    tooltipProvider.setSource(source(tooltip, viewId: tooltipId), debugParams: DebugParams())
    return (hostProvider, tooltipProvider)
  }

  private func provider(
    components: DivKitComponents,
    id: DivViewId
  ) -> DivBlockProvider {
    DivBlockProvider(id: id, divKitComponents: components, onCardSizeChanged: { _, _ in })
  }

  private func source(_ div: Div, viewId: DivViewId? = nil) -> DivViewSource {
    DivViewSource(
      kind: .divData(divData(div)),
      cardId: cardId,
      tooltip: viewId?.tooltip
    )
  }

  private func conditionalTarget() -> Div {
    divContainer(
      items: [divSeparator(id: "target")],
      visibility: expression("@{show_target == 1 ? 'visible' : 'gone'}")
    )
  }

  private func setShowTargetAndEnqueueFocus(
    components: DivKitComponents,
    sourceViewId: DivViewId
  ) {
    components.actionHandler.handle(
      divAction(
        logId: "show",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: .integerValue(IntegerValue(value: .value(1))),
          variableName: .value("show_target")
        ))
      ),
      path: sourceViewId.path + "source",
      source: .tap,
      sender: nil
    )
    enqueueFocus(components: components, sourceViewId: sourceViewId)
  }

  private func enqueueFocus(
    components: DivKitComponents,
    sourceViewId: DivViewId
  ) {
    components.actionHandler.handle(
      divAction(
        logId: "focus",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("target")))
      ),
      path: sourceViewId.path + "source",
      source: .tap,
      sender: nil
    )
  }

  private func paths(
    forId id: String,
    components: DivKitComponents
  ) -> [UIElementPath] {
    components.makeContext(cardId: cardId, cachedImageHolders: [])
      .idToPath.paths(forId: id, cardId: cardId)
  }

  private func onlyPath(
    forId id: String,
    components: DivKitComponents
  ) -> UIElementPath {
    let result = paths(forId: id, components: components)
    #expect(result.count == 1)
    return result[0]
  }

  private func drainMainQueue() async {
    await withCheckedContinuation { continuation in
      onMainThreadAsync {
        continuation.resume()
      }
    }
  }
  #endif

  @Test
  func resetCardOnlyRemovesThatCardsActions() {
    let storage = PendingActionsStorage()
    let otherCardId: DivCardID = "other"
    storage.enqueue(pendingAction(cardId: cardId))
    storage.enqueue(pendingAction(cardId: otherCardId))

    storage.reset(cardId: cardId)

    let actions = storage.take()
    #expect(actions.count == 1)
    #expect(actions.first?.cardId == otherCardId)
  }

  private func pendingAction(cardId: DivCardID) -> PendingActionsStorage.PendingAction {
    PendingActionsStorage.PendingAction(
      id: "target",
      divTypes: nil,
      scopePath: nil,
      cardId: cardId,
      sourcePath: cardId.path + "source",
      apply: { _ in }
    )
  }
}
