import CommonCorePublic
import LayoutKit

extension DivChangeTransition {
  func makeChangeBoundsTransition(
    with expressionResolver: ExpressionResolver
  ) -> ChangeBoundsTransition? {
    switch self {
    case let .divChangeBoundsTransition(transition):
      return ChangeBoundsTransition(
        duration: Duration(milliseconds: transition.resolveDuration(expressionResolver)),
        delay: Delay(milliseconds: transition.resolveStartDelay(expressionResolver)),
        timingFunction: transition.resolveInterpolator(expressionResolver)
          .asTimingFunction()
      )
    case .divChangeSetTransition:
      return nil
    }
  }
}
