import VGSL

struct DivTooltipViewFactory {
  private let divKitComponents: DivKitComponents
  private let cardId: DivCardID

  init(
    divKitComponents: DivKitComponents,
    cardId: DivCardID
  ) {
    self.divKitComponents = divKitComponents
    self.cardId = cardId
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
      )
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
