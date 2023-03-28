import CoreGraphics

import CommonCorePublic

extension RelativeValue {
  init(double: Double) {
    self.init(rawValue: CGFloat(double))
  }
}
