// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

extension Color {
  public var cgColor: CGColor {
    let colorspace = CGColorSpaceCreateDeviceRGB()
    let components = [red, green, blue, alpha]
    return CGColor(colorSpace: colorspace, components: components)!
  }

  public var hexString: String {
    String(format: "#%02X%02X%02X%02X", intAlpha, intRed, intGreen, intBlue)
  }
}

extension Color: CustomDebugStringConvertible {
  public var debugDescription: String {
    hexString
  }
}

extension Color {
  public static let black = SystemColor.black.rgba
  public static let blue = SystemColor.blue.rgba
  public static let brown: Color = SystemColor.brown.rgba
  public static let clear: Color = SystemColor.clear.rgba
  public static let cyan: Color = SystemColor.cyan.rgba
  public static let darkGray: Color = SystemColor.darkGray.rgba
  public static let gray: Color = SystemColor.gray.rgba
  public static let green: Color = SystemColor.green.rgba
  public static let lightGray: Color = SystemColor.lightGray.rgba
  public static let magenta: Color = SystemColor.magenta.rgba
  public static let orange: Color = SystemColor.orange.rgba
  public static let purple: Color = SystemColor.purple.rgba
  public static let red: Color = SystemColor.red.rgba
  public static let white: Color = SystemColor.white.rgba
  public static let yellow: Color = SystemColor.yellow.rgba
}
