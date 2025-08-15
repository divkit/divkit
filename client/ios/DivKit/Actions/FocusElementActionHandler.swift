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
    context.updateCard(.state(cardId))
  }
}
