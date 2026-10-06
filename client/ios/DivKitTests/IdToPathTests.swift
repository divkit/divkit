@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

/// Both tooltips in these tests hang off one anchor, which is what production does when an
/// element declares several - the anchor is the discriminator, not the tooltip id.
private let anchorPath = UIElementPath("card") + "0" + "anchor"

@Suite
struct IdToPathTests {
  private let hostViewId = DivViewId(cardId: "card")
  private let tooltip1ViewId = DivViewId(
    cardId: "card",
    tooltip: DivViewId.Tooltip(id: "tooltip1", anchorPath: anchorPath)
  )
  private let tooltip2ViewId = DivViewId(
    cardId: "card",
    tooltip: DivViewId.Tooltip(id: "tooltip2", anchorPath: anchorPath)
  )

  @Test
  func reset_withTooltipViewId_doesNotAffectHostsRegistrations() {
    let idToPath = IdToPath()
    let hostPath = hostViewId.path + "0" + "hostScope"
    let tooltipPath = tooltip1ViewId.path + "0" + "tooltipScope"

    idToPath.add(hostPath, forId: "hostScope", viewId: hostViewId)
    idToPath.add(tooltipPath, forId: "tooltipScope", viewId: tooltip1ViewId)

    // Simulates the tooltip's own DivBlockProvider refreshing just its own registrations
    // on remodel (see DivKitComponents.resetIdToPath) - this must not wipe the host's,
    // which is exactly the bug this test guards against.
    idToPath.reset(viewId: tooltip1ViewId)

    #expect(idToPath.paths(forId: "hostScope", cardId: "card") == [hostPath])
    #expect(idToPath.paths(forId: "tooltipScope", cardId: "card").isEmpty)
  }

  @Test
  func reset_withTooltipViewId_leavesSiblingTooltipUntouched() {
    let idToPath = IdToPath()
    let tooltip1Path = tooltip1ViewId.path + "0" + "button"
    let tooltip2Path = tooltip2ViewId.path + "0" + "button"

    idToPath.add(tooltip1Path, forId: "button", viewId: tooltip1ViewId)
    idToPath.add(tooltip2Path, forId: "button", viewId: tooltip2ViewId)

    idToPath.reset(viewId: tooltip1ViewId)

    // Both tooltips register the same id, so a card-wide lookup returning exactly the
    // surviving one says both that tooltip1's entry is gone and tooltip2's is intact.
    #expect(idToPath.paths(forId: "button", cardId: "card") == [tooltip2Path])
  }

  @Test
  func resetViewId_host_leavesTooltipAndOtherCardUntouched() {
    let fixture = makeFixture()

    fixture.idToPath.reset(viewId: hostViewId)

    #expect(fixture.idToPath.paths(forId: "hostButton", cardId: "card").isEmpty)
    #expect(fixture.idToPath.paths(forId: "tooltipButton", cardId: "card") == [fixture.tooltipPath])
    #expect(fixture.idToPath
      .paths(forId: "otherButton", cardId: "other_card") == [fixture.otherPath]
    )
  }

  @Test
  func resetViewId_tooltip_leavesHostAndOtherCardUntouched() {
    let fixture = makeFixture()

    fixture.idToPath.reset(viewId: tooltip1ViewId)

    #expect(fixture.idToPath.paths(forId: "hostButton", cardId: "card") == [fixture.hostPath])
    #expect(fixture.idToPath.paths(forId: "tooltipButton", cardId: "card").isEmpty)
    #expect(fixture.idToPath
      .paths(forId: "otherButton", cardId: "other_card") == [fixture.otherPath]
    )
  }

  @Test
  func resetCardId_dropsHostAndItsTooltips_leavesOtherCardUntouched() {
    let fixture = makeFixture()

    fixture.idToPath.reset(cardId: "card")

    #expect(fixture.idToPath.paths(forId: "hostButton", cardId: "card").isEmpty)
    #expect(fixture.idToPath.paths(forId: "tooltipButton", cardId: "card").isEmpty)
    #expect(fixture.idToPath
      .paths(forId: "otherButton", cardId: "other_card") == [fixture.otherPath]
    )
  }

  @Test
  func lookup_spansTheWholeCard() {
    let idToPath = IdToPath()
    let hostPath = hostViewId.path + "0" + "hostButton"
    let tooltipPath = tooltip1ViewId.path + "0" + "tooltipButton"

    idToPath.add(hostPath, forId: "hostButton", viewId: hostViewId)
    idToPath.add(tooltipPath, forId: "tooltipButton", viewId: tooltip1ViewId)

    // Registrations are partitioned so that each view can drop its own, but addressing stays
    // card-wide in both directions - an action inside a tooltip may name an element of the
    // host card, and vice versa.
    #expect(idToPath.paths(forId: "hostButton", cardId: "card") == [hostPath])
    #expect(idToPath.paths(forId: "tooltipButton", cardId: "card") == [tooltipPath])
  }

  @Test
  func lookup_doesNotReachAnotherCard() {
    let idToPath = IdToPath()
    let otherViewId = DivViewId(cardId: "other_card")
    idToPath.add(otherViewId.path + "0" + "button", forId: "button", viewId: otherViewId)

    #expect(idToPath.paths(forId: "button", cardId: "card").isEmpty)
  }

  @Test
  func handle_idDuplicatedBetweenHostAndTooltip_isAmbiguous() {
    let idToPath = IdToPath()
    let reporter = MockReporter()

    idToPath.add(hostViewId.path + "0" + "duplicate", forId: "duplicate", viewId: hostViewId)
    idToPath.add(
      tooltip1ViewId.path + "0" + "duplicate",
      forId: "duplicate",
      viewId: tooltip1ViewId
    )

    let handler = DivActionHandler(idToPath: idToPath, reporter: reporter)

    handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "duplicate",
        typed: .divActionClearFocus(DivActionClearFocus())
      ),
      path: tooltip1ViewId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    // An id reused by the host and one of its tooltips is reported, not silently resolved in
    // favour of the nearest one: choosing for the author is what scope_id exists to avoid.
    #expect(reporter.lastError?.message == "Scope with id 'duplicate' is ambiguous")
  }

  @Test
  func handle_scopeId_fromTooltip_resolvesHostScope() {
    let idToPath = IdToPath()
    let reporter = MockReporter()

    let hostScopePath = hostViewId.path + "0" + "hostScope"
    idToPath.add(hostScopePath, forId: "hostScope", viewId: hostViewId)

    let handler = DivActionHandler(idToPath: idToPath, reporter: reporter)

    handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "hostScope",
        typed: .divActionClearFocus(DivActionClearFocus())
      ),
      path: tooltip1ViewId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(reporter.lastError == nil)
  }

  @Test
  func handle_scopeId_fromHost_survivesTooltipsOwnRemodel() {
    let idToPath = IdToPath()
    let reporter = MockReporter()

    let scopePath = hostViewId.path + "0" + "myScope"
    idToPath.add(scopePath, forId: "myScope", viewId: hostViewId)

    // The tooltip is shown and its own DivBlockProvider remodels, refreshing only its
    // own registrations in the shared IdToPath (see DivKitComponents.resetIdToPath).
    // Before the DivViewId-based addressing fix, this used to wipe the host's scope too,
    // since the reset only ever saw the flattened, host-only cardId.
    idToPath.reset(viewId: tooltip1ViewId)

    let handler = DivActionHandler(idToPath: idToPath, reporter: reporter)

    handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "myScope",
        typed: .divActionClearFocus(DivActionClearFocus())
      ),
      path: hostViewId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(reporter.lastError == nil)
  }

  @Test
  @MainActor
  func animatorStart_insideTooltip_afterHostRemodel_matchesDisplayedTooltipAnimator() throws {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    components.variablesStorage.set(
      cardId: hostViewId.cardId,
      variables: ["animation_value": .number(0)]
    )
    let tooltipContent = divContainer(
      id: "tooltip_scope",
      animators: [
        .divNumberAnimator(DivNumberAnimator(
          duration: .value(100),
          endValue: .value(1),
          id: "tooltip_animator",
          variableName: "animation_value"
        )),
      ]
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
      cardId: hostViewId.cardId,
      tooltip: DivViewId.Tooltip(
        id: "tooltip1",
        anchorPath: hostViewId.cardId.path + "0" + DivSeparator.type
      )
    )

    #if os(iOS)
    remodelHostAndTooltipThroughProviders(
      components: components,
      cardId: hostViewId.cardId,
      host: host,
      tooltipContent: tooltipContent,
      tooltipId: "tooltip1",
      anchorPath: displayedTooltip.tooltip!.anchorPath
    )
    #else
    // Stand-in for non-iOS targets: repeated makeBlock skips resetIdToPath, but still
    // checks that tooltip registrations are not duplicated after a second host pass.
    let hostContext = components.makeContext(
      cardId: hostViewId.cardId,
      cachedImageHolders: []
    )
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

    let scopePaths = tooltipContext.idToPath.paths(
      forId: "tooltip_scope",
      cardId: hostViewId.cardId
    )
    #expect(scopePaths.count == 1)
    guard let scopePath = scopePaths.first else {
      return
    }

    // The modeled path already contains `tooltip#<id>`, which is what identifies the tooltip
    // at dispatch time.
    components.actionHandler.handle(
      divAction(
        logId: "start_tooltip_animator",
        scopeId: "tooltip_scope",
        typed: .divActionAnimatorStart(DivActionAnimatorStart(
          animatorId: "tooltip_animator"
        ))
      ),
      path: scopePath + "action",
      source: .tap,
      sender: nil
    )

    #expect(reporter.lastError == nil)
  }

  private func makeFixture() -> (
    idToPath: IdToPath,
    hostPath: UIElementPath,
    tooltipPath: UIElementPath,
    otherPath: UIElementPath
  ) {
    let idToPath = IdToPath()
    let otherViewId = DivViewId(cardId: "other_card")
    let hostPath = hostViewId.path + "0" + "hostButton"
    let tooltipPath = tooltip1ViewId.path + "0" + "tooltipButton"
    let otherPath = otherViewId.path + "0" + "otherButton"

    idToPath.add(hostPath, forId: "hostButton", viewId: hostViewId)
    idToPath.add(tooltipPath, forId: "tooltipButton", viewId: tooltip1ViewId)
    idToPath.add(otherPath, forId: "otherButton", viewId: otherViewId)

    return (idToPath, hostPath, tooltipPath, otherPath)
  }
}
