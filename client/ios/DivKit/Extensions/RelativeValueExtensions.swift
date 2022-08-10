import CoreGraphics

import CommonCore

extension RelativeValue {
  init(double: Double) {
    self.init(rawValue: CGFloat(double))
  }
}
