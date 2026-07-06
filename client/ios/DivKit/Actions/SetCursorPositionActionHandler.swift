import LayoutKit

struct SetCursorPositionActionHandler {
  private let pathResolver: ActionPathResolver

  init(pathResolver: ActionPathResolver) {
    self.pathResolver = pathResolver
  }

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

    pathResolver.resolve(id: id, context: context) { path in
      context.blockStateStorage.setPendingState(path, state: state)
      context.blockStateStorage.setFocused(isFocused: true, path: path)
      context.updateCard(.state(context.cardId))
    }
  }
}
