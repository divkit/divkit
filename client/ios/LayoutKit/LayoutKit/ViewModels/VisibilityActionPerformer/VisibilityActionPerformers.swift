import CoreGraphics
import Foundation

import BasePublic

public final class VisibilityActionPerformers {
  private let actionPerformers: [VisibilityActionPerformer]
  private let lastVisibleBounds: Property<CGRect>

  init(
    visibilityCheckParams: [VisibilityCheckParam],
    lastVisibleBounds: Property<CGRect>,
    scheduling: Scheduling
  ) {
    self.lastVisibleBounds = lastVisibleBounds
    actionPerformers = visibilityCheckParams.map {
      VisibilityActionPerformer(visibilityCheckParam: $0, scheduling: scheduling)
    }
  }

  func onVisibleBoundsChanged(to: CGRect, bounds: CGRect) {
    let beforeVisibleBounds = lastVisibleBounds.value
    lastVisibleBounds.value = to

    let visibleAreaPercentageBefore = bounds
      .isEmpty ? 0 : Int(beforeVisibleBounds.area * 100 / bounds.area)
    let visibleAreaPercentageAfter = bounds.isEmpty ? 0 : Int(to.area * 100 / bounds.area)

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
