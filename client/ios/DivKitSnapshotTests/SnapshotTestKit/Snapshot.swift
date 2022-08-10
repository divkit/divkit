import UIKit

extension UIView {
  public func makeSnapshot() -> UIImage? {
    window.apply { assert($0.safeAreaInsets == .zero) }
    assert(!bounds.isEmpty, "View bounds should not be empty")
    UIGraphicsBeginImageContextWithOptions(bounds.size, false, 0)
    layer.layoutIfNeeded()
    layer.render(in: UIGraphicsGetCurrentContext()!)
    let image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    removeFromSuperview()
    return image
  }
}
