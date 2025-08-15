import VGSL

public struct DivTooltipViewFactory {
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
  func makeView(div: Div, tooltipId: String) async -> VisibleBoundsTrackingView {
    let view = DivView(divKitComponents: divKitComponents)
    let divData = DivData(
      functions: nil,
      logId: tooltipId,
      states: [.init(div: div, stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: nil,
      variables: nil
    )
    await view.setSource(
      DivViewSource(
        kind: .divData(divData),
        cardId: cardId,
        additionalId: tooltipId
      )
    )
    return view
  }

  #else
  func makeView(div _: Div, tooltipId _: String) async -> ViewType {
    self as AnyObject
  }
  #endif
}
