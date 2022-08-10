import CoreGraphics
import QuartzCore

extension CornerRadii {
  var unifiedRadius: CGFloat? {
    let differentRadiuses = [
      topLeft,
      topRight,
      bottomLeft,
      bottomRight,
    ].filter { $0 != 0 }
      .uniqueElements
    if differentRadiuses.count > 1 {
      return nil
    } else {
      return differentRadiuses.first ?? 0
    }
  }

  var maskedCorners: CACornerMask {
    CACornerMask()
      .union(topLeft != 0 ? .layerMinXMinYCorner : [])
      .union(topRight != 0 ? .layerMaxXMinYCorner : [])
      .union(bottomLeft != 0 ? .layerMinXMaxYCorner : [])
      .union(bottomRight != 0 ? .layerMaxXMaxYCorner : [])
  }
}
