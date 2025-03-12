import Foundation
import LayoutKit

protocol DivVisibilityActionBase: DivActionBase {
  var actionType: VisibilityActionType { get }

  func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool
  func resolveLogLimit(_ resolver: ExpressionResolver) -> Int
  func resolveLogId(_ resolver: ExpressionResolver) -> String?
  func resolveRequiredDuration(_ resolver: ExpressionResolver) -> Int
  func resolveVisibilityPercentage(_ resolver: ExpressionResolver) -> Int
}

extension DivVisibilityActionBase {
  public var logUrl: Expression<URL>? {
    nil
  }

  public func resolveLogUrl(_: ExpressionResolver) -> URL? {
    nil
  }
}

extension DivVisibilityActionBase {
  func makeVisibilityAction(
    context: DivBlockModelingContext
  ) -> VisibilityAction? {
    let expressionResolver = context.expressionResolver
    guard resolveIsEnabled(expressionResolver) else {
      return nil
    }
    let logLimitValue = resolveLogLimit(expressionResolver)
    let logId = resolveLogId(expressionResolver)
    let logIdValue = logId ?? ""
    let path = context.parentPath + actionType.rawValue + logIdValue

    let source: UserInterfaceAction.DivActionSource = switch actionType {
    case .appear: .visibility
    case .disappear: .disappear
    }

    return VisibilityAction(
      logId: logId,
      uiAction: UserInterfaceAction(
        payload: makeDivActionPayload(path: path, source: source),
        path: path
      ),
      requiredDuration: TimeInterval(
        resolveRequiredDuration(expressionResolver)
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
      actionType: actionType
    )
  }
}
