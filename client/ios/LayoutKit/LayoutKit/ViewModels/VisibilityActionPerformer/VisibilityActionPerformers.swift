import CoreGraphics
import Foundation

import VGSL

final class VisibilityActionPerformers {
  private let visibilityParams: VisibilityParams
  private let actionPerformers: [VisibilityActionPerformer]

  init(
    visibilityParams: VisibilityParams,
    actionSender: ViewType
  ) {
    self.visibilityParams = visibilityParams

    actionPerformers = visibilityParams.actions.map { action in
      VisibilityActionPerformer(
        requiredDuration: action.requiredDuration,
        targetVisibilityPercentage: action.targetPercentage,
        limiter: action.limiter,
        action: { [unowned actionSender] in
          #if os(iOS)
          UIActionEvent(
            uiAction: action.uiAction,
            originalSender: actionSender
          ).sendFrom(actionSender)
          #endif
        },
        type: action.actionType,
        timerScheduler: visibilityParams.scheduler
      )
    }
  }

  func onVisibleBoundsChanged(to: CGRect, bounds: CGRect) {
    let visibleAreaPercentageBefore = visibilityParams.lastVisibleArea.value
    let visibleAreaPercentageAfter: Int = if bounds.isEmpty {
      visibilityParams.isVisible ? 100 : 0
    } else {
      min(Int(to.area * 100 / bounds.area), 100)
    }

    guard visibleAreaPercentageAfter != visibleAreaPercentageBefore else {
      return
    }

    visibilityParams.lastVisibleArea.value = visibleAreaPercentageAfter

    for performer in actionPerformers {
      performer.onVisibleBoundsChanged(
        visibleAreaPercentageBefore: visibleAreaPercentageBefore,
        visibleAreaPercentageAfter: visibleAreaPercentageAfter
      )
    }
  }
}

extension CGRect {
  fileprivate var area: CGFloat {
    width * height
  }
}
