import CoreGraphics
import Foundation

import CommonCorePublic

struct VisibilityCheckParam {
  let requiredVisibilityDuration: TimeInterval
  let targetPercentage: Int
  let limiter: VisibilityAction.Limiter
  let action: Action
}

public final class VisibilityActionPerformers {
  private let actionPerformers: [VisibilityActionPerformer]

  init(visibilityCheckParams: [VisibilityCheckParam]) {
    actionPerformers = visibilityCheckParams.map(VisibilityActionPerformer.init)
  }

  func onVisibleBoundsChanged(to: CGRect, bounds: CGRect) {
    let visibleAreaPercentage = bounds.isEmpty ? 0 : Int(to.area * 100 / bounds.area)
    for performer in actionPerformers {
      performer.onVisibleBoundsChanged(visibleAreaPercentage: visibleAreaPercentage)
    }
  }
}

extension VisibilityActionPerformer {
  fileprivate convenience init(visibilityCheckParam: VisibilityCheckParam) {
    self.init(
      requiredVisibilityDuration: visibilityCheckParam.requiredVisibilityDuration,
      targetVisibilityPercentage: visibilityCheckParam.targetPercentage,
      limiter: visibilityCheckParam.limiter,
      action: visibilityCheckParam.action
    )
  }
}

extension CGRect {
  fileprivate var area: CGFloat {
    width * height
  }
}
