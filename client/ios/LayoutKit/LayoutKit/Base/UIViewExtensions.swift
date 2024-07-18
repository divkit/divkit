
import UIKit

extension UIView {
  /// Unlinks the view from a given view if it is a superview of the view
  public func removeFrom(_ view: UIView) {
    self.superview.map {
      guard $0 === view else { return }

      self.removeFromSuperview()
    }
  }
}
