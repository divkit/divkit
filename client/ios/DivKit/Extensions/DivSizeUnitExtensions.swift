import CoreGraphics
import LayoutKit
import VGSL

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
      PlatformDescription.screenScale()
    case .dp, .sp:
      1
    }
  }
}
