@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import Foundation
import LayoutKit
import Testing

/// `set_state` addresses a target by path rather than by id, and the path itself says whether
/// it is absolute (`0/state/state2`, rooted at a div-data state of the host card) or relative
/// (`state/state2`, resolved against the view the action came from). That view is recovered
/// from the action path: a `tooltip#<id>` segment names a tooltip.
@Suite
struct SetStateActionHandlerTests {
  private let stateManagement = DefaultDivStateManagement()
  private let handler: DivActionHandler

  init() {
    handler = DivActionHandler(stateManagement: stateManagement)
  }

  @Test
  func relativeStatePath_fromHost_staysAbsolute() {
    handler.handle(
      setState("0/state/state2"),
      path: cardId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "0/state") == "state2")
  }

  @Test
  func hierarchicalHostPath_fromTooltipWithNumericId_staysAbsolute() {
    handler.handle(
      setState("0/state1/state11/state111"),
      path: tooltipViewId("0").path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "0/state1/state11") == "state111")
    #expect(currentStateId(at: "tooltip#0/0/state1/state11") == nil)
  }

  @Test
  func relativeStatePath_fromTooltip_resolvesAgainstThatTooltip() {
    handler.handle(
      setState("state/state2"),
      path: tooltipViewId("my_tooltip").path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "tooltip#my_tooltip/0/state") == "state2")
  }

  @Test(arguments: ["0", "2"])
  func relativeStatePath_fromTooltipWithNumericId_resolvesAgainstThatTooltip(
    tooltipId: String
  ) {
    // A tooltip id is an arbitrary string, so it may well look like a div-data state id.
    // The originating view is the `tooltip#<id>` segment of the path, so a numeric id stays
    // a tooltip rather than being read as the host's state.
    handler.handle(
      setState("state/state2"),
      path: tooltipViewId(tooltipId).path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "tooltip#\(tooltipId)/0/state") == "state2")
    #expect(currentStateId(at: "state") == nil)
  }

  @Test
  func legacyTooltipPath_fromHost_isRewrittenToTooltipNamespace() {
    handler.handle(
      setState("tooltip1/0/state/state2"),
      path: cardId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "tooltip#tooltip1/0/state") == "state2")
    #expect(currentStateId(at: "tooltip1/0/state") == nil)
  }

  @Test
  func namespacedTooltipPath_fromHost_isNotRewrittenAgain() {
    handler.handle(
      setState("tooltip#tooltip1/0/state/state2"),
      path: cardId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "tooltip#tooltip1/0/state") == "state2")
    #expect(currentStateId(at: "tooltip#tooltip#tooltip1/0/state") == nil)
  }

  /// End-to-end over the shape of regression_test_data/tooltips/tooltip-with-states.json: two
  /// buttons sitting inside a tooltip, one naming the host's state absolutely and one naming
  /// the tooltip's own state relatively, while both `div-state`s share the id `state`. Goes
  /// through the real modeling context and the real payload, so the path produced by `uiAction`
  /// is what `handle(params:)` uses to recover the tooltip.
  @Test
  func tooltipWithStates_addressesBothRootsFromInsideTheTooltip() {
    let components = DivKitComponents(stateManagement: stateManagement)
    let tooltip = tooltipViewId("tooltip1")
    let tooltipContext = components.makeContext(
      viewId: tooltip,
      cachedImageHolders: []
    )

    for stateId in ["0/state/state2", "state/state2"] {
      let params = setState(stateId)
        .uiAction(context: tooltipContext)?
        .payload
        .divActionParams
      #expect(params?.path.viewId == tooltip)
      guard let params else {
        return
      }
      components.actionHandler.handle(params: params, sender: nil)
    }

    #expect(currentStateId(at: "0/state") == "state2")
    #expect(currentStateId(at: "tooltip#tooltip1/0/state") == "state2")
  }

  @Test
  func tooltipRelativeStatePath_afterPayloadRoundTrip_resolvesAgainstTooltip() throws {
    let components = DivKitComponents(stateManagement: stateManagement)
    let tooltipContext = components.makeContext(
      viewId: tooltipViewId("tooltip1"),
      cachedImageHolders: []
    )
    let payload = try #require(
      setState("state/state2").uiAction(context: tooltipContext)?.payload
    )
    let data = try JSONEncoder().encode(payload)
    let decodedPayload = try JSONDecoder().decode(
      UserInterfaceAction.Payload.self,
      from: data
    )
    let params = try #require(decodedPayload.divActionParams)

    components.actionHandler.handle(params: params, sender: nil)

    #expect(currentStateId(at: "tooltip#tooltip1/0/state") == "state2")
  }

  @Test
  func spiHandle_tooltipPath_resolvesRelativeStateInTooltip() {
    let tooltip = tooltipViewId("hint")
    handler.handle(
      setState("state/state2"),
      path: tooltip.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "tooltip#hint/0/state") == "state2")
  }

  @Test
  func spiHandle_hostPath_resolvesAbsoluteStateOnHost() {
    handler.handle(
      setState("0/state/state2"),
      path: cardId.path + "0" + "button",
      source: .tap,
      sender: nil
    )

    #expect(currentStateId(at: "0/state") == "state2")
    #expect(currentStateId(at: "tooltip#hint/0/state") == nil)
  }

  /// Patch callbacks are dispatched with the card-root path, so they run as host actions even
  /// when a tooltip provider applies the patch. A nonnumeric `state/state2` is then the host's
  /// legacy tooltip alias: the root is rewritten to `tooltip#state`. The tooltip that applied
  /// the patch (`tooltip#hint`) keeps its own state unset.
  @Test
  func patchCallback_fromTooltipProvider_relativeStateUsesHostTooltipAlias() {
    let components = DivKitComponents(stateManagement: stateManagement)
    let tooltipContext = components.makeContext(
      viewId: tooltipViewId("hint"),
      cachedImageHolders: []
    )
    let data = divData(divText(id: "target", text: "old"))
    let patch = DivPatch(
      changes: [
        DivPatch.Change(id: "target", items: [divText(id: "target", text: "new")]),
      ],
      onAppliedActions: [setState("state/state2")]
    )

    _ = data.applyPatchWithActions(patch, context: tooltipContext)

    #expect(currentStateId(at: "tooltip#state") == "state2")
    #expect(currentStateId(at: "tooltip#hint/0/state") == nil)
  }

  @Test
  func triggerInsideTooltip_relativeSetState_resolvesAgainstTooltip() {
    let components = DivKitComponents(stateManagement: stateManagement)
    let tooltip = tooltipViewId("hint")
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["should_switch": .bool(true)]
    )
    let tooltipContext = components.makeContext(
      viewId: tooltip,
      cachedImageHolders: []
    )

    _ = makeBlock(
      divContainer(
        id: "root",
        variableTriggers: [
          DivTrigger(
            actions: [setState("state/state2")],
            condition: expression("@{should_switch}"),
            mode: .value(.onCondition)
          ),
        ]
      ),
      context: tooltipContext
    )

    #expect(currentStateId(at: "tooltip#hint/0/state") == "state2")
  }

  @Test
  func propertyInsideTooltip_relativeSetState_resolvesAgainstTooltip() {
    let components = DivKitComponents(stateManagement: stateManagement)
    let tooltip = tooltipViewId("hint")
    let tooltipContext = components.makeContext(
      viewId: tooltip,
      cachedImageHolders: []
    )

    _ = makeBlock(
      divText(
        id: "label",
        variables: [
          .propertyVariable(PropertyVariable(
            get: .value("old"),
            name: "title",
            set: [setState("state/state2")],
            valueType: .string
          )),
        ]
      ),
      context: tooltipContext
    )

    components.variablesStorage.update(
      path: tooltip.path + "0" + "label",
      name: "title",
      value: "new"
    )

    #expect(currentStateId(at: "tooltip#hint/0/state") == "state2")
  }

  private func currentStateId(at path: String) -> DivStateID? {
    stateManagement
      .getStateManagerForCard(cardId: cardId)
      .items[DivStatePath.makeDivStatePath(from: path)]?
      .currentStateID
  }
}

/// The state namespace is rooted at `tooltip#<id>`, so these tests are indifferent to the
/// anchor - it is supplied only to build a well-formed identity.
private func tooltipViewId(_ id: String) -> DivViewId {
  DivViewId(
    cardId: cardId,
    tooltip: DivViewId.Tooltip(id: id, anchorPath: cardId.path + "anchor")
  )
}

private func setState(_ stateId: String) -> DivAction {
  divAction(
    logId: "set_state",
    typed: .divActionSetState(DivActionSetState(stateId: .value(stateId)))
  )
}

private let cardId = DivBlockModelingContext.testCardId
