import Foundation
import LayoutKit

final class SetStateActionHandler {
  private let stateUpdater: DivStateUpdater

  init(stateUpdater: DivStateUpdater) {
    self.stateUpdater = stateUpdater
  }

  func handle(_ action: DivActionSetState, context: DivActionHandlingContext) {
    guard let stateId = action.resolveStateId(context.expressionResolver) else {
      return
    }

    handle(
      divStatePath: DivStatePath.makeDivStatePath(from: stateId),
      lifetime: .short,
      context: context
    )
  }

  func handle(
    divStatePath: DivStatePath,
    lifetime: DivStateLifetime,
    context: DivActionHandlingContext
  ) {
    let cardId = context.path.cardId
    stateUpdater.set(
      path: divStatePath,
      cardId: cardId,
      lifetime: lifetime
    )
    context.updateCard(.state(cardId))
  }
}
