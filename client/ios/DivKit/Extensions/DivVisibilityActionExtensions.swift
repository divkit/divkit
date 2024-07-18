import Foundation

import LayoutKit
import VGSL

extension DivVisibilityAction {
  func makeVisibilityAction(context: DivBlockModelingContext) -> VisibilityAction? {
    let expressionResolver = context.expressionResolver
    guard resolveIsEnabled(expressionResolver) else {
      return nil
    }
    let logLimitValue = resolveLogLimit(expressionResolver)
    let path = context.parentPath + (resolveLogId(expressionResolver) ?? "")
    return VisibilityAction(
      uiAction: UserInterfaceAction(
        payload: makeDivActionPayload(path: context.parentPath, source: .visibility),
        path: path
      ),
      requiredDuration: TimeInterval(
        resolveVisibilityDuration(expressionResolver)
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
      actionType: .appear
    )
  }
}

extension DivVisibilityAction: DivActionBase {
  public var logUrl: Expression<URL>? {
    nil
  }

  public func resolveLogUrl(_: ExpressionResolver) -> URL? {
    nil
  }
}
