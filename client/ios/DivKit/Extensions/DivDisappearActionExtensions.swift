import Foundation

import CommonCorePublic
import LayoutKit

extension DivDisappearAction {
  func makeDisappearAction(context: DivBlockModelingContext) -> VisibilityAction? {
    let expressionResolver = context.expressionResolver
    guard resolveIsEnabled(expressionResolver) else {
      return nil
    }
    let logLimitValue = resolveLogLimit(expressionResolver)
    let path = context.parentPath + (resolveLogId(expressionResolver) ?? "")
    return VisibilityAction(
      uiAction: UserInterfaceAction(
        payload: makeDivActionPayload(path: context.parentPath, source: .disappear),
        path: path
      ),
      requiredDuration: TimeInterval(
        resolveDisappearDuration(expressionResolver)
      ) / 1000,
      targetPercentage: resolveVisibilityPercentage(expressionResolver),
      limiter: ActionLimiter(
        canSend: {
          logLimitValue == 0
            || context.visibilityCounter.visibilityCount(for: path) < logLimitValue
        },
        markSent: {
          context.visibilityCounter.incrementCount(for: path)
        }
      ),
      actionType: .disappear
    )
  }
}

extension DivDisappearAction: DivActionBase {
  public var logUrl: Expression<URL>? {
    nil
  }

  public func resolveLogUrl(_: ExpressionResolver) -> URL? {
    nil
  }
}
