@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct FocusElementActionHandlerTests {
  @Test
  @MainActor
  func focusElement_insideTooltip_afterHostRemodel_usesDisplayedTooltipPath() throws {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["input_text": .string("")]
    )
    let tooltipContent = divContainer(
      id: "tooltip_scope",
      items: [divInput(id: "input", textVariable: "input_text")]
    )
    let host = divSeparator(
      tooltips: [
        DivTooltip(
          div: tooltipContent,
          id: "tooltip1",
          position: .value(.center)
        ),
      ]
    )

    // The anchor is the unnamed separator, so modeling puts it at <card>/0/separator - the
    // tooltip's identity has to be built from that same path to match what the providers make.
    let displayedTooltip = DivViewId(
      cardId: cardId,
      tooltip: DivViewId.Tooltip(
        id: "tooltip1",
        anchorPath: cardId.path + "0" + DivSeparator.type
      )
    )

    #if os(iOS)
    remodelHostAndTooltipThroughProviders(
      components: components,
      cardId: cardId,
      host: host,
      tooltipContent: tooltipContent,
      tooltipId: "tooltip1",
      anchorPath: displayedTooltip.tooltip!.anchorPath
    )
    #else
    // Stand-in for non-iOS targets: repeated makeBlock skips resetIdToPath, but still
    // checks that tooltip registrations are not duplicated after a second host pass.
    let hostContext = components.makeContext(cardId: cardId, cachedImageHolders: [])
    let tooltipContext = components.makeContext(
      viewId: displayedTooltip,
      cachedImageHolders: []
    )
    _ = makeBlock(host, context: hostContext)
    _ = try divData(tooltipContent).makeBlock(context: tooltipContext)
    _ = makeBlock(host, context: hostContext)
    #endif

    let tooltipContext = components.makeContext(
      viewId: displayedTooltip,
      cachedImageHolders: []
    )

    let inputPaths = tooltipContext.idToPath.paths(forId: "input", cardId: cardId)
    #expect(inputPaths.count == 1)
    guard let inputPath = inputPaths.first else {
      return
    }

    // The modeled path already contains `tooltip#<id>`, which is what identifies the tooltip
    // at dispatch time.
    components.actionHandler.handle(
      divAction(
        logId: "focus_tooltip_input",
        scopeId: "tooltip_scope",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: inputPath + "action",
      source: .tap,
      sender: nil
    )

    #expect(reporter.lastError == nil)
    #expect(components.blockStateStorage.isFocused(path: inputPath))
  }

  @Test
  func focusElement_withScopeId_firstScope_setsFocusOnFirstInput() {
    let layout = makeScopedFocusLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(layout.blockStateStorage.isFocused(path: layout.firstInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.secondInputPath))
  }

  @Test
  func focusElement_withScopeId_secondScope_setsFocusOnSecondInput() {
    let layout = makeScopedFocusLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "second",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(layout.blockStateStorage.isFocused(path: layout.secondInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.firstInputPath))
  }

  @Test
  func focusElement_withoutScopeId_ambiguousId_reportsErrorAndDoesNotFocus() {
    let layout = makeScopedFocusLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError != nil)
    #expect(!layout.blockStateStorage.isFocused(path: layout.firstInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.secondInputPath))
  }

  @Test
  func focusElement_whenTargetNotModeledYet_defersFocusUntilRemodel() {
    let blockStateStorage = DivBlockStateStorage()
    let idToPath = IdToPath()
    let inputPath = cardId.path + "container" + "0" + "input"
    let reporter = MockReporter()
    let pendingActions = PendingActionsStorage()
    var updateReasons: [DivCardUpdateReason] = []
    let handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
      pendingActions: pendingActions,
      reporter: reporter,
      updateCard: { updateReasons.append($0) }
    )

    // The input lives in a `gone` subtree, so it is not in `idToPath` yet.
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    // Nothing focused yet, no error reported, but a re-model was scheduled.
    #expect(reporter.lastError == nil)
    #expect(!blockStateStorage.isFocused(path: inputPath))
    #expect(!updateReasons.isEmpty)

    // The re-model reveals the element and repopulates `idToPath`.
    idToPath.add(inputPath, forId: "input", viewId: hostViewId)
    handler.processPendingActions(pendingActions.take())

    #expect(reporter.lastError == nil)
    #expect(blockStateStorage.isFocused(path: inputPath))
  }

  @Test
  func focusElement_whenTargetNeverAppears_reportsNotFoundOnDrain() {
    let blockStateStorage = DivBlockStateStorage()
    let idToPath = IdToPath()
    let reporter = MockReporter()
    let pendingActions = PendingActionsStorage()
    let handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
      pendingActions: pendingActions,
      reporter: reporter
    )

    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    // Deferred: the error is not reported immediately.
    #expect(reporter.lastError == nil)

    // The element is still absent after the re-model, so the deferred error surfaces.
    handler.processPendingActions(pendingActions.take())
    #expect(reporter.lastError != nil)
  }

  private func makeScopedFocusLayout() -> ScopedFocusLayout {
    let blockStateStorage = DivBlockStateStorage()
    let idToPath = IdToPath()
    let firstScopePath = cardId.path + "container" + "0" + "first"
    let secondScopePath = cardId.path + "container" + "1" + "second"
    let firstInputPath = firstScopePath + "0" + "input"
    let secondInputPath = secondScopePath + "0" + "input"

    idToPath.add(firstScopePath, forId: "first", viewId: hostViewId)
    idToPath.add(secondScopePath, forId: "second", viewId: hostViewId)
    idToPath.add(firstInputPath, forId: "input", viewId: hostViewId)
    idToPath.add(secondInputPath, forId: "input", viewId: hostViewId)

    let reporter = MockReporter()
    let handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
      reporter: reporter
    )

    return ScopedFocusLayout(
      blockStateStorage: blockStateStorage,
      handler: handler,
      reporter: reporter,
      firstInputPath: firstInputPath,
      secondInputPath: secondInputPath
    )
  }
}

private let cardId = DivBlockModelingContext.testCardId
private let hostViewId = DivViewId(cardId: cardId)

private struct ScopedFocusLayout {
  let blockStateStorage: DivBlockStateStorage
  let handler: DivActionHandler
  let reporter: MockReporter
  let firstInputPath: UIElementPath
  let secondInputPath: UIElementPath
}
