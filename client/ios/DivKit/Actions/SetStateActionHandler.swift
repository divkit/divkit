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

    guard let statePath = DivStatePath.makeDivStatePath(from: stateId) else {
      DivKitLogger.error("Invalid state id: \(stateId)")
      return
    }

    let cardId = context.path.cardId
    stateUpdater.set(
      path: statePath,
      cardId: cardId,
      lifetime: .short
    )

    context.updateCard(.state(cardId))
  }
}
