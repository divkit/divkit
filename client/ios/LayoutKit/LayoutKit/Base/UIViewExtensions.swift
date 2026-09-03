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
    guard let element else {
      isAccessibilityElement = false
      accessibilityLabel = nil
      accessibilityTraits = UIAccessibilityTraits()
      accessibilityValue = nil
      accessibilityHint = nil
      return
    }
    // Reset fields that `applyAccessibility` overlays additively so that stale
    // data from a previous element (e.g. a removed `.button` trait or a removed
    // hint/value) is not preserved when transitioning between two non-nil
    // elements.  We intentionally do NOT toggle `isAccessibilityElement` to
    // `false` here to avoid a transient VoiceOver interruption.
    accessibilityTraits = UIAccessibilityTraits()
    accessibilityHint = nil
    accessibilityValue = nil
    applyAccessibility(element)
  }
}
#endif
