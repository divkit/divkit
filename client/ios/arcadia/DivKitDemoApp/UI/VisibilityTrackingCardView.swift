import UIKit

import CommonCore

final class VisibilityTrackingCardView: UIView, VisibleBoundsTrackingContainer {
  var blockView: VisibleBoundsTrackingView? {
    didSet {
      oldValue?.removeFromSuperview()
      blockView.map { addSubview($0) }
    }
  }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { blockView.asArray() }
}
