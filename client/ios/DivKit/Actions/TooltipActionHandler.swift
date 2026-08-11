import LayoutKit

final class TooltipActionHandler {
  private let performer: TooltipActionPerformer?
  private let reporter: DivReporter

  init(
    performer: TooltipActionPerformer?,
    reporter: DivReporter
  ) {
    self.performer = performer
    self.reporter = reporter
  }

  func handle(_ action: DivActionShowTooltip, context: DivActionHandlingContext) {
    guard let id = action.resolveId(context.expressionResolver),
          let multiple = action.resolveMultiple(context.expressionResolver) else {
      return
    }

    showTooltip(makeTooltipInfo(id: id, multiple: multiple, context: context))
  }

  func handle(_ action: DivActionHideTooltip, context: DivActionHandlingContext) {
    guard let id = action.resolveId(context.expressionResolver) else {
      return
    }

    hideTooltip(
      TooltipIdentity(id: id, scopePath: context.scopePath)
    )
  }

  func handleShowTooltip(id: String, multiple: Bool, context: DivActionHandlingContext) {
    showTooltip(makeTooltipInfo(id: id, multiple: multiple, context: context))
  }

  func handleHideTooltip(id: String, context: DivActionHandlingContext) {
    hideTooltip(
      TooltipIdentity(id: id, scopePath: context.scopePath)
    )
  }

  func showTooltip(_ info: TooltipInfo) {
    performer?.showTooltip(info: info)
  }

  func hideTooltip(_ identity: TooltipIdentity) {
    performer?.hideTooltip(identity: identity)
  }

  private func makeTooltipInfo(
    id: String,
    multiple: Bool,
    context: DivActionHandlingContext
  ) -> TooltipInfo {
    let cardId = context.cardId
    let sourcePath = context.sourcePath
    return TooltipInfo(
      id: id,
      showsOnStart: false,
      multiple: multiple,
      scopePath: context.scopePath,
      onError: { [weak self] message in
        self?.reporter.reportError(
          cardId: cardId,
          error: DivUnknownError(message: message, path: sourcePath)
        )
      }
    )
  }
}
