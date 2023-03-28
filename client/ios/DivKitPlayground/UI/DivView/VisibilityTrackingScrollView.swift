import UIKit

import BaseUIPublic
import CommonCorePublic

final class VisibilityTrackingScrollView: UIScrollView, VisibleBoundsTrackingContainer {
  private var previousVisibleBounds = CGRect.zero
  private var currentVisibleBounds = CGRect.zero
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    cardView.asArray()
  }

  var cardView: VisibilityTrackingCardView? {
    didSet {
      oldValue?.removeFromSuperview()
      cardView.map { addSubview($0) }
    }
  }

  override var bounds: CGRect {
    didSet {
      if bounds.size == oldValue.size {
        let newOrigin = currentVisibleBounds.origin + bounds.origin - oldValue.origin
        currentVisibleBounds = CGRect(origin: newOrigin, size: currentVisibleBounds.size)
      }
      passVisibleBoundsChanged(
        from: oldValue.intersection(previousVisibleBounds),
        to: bounds.intersection(currentVisibleBounds)
      )
      previousVisibleBounds = currentVisibleBounds
    }
  }
}

extension VisibilityTrackingScrollView: VisibleBoundsTracking {
  func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    currentVisibleBounds = to
    passVisibleBoundsChanged(from: from, to: to)
  }
}
