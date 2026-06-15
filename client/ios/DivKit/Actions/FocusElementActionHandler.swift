import LayoutKit

final class FocusElementActionHandler {
  func handle(_ action: DivActionFocusElement, context: DivActionHandlingContext) {
    guard let elementId = action.resolveElementId(context.expressionResolver) else {
      return
    }

    let cardId = context.cardId
    if let previousCard = context.blockStateStorage.getFocusedElement()?.cardId,
       previousCard != cardId {
      context.updateCard(.state(previousCard))
    }

    context.blockStateStorage.setFocused(
      isFocused: true,
      element: IdAndCardId(id: elementId, cardId: cardId)
    )

    let state = TextInputViewState(
      pendingSelection: .textEnd
    )
    context.blockStateStorage.setPendingState(
      id: elementId,
      cardId: cardId,
      state: state
    )

    context.updateCard(.state(cardId))
  }
}

extension TextInputViewState.Selection {
  static let textEnd = TextInputViewState.Selection(start: -1, end: -1)
}
