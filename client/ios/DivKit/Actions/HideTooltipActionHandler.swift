import LayoutKit

final class HideTooltipActionHandler {
  private let performer: TooltipActionPerformer?
  private let showTooltip: DivActionURLHandler.ShowTooltipAction?

  init(performer: TooltipActionPerformer?, showTooltip: DivActionURLHandler.ShowTooltipAction?) {
    self.performer = performer
    self.showTooltip = showTooltip
  }

  func handle(_ action: DivActionHideTooltip, context: DivActionHandlingContext) {
    guard let cardId = action.resolveId(context.expressionResolver) else {
      return
    }

    guard showTooltip == nil else {
      return
    }

    performer?.hideTooltip(id: cardId)
  }
}
