import CoreGraphics

import VGSL

extension RelativeValue {
  init(double: Double) {
    self.init(rawValue: CGFloat(double))
  }
}
