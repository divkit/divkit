import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivSizeUnit {
  func makeScaledValue(_ value: Int) -> CGFloat {
    CGFloat(value) / scale
  }

  func makeScaledValue(_ value: Double) -> CGFloat {
    CGFloat(value) / scale
  }

  private var scale: CGFloat {
    switch self {
    case .px:
      return PlatformDescription.screenScale()
    case .dp, .sp:
      return 1
    }
  }
}
