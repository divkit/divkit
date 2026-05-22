import LayoutKit

final class SetCursorPositionActionHandler {
  func handle(_ action: DivActionSetCursorPosition, context: DivActionHandlingContext) {
    let resolver = context.expressionResolver
    guard let id = action.resolveId(resolver),
          let start = action.position.resolveStart(resolver) else {
      DivKitLogger.error("SetCursorPosition action: id or start position could not be resolved")
      return
    }

    let end = action.position.resolveEnd(resolver) ?? start

    let state = TextInputViewState(
      pendingSelection: TextInputViewState.Selection(start: start, end: end)
    )
    context.blockStateStorage.setPendingState(id: id, cardId: context.cardId, state: state)
    context.blockStateStorage.setFocused(isFocused: true, element: IdAndCardId(id: id, cardId: context.cardId))
    context.updateCard(.state(context.cardId))
  }
}
