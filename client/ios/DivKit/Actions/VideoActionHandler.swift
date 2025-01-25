import Foundation
import LayoutKit
import VGSL

final class VideoActionHandler {
  func handle(
    _ action: DivActionVideo,
    context: DivActionHandlingContext
  ) {
    let expressionResolver = context.expressionResolver

    guard let id = action.resolveId(expressionResolver),
          let command = action.resolveAction(expressionResolver) else {
      return
    }

    let cardId = context.path.cardId

    context.blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: VideoBlockViewState(state: command == .start ? .playing : .paused)
    )

    context.updateCard(.state(cardId))
  }
}
