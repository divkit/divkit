import Foundation
import LayoutKit
import VGSL

struct VideoActionHandler {
  func handle(
    _ action: DivActionVideo,
    context: DivActionHandlingContext,
    pathResolver: ActionPathResolver
  ) {
    let expressionResolver = context.expressionResolver
    guard let id = action.resolveId(expressionResolver),
          let command = action.resolveAction(expressionResolver) else {
      return
    }

    let videoAction: DivVideoAction = switch command {
    case .start: .play
    case .pause: .pause
    }
    handle(id: id, action: videoAction, context: context, pathResolver: pathResolver)
  }

  func handle(
    id: String,
    action: DivVideoAction,
    context: DivActionHandlingContext,
    pathResolver: ActionPathResolver
  ) {
    pathResolver.resolve(id: id, context: context) { path in
      context.blockStateStorage.setState(
        path: path,
        state: VideoBlockViewState(state: action == .play ? .playing : .paused)
      )
      context.updateCard(.state(context.cardId))
    }
  }
}
