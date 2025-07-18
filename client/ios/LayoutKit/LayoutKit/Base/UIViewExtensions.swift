#if os(iOS)
import UIKit
import VGSL

extension UIView {
  /// Unlinks the view from a given view if it is a superview of the view
  public func removeFrom(_ view: UIView) {
    self.superview.map {
      guard $0 === view else { return }

      self.removeFromSuperview()
    }
  }
}

extension UIView {
  func applyAccessibilityFromScratch(_ element: AccessibilityElement?) {
    isAccessibilityElement = false
    accessibilityLabel = nil
    accessibilityTraits = UIAccessibilityTraits()
    accessibilityValue = nil
    accessibilityHint = nil

    applyAccessibility(element)
  }
}
#endif
