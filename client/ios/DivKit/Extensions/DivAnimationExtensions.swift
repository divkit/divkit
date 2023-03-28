import CommonCorePublic
import LayoutKit

extension DivAnimation {
  func makeActionAnimation(with expressionResolver: ExpressionResolver) -> ActionAnimation {
    ActionAnimation(
      touchDown: makeActionAnimation(for: .direct, with: expressionResolver),
      touchUp: makeActionAnimation(for: .reverse, with: expressionResolver)
    )
  }

  private func makeActionAnimation(
    for type: TimeOrientation,
    with expressionResolver: ExpressionResolver
  ) -> [TransitioningAnimation] {
    let name = resolveName(expressionResolver) ?? .noAnimation
    if name == .set {
      return (items ?? []).flatMap {
        $0.makeActionAnimation(for: type, with: expressionResolver)
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
    case .linear: return .linear
    case .easeIn: return .easeIn
    case .easeOut: return .easeOut
    case .easeInOut, .ease, .spring: return .easeInEaseOut
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
    case .fade: return .fade
    case .scale: return .scaleXY
    case .set, .translate, .noAnimation, .native: return nil
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
    case .direct: return .reverse
    case .reverse: return .direct
    }
  }
}
