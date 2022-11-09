import UIKit

extension UIView {
  public func makeSnapshot() -> UIImage? {
    window.apply { assert($0.safeAreaInsets == .zero) }
    assert(!bounds.isEmpty, "View bounds should not be empty")
    UIGraphicsBeginImageContextWithOptions(bounds.size, false, 0)
    layer.layoutIfNeeded()
    drawHierarchy(in: bounds, afterScreenUpdates: true)
    let image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    removeFromSuperview()
    return image
  }
}
