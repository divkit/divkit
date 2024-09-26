import Foundation
import LayoutKit
import VGSL

final class TimerActionHandler {
  private let performer: DivActionURLHandler.PerformTimerAction
  
  init (performer: @escaping DivActionURLHandler.PerformTimerAction) {
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
    
    performer(context.path.cardId, id, command.toDivTimerAction())
  }
}

extension DivActionTimer.Action {
  func toDivTimerAction() -> DivTimerAction {
    switch self {
    case .start:
      return .start
    case .stop:
      return .stop
    case .pause:
      return .pause
    case .resume:
      return .resume
    case .cancel:
      return .cancel
    case .reset:
      return .reset
    }
  }
}
