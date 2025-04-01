import LayoutKit

final class TooltipActionHandler {
  private let performer: TooltipActionPerformer?
  private let showTooltipAction: DivActionHandler.ShowTooltipAction?

  init(
    performer: TooltipActionPerformer?,
    showTooltip: DivActionHandler.ShowTooltipAction?
  ) {
    self.performer = performer
    self.showTooltipAction = showTooltip
  }

  func handle(_ action: DivActionShowTooltip, context: DivActionHandlingContext) {
    guard let cardId = action.resolveId(context.expressionResolver),
          let multiple = action.resolveMultiple(context.expressionResolver) else {
      return
    }

    showTooltip(TooltipInfo(id: cardId, showsOnStart: false, multiple: multiple))
  }

  func handle(_ action: DivActionHideTooltip, context: DivActionHandlingContext) {
    guard let id = action.resolveId(context.expressionResolver) else {
      return
    }

    hideTooltip(id: id)
  }

  func showTooltip(_ info: TooltipInfo) {
    if let showTooltipAction {
      showTooltipAction(info)
    } else {
      performer?.showTooltip(info: info)
    }
  }

  func hideTooltip(id: String) {
    if showTooltipAction == nil {
      performer?.hideTooltip(id: id)
    }
  }
}
