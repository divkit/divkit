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

    handle(cardId: context.path.cardId, timerId: id, action: command.toDivTimerAction())
  }

  func handle(cardId: DivCardID, timerId: String, action: DivTimerAction) {
    performer(cardId, timerId, action)
  }
}

extension DivActionTimer.Action {
  func toDivTimerAction() -> DivTimerAction {
    switch self {
    case .start:
      .start
    case .stop:
      .stop
    case .pause:
      .pause
    case .resume:
      .resume
    case .cancel:
      .cancel
    case .reset:
      .reset
    }
  }
}
