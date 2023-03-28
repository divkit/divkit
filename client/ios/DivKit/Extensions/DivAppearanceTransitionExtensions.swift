import CommonCorePublic
import LayoutKit

extension DivAppearanceTransition {
  func makeTransitioningAnimations(
    for type: TransitioningAnimationType,
    with expressionResolver: ExpressionResolver
  ) -> [TransitioningAnimation] {
    let kind: TransitioningAnimation.Kind
    let value1: Double
    let value2: Double
    let transition: DivTransitionBase
    switch self {
    case let .divAppearanceSetTransition(item):
      return item.items.flatMap {
        $0.makeTransitioningAnimations(for: type, with: expressionResolver)
      }
    case let .divFadeTransition(item):
      kind = .fade
      value1 = item.resolveAlpha(expressionResolver)
      value2 = 1
      transition = item
    case let .divScaleTransition(item):
      kind = .scaleXY
      value1 = item.resolveScale(expressionResolver)
      value2 = 1
      transition = item
    case let .divSlideTransition(item):
      let edge = item.resolveEdge(expressionResolver)
      switch edge {
      case .left, .right:
        kind = .translationX
      case .top, .bottom:
        kind = .translationY
      }
      value1 = item.distance?.makeScaledValue(expressionResolver).map {
        switch edge {
        case .left, .top:
          return -$0
        case .right, .bottom:
          return $0
        }
      } ?? getDefaultSlideValue(edge)
      value2 = 0
      transition = item
    }

    let animation = TransitioningAnimation(
      kind: kind,
      start: type == .appearing ? value1 : value2,
      end: type == .appearing ? value2 : value1,
      duration: Duration(milliseconds: transition.resolveDuration(expressionResolver)),
      delay: Delay(milliseconds: transition.resolveStartDelay(expressionResolver)),
      timingFunction: transition.resolveInterpolator(expressionResolver).asTimingFunction()
    )

    return [animation]
  }
}

enum TransitioningAnimationType {
  case appearing
  case disappearing
}

extension TransitioningAnimationType {
  func inverted() -> Self {
    switch self {
    case .appearing:
      return .disappearing
    case .disappearing:
      return .appearing
    }
  }
}

fileprivate func getDefaultSlideValue(_ edge: DivSlideTransition.Edge) -> Double {
  switch edge {
  case .left, .top:
    return TransitioningAnimation.defaultLeadingSlideDistance
  case .right, .bottom:
    return TransitioningAnimation.defaultTrailingSlideDistance
  }
}

extension DivDimension {
  fileprivate func makeScaledValue(_ expressionResolver: ExpressionResolver) -> Double? {
    guard let value = resolveValue(expressionResolver) else { return nil }
    return resolveUnit(expressionResolver).makeScaledValue(value)
  }
}
