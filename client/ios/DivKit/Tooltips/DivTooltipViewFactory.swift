import VGSL

struct DivTooltipViewFactory {
  private let divKitComponents: DivKitComponents
  private let cardId: DivCardID
  private let widcardExtensionHandlers: [DivExtensionHandler]

  init(
    divKitComponents: DivKitComponents,
    cardId: DivCardID,
    widcardExtensionHandlers: [DivExtensionHandler] = []
  ) {
    self.divKitComponents = divKitComponents
    self.cardId = cardId
    self.widcardExtensionHandlers = widcardExtensionHandlers
  }

  #if os(iOS)
  @MainActor
  func makeView(div: Div, tooltip: DivViewId.Tooltip) async -> VisibleBoundsTrackingView {
    await makeView(div: div, tooltip: tooltip, logId: tooltip.id)
  }

  @MainActor
  func makeSubstrateView(div: Div, tooltip: DivViewId.Tooltip) async -> VisibleBoundsTrackingView {
    let substrate = DivViewId.Tooltip(
      id: "\(tooltip.id)_substrate",
      anchorPath: tooltip.anchorPath
    )
    return await makeView(div: div, tooltip: substrate, logId: substrate.id)
  }

  @MainActor
  private func makeView(
    div: Div,
    tooltip: DivViewId.Tooltip,
    logId: String
  ) async -> VisibleBoundsTrackingView {
    let view = DivView(divKitComponents: divKitComponents)
    let divData = DivData(
      functions: nil,
      logId: logId,
      states: [.init(div: div, stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: nil,
      variables: nil
    )
    view.openAsTooltip(
      viewId: DivViewId(cardId: cardId, tooltip: tooltip),
      registry: divKitComponents.tooltipViewRegistry
    )
    await view.setSource(
      DivViewSource(
        kind: .divData(divData),
        cardId: cardId,
        tooltip: tooltip
      ),
      // Tooltips share the host's card id. Only inherit wildcard handlers to avoid
      // replacing the host's error storage or reporting tooltip timings as card timings.
      debugParams: DebugParams(widcardExtensionHandlers: widcardExtensionHandlers)
    )
    return view
  }

  #else
  func makeView(div _: Div, tooltip _: DivViewId.Tooltip) async -> ViewType {
    self as AnyObject
  }

  func makeSubstrateView(div _: Div, tooltip _: DivViewId.Tooltip) async -> ViewType {
    self as AnyObject
  }
  #endif
}
