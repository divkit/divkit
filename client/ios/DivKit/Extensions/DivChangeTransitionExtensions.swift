// Copyright 2022 Yandex LLC. All rights reserved.

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
      // TODO: support transition sets when needed (https://st.yandex-team.ru/ALICEKITIOS-2516)
      return nil
    }
  }
}
