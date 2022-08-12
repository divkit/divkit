// Copyright 2022 Yandex LLC. All rights reserved.

import Foundation

import LayoutKit

extension DivFadeTransition {
  func makeAppearanceAnimation(
    with expressionResolver: ExpressionResolver
  ) -> TransitioningAnimation {
    TransitioningAnimation(
      kind: .fade,
      start: resolveAlpha(expressionResolver),
      end: 1,
      duration: Duration(milliseconds: resolveDuration(expressionResolver)) ?? 0.3,
      delay: Delay(milliseconds: resolveStartDelay(expressionResolver)) ?? 0,
      timingFunction: resolveInterpolator(expressionResolver).asTimingFunction()
    )
  }
}
