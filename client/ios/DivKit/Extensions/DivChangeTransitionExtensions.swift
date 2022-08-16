import CommonCore
import LayoutKit

extension DivChangeTransition {
  func makeChangeBoundsTransition(
    with expressionResolver: ExpressionResolver
  ) -> ChangeBoundsTransition? {
    switch self {
    case let .divChangeBoundsTransition(transition):
      return ChangeBoundsTransition(
        duration: Duration(
          milliseconds: transition.resolveDuration(expressionResolver)
        ) ?? 0.3,
        delay: Delay(milliseconds: transition.resolveStartDelay(expressionResolver)) ?? 0,
        timingFunction: transition.resolveInterpolator(expressionResolver)
          .asTimingFunction()
      )
    case .divChangeSetTransition:
      return nil
    }
  }
}
