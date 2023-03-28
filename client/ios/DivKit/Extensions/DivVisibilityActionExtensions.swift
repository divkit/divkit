import Foundation

import CommonCorePublic
import LayoutKit

extension DivVisibilityAction {
  func makeVisibilityAction(
    context: DivBlockModelingContext,
    index: Int
  ) -> VisibilityAction? {
    guard let uiAction = uiAction(context: context, index: index) else {
      return nil
    }

    let expressionResolver = context.expressionResolver
    let logLimitValue = resolveLogLimit(expressionResolver)
    return VisibilityAction(
      uiAction: uiAction,
      requiredVisibilityDuration: TimeInterval(
        resolveVisibilityDuration(expressionResolver)
      ) / 1000,
      targetPercentage: resolveVisibilityPercentage(expressionResolver),
      limiter: VisibilityAction.Limiter(
        canSend: {
          logLimitValue == 0
            || context.visibilityCounter.visibilityCount(for: uiAction.path) < logLimitValue
        },
        markSent: {
          context.visibilityCounter.incrementCount(for: uiAction.path)
        }
      )
    )
  }

  private func uiAction(
    context: DivBlockModelingContext,
    index: Int
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
      path: context.parentPath + index
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
