import LayoutKit

final class FocusElementActionHandler {
  private let pathResolver: ActionPathResolver

  init(pathResolver: ActionPathResolver) {
    self.pathResolver = pathResolver
  }

  func handle(_ action: DivActionFocusElement, context: DivActionHandlingContext) {
    guard let elementId = action.resolveElementId(context.expressionResolver) else {
      return
    }

    let cardId = context.cardId
    if let previousCard = context.blockStateStorage.getFocusedElement()?.cardId,
       previousCard != cardId {
      context.updateCard(.state(previousCard))
    }

    let state = TextInputViewState(pendingSelection: .textEnd)
    pathResolver.resolve(id: elementId, context: context) { path in
      context.blockStateStorage.setFocused(isFocused: true, path: path)
      context.blockStateStorage.setPendingState(path, state: state)
      context.updateCard(.state(cardId))
    }
  }
}

extension TextInputViewState.Selection {
  static let textEnd = TextInputViewState.Selection(start: -1, end: -1)
}
