import LayoutKit

final class ShowTooltipActionHandler {
  private let performer: TooltipActionPerformer?
  private let showTooltip: DivActionURLHandler.ShowTooltipAction?

  init(performer: TooltipActionPerformer?, showTooltip: DivActionURLHandler.ShowTooltipAction?) {
    self.performer = performer
    self.showTooltip = showTooltip
  }

  func handle(_ action: DivActionShowTooltip, context: DivActionHandlingContext) {
    guard let cardId = action.resolveId(context.expressionResolver),
          let multiple = action.resolveMultiple(context.expressionResolver) else {
      return
    }

    let tooltipInfo = TooltipInfo(id: cardId, showsOnStart: false, multiple: multiple)
    guard showTooltip == nil else {
      showTooltip?(tooltipInfo)
      return
    }

    performer?.showTooltip(info: tooltipInfo)
  }
}
