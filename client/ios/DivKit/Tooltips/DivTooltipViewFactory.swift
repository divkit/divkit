import CommonCorePublic

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
  func makeView(div: Div, tooltipId: String) -> VisibleBoundsTrackingView {
    let view = DivView(divKitComponents: divKitComponents)
    let divData = DivData(
      logId: tooltipId,
      states: [.init(div: div, stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: nil,
      variables: nil
    )
    view.setSource(
      DivViewSource(
        kind: .divData(divData),
        cardId: cardId,
        additionalId: tooltipId
      )
    )
    return view
  }

  #else
  func makeView(div: Div, tooltipId: String) -> ViewType {
    self as AnyObject
  }
  #endif
}
