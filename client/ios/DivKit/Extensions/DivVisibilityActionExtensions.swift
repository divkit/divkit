import Foundation

import CommonCorePublic
import LayoutKit

extension DivVisibilityAction {
  func makeVisibilityAction(
    context: DivBlockModelingContext,
    logId: String
  ) -> VisibilityAction? {
    guard let uiAction = uiAction(context: context, logId: logId) else {
      return nil
    }

    let expressionResolver = context.expressionResolver
    let logLimitValue = resolveLogLimit(expressionResolver)
    return VisibilityAction(
      uiAction: uiAction,
      requiredDuration: TimeInterval(
        resolveVisibilityDuration(expressionResolver)
      ) / 1000,
      targetPercentage: resolveVisibilityPercentage(expressionResolver),
      limiter: ActionLimiter(
        canSend: {
          logLimitValue == 0
            || context.visibilityCounter.visibilityCount(for: uiAction.path) < logLimitValue
        },
        markSent: {
          context.visibilityCounter.incrementCount(for: uiAction.path)
        }
      ),
      actionType: .appear
    )
  }

  private func uiAction(
    context: DivBlockModelingContext,
    logId: String
  ) -> UserInterfaceAction? {
    let payloads = [
      makeDivActionPayload(cardId: context.cardId, source: .visibility),
      makeJsonPayload(),
    ].compactMap { $0 }
    if payloads.isEmpty {
      return nil
    }
    return UserInterfaceAction(
      payloads: payloads,
      path: context.parentPath + logId
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
