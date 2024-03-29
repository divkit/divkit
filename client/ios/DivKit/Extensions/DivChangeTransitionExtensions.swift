import LayoutKit

extension DivChangeTransition {
  func resolveTransition(
    _ expressionResolver: ExpressionResolver
  ) -> ChangeBoundsTransition? {
    switch self {
    case let .divChangeBoundsTransition(transition):
      ChangeBoundsTransition(
        duration: Duration(milliseconds: transition.resolveDuration(expressionResolver)),
        delay: Delay(milliseconds: transition.resolveStartDelay(expressionResolver)),
        timingFunction: transition.resolveInterpolator(expressionResolver)
          .asTimingFunction()
      )
    case .divChangeSetTransition:
      nil
    }
  }
}
