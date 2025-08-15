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
        action: { [weak actionSender] in
          #if os(iOS)
          guard let actionSender else { return }
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

    let visibleAreaPercentageAfter =
      (!visibilityParams.isVisible || bounds.isEmpty) ?
      0 : min(Int(to.area * 100 / bounds.area), 100)

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
