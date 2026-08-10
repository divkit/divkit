import Foundation
import LayoutKit
import VGSL

final class TimerActionHandler {
  private let performer: DivActionHandler.PerformTimerAction

  init(performer: @escaping DivActionHandler.PerformTimerAction) {
    self.performer = performer
  }

  func handle(
    _ action: DivActionTimer,
    context: DivActionHandlingContext
  ) {
    let expressionResolver = context.expressionResolver
    guard let id = action.resolveId(expressionResolver),
          let command = action.resolveAction(expressionResolver) else {
      return
    }

    handle(cardId: context.cardId, timerId: id, action: command)
  }

  func handle(cardId: DivCardID, timerId: String, action: DivActionTimer.Action) {
    performer(cardId, timerId, action)
  }
}
