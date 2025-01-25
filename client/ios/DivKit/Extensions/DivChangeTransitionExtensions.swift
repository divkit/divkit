import Foundation
import LayoutKit

extension DivChangeTransition {
  func resolveTransition(
    _ expressionResolver: ExpressionResolver
  ) -> ChangeBoundsTransition? {
    switch self {
    case let .divChangeBoundsTransition(transition):
      ChangeBoundsTransition(
        duration: TimeInterval(milliseconds: transition.resolveDuration(expressionResolver)),
        delay: TimeInterval(milliseconds: transition.resolveStartDelay(expressionResolver)),
        timingFunction: transition.resolveInterpolator(expressionResolver)
          .asTimingFunction()
      )
    case .divChangeSetTransition:
      nil
    }
  }
}
