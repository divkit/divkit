// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import CoreImage

extension CGColor {
  public static func fromHex(_ hex: UInt32) -> CGColor {
    RGBAColor.colorWithHexCode(hex).cgColor
  }

  public static func fromRGBA(_ color: RGBAColor) -> CGColor {
    color.cgColor
  }

  public func multiplyingRGBComponents(by factor: CGFloat) -> CGColor {
    let color = CIColor(cgColor: self)
    let red = min(max(color.red * factor, 0), 1)
    let green = min(max(color.green * factor, 0), 1)
    let blue = min(max(color.blue * factor, 0), 1)
    let alpha = color.alpha
    let components = [red, green, blue, alpha]
    return CGColor(colorSpace: CGColorSpaceCreateDeviceRGB(), components: components)!
  }
}
