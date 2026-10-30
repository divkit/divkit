import LayoutKit

struct ExtensionActionHandler {
  let pathResolver: ActionPathResolver
  let reporter: DivReporter
  let isCardUpdatePending: (DivCardID) -> Bool

  func handle(_ action: DivExtensionAction, context: DivActionHandlingContext) {
    let resolver = context.expressionResolver
    guard let divId = action.resolveDivId(resolver),
          let extensionId = action.resolveExtensionId(resolver) else {
      return
    }
    context.actionHandler.supersedePendingExtensionAction(
      id: divId, extensionId: extensionId, scopePath: context.scopePath, cardId: context.cardId
    )
    // Retained commands must not capture context.actionHandler and retain their own queue.
    let perform: (IdToPath.Target) -> Void = { [
      info = context.info,
      blockStateStorage = context.blockStateStorage,
      updateCard = context.updateCard
    ] target in
      guard let handler = target.extensionHandler?.actionHandler else {
        report("No action handler for extension '\(extensionId)'", info: info)
        return
      }
      let handled = handler.handleAction(
        context: DivExtensionActionContext(
          path: target.path,
          extensionId: extensionId,
          info: info,
          expressionResolver: resolver,
          blockStateStorage: blockStateStorage,
          updateCard: updateCard
        )
      )
      if !handled {
        report("Unsupported action for extension '\(extensionId)'", info: info)
      }
    }
    if isCardUpdatePending(context.cardId) {
      context.actionHandler.enqueuePendingAction(
        id: divId, extensionId: extensionId, scopePath: context.scopePath,
        cardId: context.cardId, sourcePath: context.sourcePath, apply: perform
      )
    } else {
      pathResolver.resolveTarget(
        id: divId, extensionId: extensionId, context: context, perform: perform
      )
    }
  }

  private func report(_ message: String, info: DivActionInfo) {
    reporter.reportError(
      cardId: info.cardId,
      error: DivUnknownError(message: message, path: info.path)
    )
  }
}
