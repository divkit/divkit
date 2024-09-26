import LayoutKit

final class FocusElementActionHandler {
  func handle(_ action: DivActionFocusElement, context: DivActionHandlingContext) {
    guard let elementId = action.resolveElementId(context.expressionResolver) else {
      return
    }

    context.blockStateStorage.setFocused(
      isFocused: true,
      element: IdAndCardId(id: elementId, cardId: context.path.cardId)
    )
  }
}
