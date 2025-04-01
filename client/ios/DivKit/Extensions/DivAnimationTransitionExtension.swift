import Foundation
import LayoutKit

extension DivAnimation {
  func makeTransitioningAnimations(
    for type: TransitioningAnimationType,
    with expressionResolver: ExpressionResolver
  ) -> [TransitioningAnimation] {
    resolveAnimations(
      expressionResolver: expressionResolver,
      animationNameToKind: { $0.kind },
      defaultStartValue: { kind in
        kind.defaultStartValue(for: type)
      },
      defaultEndValue: { kind in
        kind.defaultEndValue(for: type)
      }
    )
  }
}

extension TransitioningAnimation.Kind {
  fileprivate func defaultStartValue(for type: TransitioningAnimationType) -> Double {
    switch (self, type) {
    case (.fade, .appearing), (.scaleXY, .appearing):
      return 0
    case (.fade, .disappearing), (.scaleXY, .disappearing):
      return 1
    case (.translationY, .appearing):
      return TransitioningAnimation.defaultTrailingSlideDistance
    case (.translationY, .disappearing):
      return 0
    case (.translationX, _):
      assertionFailure()
      return 0
    }
  }

  fileprivate func defaultEndValue(for type: TransitioningAnimationType) -> Double {
    defaultStartValue(for: type.inverted())
  }
}

extension DivAnimation.Name {
  fileprivate var kind: TransitioningAnimation.Kind? {
    switch self {
    case .fade: .fade
    case .scale: .scaleXY
    case .translate: .translationY
    case .set, .noAnimation, .native: nil
    }
  }
}
