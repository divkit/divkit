import CoreGraphics
import Foundation

import BasePublic

public final class VisibilityActionPerformers {
  private let actionPerformers: [VisibilityActionPerformer]
  private let lastVisibleArea: Property<Int>

  init(
    visibilityCheckParams: [VisibilityCheckParam],
    lastVisibleArea: Property<Int>,
    scheduling: Scheduling
  ) {
    self.lastVisibleArea = lastVisibleArea
    actionPerformers = visibilityCheckParams.map {
      VisibilityActionPerformer(visibilityCheckParam: $0, scheduling: scheduling)
    }
  }

  func onVisibleBoundsChanged(to: CGRect, bounds: CGRect) {
    let visibleAreaPercentageBefore = lastVisibleArea.value
    let visibleAreaPercentageAfter = bounds.isEmpty ? 100 : Int(to.area * 100 / bounds.area)
    lastVisibleArea.value = visibleAreaPercentageAfter

    for performer in actionPerformers {
      performer.onVisibleBoundsChanged(
        visibleAreaPercentageBefore: visibleAreaPercentageBefore,
        visibleAreaPercentageAfter: visibleAreaPercentageAfter
      )
    }
  }
}

extension VisibilityActionPerformer {
  fileprivate convenience init(visibilityCheckParam: VisibilityCheckParam, scheduling: Scheduling) {
    self.init(
      requiredDuration: visibilityCheckParam.requiredDuration,
      targetVisibilityPercentage: visibilityCheckParam.targetPercentage,
      limiter: visibilityCheckParam.limiter,
      action: visibilityCheckParam.action,
      type: visibilityCheckParam.type,
      timerScheduler: scheduling
    )
  }
}

extension CGRect {
  fileprivate var area: CGFloat {
    width * height
  }
}
