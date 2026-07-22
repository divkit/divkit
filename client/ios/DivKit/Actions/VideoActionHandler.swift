import Foundation
import LayoutKit
import VGSL

struct VideoActionHandler {
  private let pathResolver: ActionPathResolver

  init(pathResolver: ActionPathResolver) {
    self.pathResolver = pathResolver
  }

  func handle(
    _ action: DivActionVideo,
    context: DivActionHandlingContext
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

    handle(id: id, action: videoAction, context: context)
  }

  func handle(
    id: String,
    action: DivVideoAction,
    context: DivActionHandlingContext
  ) {
    pathResolver.resolve(id: id, divTypes: [DivVideo.type], context: context) { path in
      context.blockStateStorage.setState(
        path: path,
        state: VideoBlockViewState(state: action == .play ? .playing : .paused)
      )
      context.updateCard(.state(context.cardId))
    }
  }
}
