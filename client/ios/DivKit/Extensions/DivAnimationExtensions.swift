import LayoutKit
import VGSL

extension DivAnimation {
  func resolveActionAnimation(_ expressionResolver: ExpressionResolver) -> ActionAnimation {
    ActionAnimation(
      touchDown: resolveAnimations(expressionResolver, type: .direct),
      touchUp: resolveAnimations(expressionResolver, type: .reverse)
    )
  }

  private func resolveAnimations(
    _ expressionResolver: ExpressionResolver,
    type: TimeOrientation
  ) -> [TransitioningAnimation] {
    let name = resolveName(expressionResolver) ?? .noAnimation
    if name == .set {
      return (items ?? []).flatMap {
        $0.resolveAnimations(expressionResolver, type: type)
      }
    }

    guard let kind = name.kind else {
      return []
    }

    let originalStart = self.resolveStartValue(expressionResolver)
    let originalEnd = self.resolveEndValue(expressionResolver)
    let startValue: Double?
    let endValue: Double?
    switch type {
    case .direct:
      startValue = originalStart
      endValue = originalEnd
    case .reverse:
      startValue = originalEnd
      endValue = originalStart
    }

    let animation = TransitioningAnimation(
      kind: kind,
      start: startValue ?? kind.defaultStartValue(for: type),
      end: endValue ?? kind.defaultEndValue(for: type),
      duration: Duration(milliseconds: resolveDuration(expressionResolver)),
      delay: Delay(milliseconds: resolveStartDelay(expressionResolver)),
      timingFunction: resolveInterpolator(expressionResolver).asTimingFunction()
    )

    return [animation]
  }
}

extension DivAnimationInterpolator {
  func asTimingFunction() -> TimingFunction {
    switch self {
    case .linear: .linear
    case .easeIn: .easeIn
    case .easeOut: .easeOut
    case .easeInOut, .ease, .spring: .easeInEaseOut
    }
  }
}

private enum TimeOrientation {
  case direct
  case reverse
}

extension DivAnimation.Name {
  fileprivate var kind: TransitioningAnimation.Kind? {
    switch self {
    case .fade: .fade
    case .scale: .scaleXY
    case .set, .translate, .noAnimation, .native: nil
    }
  }
}

extension TransitioningAnimation.Kind {
  fileprivate func defaultStartValue(for type: TimeOrientation) -> Double {
    switch (self, type) {
    case (.fade, .direct):
      return 1
    case (.fade, .reverse):
      return 0.6
    case (.scaleXY, .direct):
      return 1
    case (.scaleXY, .reverse):
      return 0.95
    case (.translationX, _), (.translationY, _):
      assertionFailure()
      return 0
    }
  }

  fileprivate func defaultEndValue(for type: TimeOrientation) -> Double {
    defaultStartValue(for: type.inverted())
  }
}

extension TimeOrientation {
  fileprivate func inverted() -> Self {
    switch self {
    case .direct: .reverse
    case .reverse: .direct
    }
  }
}
