import LayoutKit

final class FocusElementActionHandler {
  func handle(_ action: DivActionFocusElement, context: DivActionHandlingContext) {
    guard let elementId = action.resolveElementId(context.expressionResolver) else {
      return
    }

    if let previousCard = context.blockStateStorage.getFocusedElement()?.cardId {
      context.updateCard(.state(previousCard))
    }
    context.blockStateStorage.setFocused(
      isFocused: true,
      element: IdAndCardId(id: elementId, cardId: context.cardId)
    )
    context.updateCard(.state(context.cardId))
  }
}
