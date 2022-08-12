// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

extension Color {
  public static func colorWithRed(_ red: UInt8, green: UInt8, blue: UInt8) -> Color {
    Color(red: CGFloat(red) / 255, green: CGFloat(green) / 255, blue: CGFloat(blue) / 255, alpha: 1)
  }

  public static func color(withRGBValue value: Int) -> Color {
    let RGBValue = UInt32(value)
    let RGBAValue = (RGBValue << 8) | 0xFF
    return .colorWithHexCode(RGBAValue)
  }

  public func interpolate(to target: Color, progress: CGFloat) -> Color {
    Color(
      red: red.interpolated(to: target.red, progress: progress),
      green: green.interpolated(to: target.green, progress: progress),
      blue: blue.interpolated(to: target.blue, progress: progress),
      alpha: alpha.interpolated(to: target.alpha, progress: progress)
    )
  }

  public func withAlphaComponent(_ alpha: CGFloat) -> Color {
    Color(red: red, green: green, blue: blue, alpha: alpha)
  }

  public func setStroke() {
    systemColor.setStroke()
  }

  public func setFill() {
    systemColor.setFill()
  }
}
