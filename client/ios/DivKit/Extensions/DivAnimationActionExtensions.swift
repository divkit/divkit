import LayoutKit

extension DivAnimation {
  func resolveActionAnimation(_ expressionResolver: ExpressionResolver) -> ActionAnimation {
    ActionAnimation(
      touchDown: resolveActionAnimations(expressionResolver, type: .direct),
      touchUp: resolveActionAnimations(expressionResolver, type: .reverse)
    )
  }

  private func resolveActionAnimations(
    _ expressionResolver: ExpressionResolver,
    type: TimeOrientation
  ) -> [TransitioningAnimation] {
    resolveAnimations(
      expressionResolver: expressionResolver,
      animationNameToKind: { $0.kind },
      defaultStartValue: {
        $0.defaultStartValue(for: type)
      },
      defaultEndValue: {
        $0.defaultEndValue(for: type)
      },
      invertResolvedStartAndEnd: type == .reverse
    )
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
