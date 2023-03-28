import UIKit

import BaseUIPublic
import CommonCorePublic

public final class VisibleBoundsTrackingCollectionView: NoContentTouchDelaysCollectionView,
  VisibleBoundsTrackingContainer {
  private var previousVisibleBounds = CGRect.zero
  private var currentVisibleBounds = CGRect.zero
  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    subviews.compactMap { $0 as? VisibleBoundsTrackingView }
  }

  public override var bounds: CGRect {
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

extension VisibleBoundsTrackingCollectionView: VisibleBoundsTracking {
  public func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    currentVisibleBounds = to
    passVisibleBoundsChanged(from: from, to: to)
  }
}
