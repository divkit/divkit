import DivKit

struct LottieExtensionActionHandler: DivExtensionActionHandler {
  func handleAction(context: DivExtensionActionContext) -> Bool {
    guard let type = context.info.payload?["type"] as? String,
          let resolvedType = context.expressionResolver.resolveString(type),
          let command = LottiePlaybackState.Command(rawValue: resolvedType) else {
      return false
    }
    let current: LottiePlaybackState? = context.getState()
    context.setState(LottiePlaybackState(command: command, sequence: (current?.sequence ?? 0) &+ 1))
    return true
  }
}
