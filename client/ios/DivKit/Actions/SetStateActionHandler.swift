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

    let cardId = context.path.cardId
    stateUpdater.set(
      path: DivStatePath.makeDivStatePath(from: stateId),
      cardId: cardId,
      lifetime: .short
    )

    context.updateCard(.state(cardId))
  }
}
