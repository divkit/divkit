import UIKit
import VGSL

extension VisibleBoundsTracking {
  func visibilityHierarchyDepth(_ counter: Int = 0) -> Int {
    guard let view = self as? UIView else { return counter }
    let counter = self is VisibleBoundsTrackingContainer ? counter + 1 : counter
    var maxValue = counter

    for subview in view.subviews {
      let value = (subview as? VisibleBoundsTracking)?.visibilityHierarchyDepth(counter) ?? counter
      maxValue = max(maxValue, value)
    }

    return maxValue
  }
}
