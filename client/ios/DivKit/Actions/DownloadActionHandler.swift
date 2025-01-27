import Foundation
import VGSL

final class DownloadActionHandler {
  private let patchProvider: DivPatchProvider
  private let updateCard: DivActionHandler.UpdateCardAction

  init(
    patchProvider: DivPatchProvider,
    updateCard: @escaping DivActionHandler.UpdateCardAction
  ) {
    self.patchProvider = patchProvider
    self.updateCard = updateCard
  }

  func handle(_ action: DivActionDownload, context: DivActionHandlingContext) {
    guard let url = action.resolveUrl(context.expressionResolver) else {
      return
    }

    handle(
      url: url,
      context: context,
      onSuccessActions: action.onSuccessActions,
      onFailActions: action.onFailActions
    )
  }

  func handle(
    url: URL,
    context: DivActionHandlingContext,
    onSuccessActions: [DivAction]?,
    onFailActions: [DivAction]?
  ) {
    let info = context.info
    patchProvider.getPatch(
      url: url,
      info: info,
      completion: { [weak self] in
        guard let self else {
          return
        }
        let callbackActions: [DivAction]
        switch $0 {
        case let .success(patch):
          updateCard(.patch(info.cardId, patch))
          callbackActions = onSuccessActions ?? []
        case .failure:
          callbackActions = onFailActions ?? []
        }
        for action in callbackActions {
          context.actionHandler.handle(
            action,
            path: info.path,
            source: .callback,
            sender: nil
          )
        }
      }
    )
  }
}
