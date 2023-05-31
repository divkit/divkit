import CoreGraphics
import Foundation

public final class VisibilityActionPerformers {
  private let actionPerformers: [VisibilityActionPerformer]

  init(visibilityCheckParams: [VisibilityCheckParam]) {
    actionPerformers = visibilityCheckParams.map(VisibilityActionPerformer.init)
  }

  func onVisibleBoundsChanged(from: CGRect, to: CGRect, bounds: CGRect) {
    let visibleAreaPercentageBefore = bounds.isEmpty ? 0 : Int(from.area * 100 / bounds.area)
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
  fileprivate convenience init(visibilityCheckParam: VisibilityCheckParam) {
    self.init(
      requiredDuration: visibilityCheckParam.requiredDuration,
      targetVisibilityPercentage: visibilityCheckParam.targetPercentage,
      limiter: visibilityCheckParam.limiter,
      action: visibilityCheckParam.action,
      type: visibilityCheckParam.type
    )
  }
}

extension CGRect {
  fileprivate var area: CGFloat {
    width * height
  }
}
