import Foundation
import LayoutKit
import VGSL

extension DivAnimation {
  func resolveAnimations(
    expressionResolver: ExpressionResolver,
    animationNameToKind: (DivAnimation.Name) -> TransitioningAnimation.Kind?,
    defaultStartValue: (TransitioningAnimation.Kind) -> Double,
    defaultEndValue: (TransitioningAnimation.Kind) -> Double,
    invertResolvedStartAndEnd: Bool = false
  ) -> [TransitioningAnimation] {
    let name = resolveName(expressionResolver) ?? .noAnimation
    if name == .set {
      return (items ?? []).flatMap {
        $0.resolveAnimations(
          expressionResolver: expressionResolver,
          animationNameToKind: animationNameToKind,
          defaultStartValue: defaultStartValue,
          defaultEndValue: defaultEndValue,
          invertResolvedStartAndEnd: invertResolvedStartAndEnd
        )
      }
    }

    guard let kind = animationNameToKind(name) else {
      return []
    }

    let originalStart = self.resolveStartValue(expressionResolver)
    let originalEnd = self.resolveEndValue(expressionResolver)
    let startValue: Double?
    let endValue: Double?

    if invertResolvedStartAndEnd {
      startValue = originalEnd
      endValue = originalStart
    } else {
      startValue = originalStart
      endValue = originalEnd
    }

    let animation = TransitioningAnimation(
      kind: kind,
      start: startValue ?? defaultStartValue(kind),
      end: endValue ?? defaultEndValue(kind),
      duration: TimeInterval(milliseconds: resolveDuration(expressionResolver)),
      delay: TimeInterval(milliseconds: resolveStartDelay(expressionResolver)),
      timingFunction: resolveInterpolator(expressionResolver).asTimingFunction()
    )

    return [animation]
  }
}
