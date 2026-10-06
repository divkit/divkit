@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct DivKitComponentsMakeContextTests {
  private let cardId = DivCardID(rawValue: "card")
  private let components = DivKitComponents()
  private let stateManagement = DefaultDivStateManagement()

  @Test
  func withoutAdditionalId_buildsHostContext() {
    let context = components.makeContext(cardId: cardId, cachedImageHolders: [])

    #expect(context.viewId == DivViewId(cardId: cardId))
    #expect(context.path == UIElementPath("card"))
  }

  @Test
  func withAdditionalId_buildsAdditionalViewContext() {
    let context = components.makeContext(
      cardId: cardId,
      additionalId: "extra",
      cachedImageHolders: []
    )
    let expectedViewId = DivViewId(
      cardId: cardId,
      additionalId: "extra"
    )

    #expect(context.path == UIElementPath("card") + "tooltip#extra")
    #expect(context.path.viewId == context.viewId)
    #expect(context.viewId == expectedViewId)
    #expect(context.viewId.isTooltip)
  }

  @Test
  func withAdditionalId_nestedTooltipsAreRejected() {
    let context = components.makeContext(
      cardId: cardId,
      additionalId: "extra",
      cachedImageHolders: []
    )

    _ = makeBlock(
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divContainer(),
            id: "nested_tooltip",
            position: .value(.center)
          ),
        ]
      ),
      context: context,
      ignoreErrors: true
    )

    let errors = context.errorsStorage.errors
    #expect(errors.count == 1)
    #expect(errors[0].message == "Tooltip can not host another tooltips")
  }

  @Test
  func withAdditionalId_idRegistration_survivesHostResetIdToPath() {
    let hostContext = components.makeContext(cardId: cardId, cachedImageHolders: [])
    let extraContext = components.makeContext(
      cardId: cardId,
      additionalId: "extra",
      cachedImageHolders: []
    )

    _ = makeBlock(divSeparator(id: "host_scope"), context: hostContext)
    _ = makeBlock(divSeparator(id: "extra_scope"), context: extraContext)

    #expect(extraContext.idToPath.paths(forId: "extra_scope", cardId: cardId).count == 1)

    components.resetIdToPath(viewId: hostContext.viewId)

    #expect(extraContext.idToPath.paths(forId: "extra_scope", cardId: cardId).count == 1)
    #expect(hostContext.idToPath.paths(forId: "host_scope", cardId: cardId).isEmpty)

    components.reset(cardId: cardId)

    #expect(extraContext.idToPath.paths(forId: "extra_scope", cardId: cardId).isEmpty)
  }

  @Test
  func withAdditionalId_relativeSetState_targetsAdditionalViewStateRoot() {
    let actionComponents = DivKitComponents(stateManagement: stateManagement)
    let context = actionComponents.makeContext(
      cardId: cardId,
      additionalId: "extra",
      cachedImageHolders: []
    )
    let params = setState("state/state2")
      .uiAction(context: context)?
      .payload
      .divActionParams

    #expect(params?.path.viewId == context.viewId)
    guard let params else {
      return
    }
    actionComponents.actionHandler.handle(params: params, sender: nil)

    #expect(currentStateId(at: "tooltip#extra/0/state") == "state2")
    #expect(currentStateId(at: "0/state") == nil)
  }

  private func currentStateId(at path: String) -> DivStateID? {
    stateManagement
      .getStateManagerForCard(cardId: cardId)
      .items[DivStatePath.makeDivStatePath(from: path)]?
      .currentStateID
  }
}

private func setState(_ stateId: String) -> DivAction {
  divAction(
    logId: "set_state",
    typed: .divActionSetState(DivActionSetState(stateId: .value(stateId)))
  )
}
