import UIKit

extension UIView {
  func makeSnapshot() -> UIImage? {
    layoutIfNeeded()

    UIGraphicsBeginImageContextWithOptions(bounds.size, false, 0)
    drawHierarchy(in: bounds, afterScreenUpdates: true)
    let image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return image
  }
}
