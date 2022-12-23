// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

public struct RGBAColor {
  public let red: CGFloat
  public let green: CGFloat
  public let blue: CGFloat
  public let alpha: CGFloat

  public init(
    red: CGFloat,
    green: CGFloat,
    blue: CGFloat,
    alpha: CGFloat
  ) {
    red.assertIsNormalized()
    green.assertIsNormalized()
    blue.assertIsNormalized()
    alpha.assertIsNormalized()
    self.red = red
    self.green = green
    self.blue = blue
    self.alpha = alpha
  }

  public init(white: CGFloat, alpha: CGFloat) {
    self.init(red: white, green: white, blue: white, alpha: alpha)
  }

  public var intRed: UInt8 {
    UInt8(normalized: red)
  }

  public var intGreen: UInt8 {
    UInt8(normalized: green)
  }

  public var intBlue: UInt8 {
    UInt8(normalized: blue)
  }

  public var intAlpha: UInt8 {
    UInt8(normalized: alpha)
  }

  public var systemColor: SystemColor {
    SystemColor(red: red, green: green, blue: blue, alpha: alpha)
  }

  public static func colorWithHexCode(_ hex: UInt32) -> RGBAColor {
    let red = UInt8(hex >> 24)
    let green = UInt8((hex & 0x00_FF_00_00) >> 16)
    let blue = UInt8((hex & 0x00_00_FF_00) >> 8)
    let alpha = UInt8(hex & 0x00_00_00_FF)

    return RGBAColor(
      red: CGFloat(red) / 255,
      green: CGFloat(green) / 255,
      blue: CGFloat(blue) / 255,
      alpha: CGFloat(alpha) / 255
    )
  }
}

extension CGFloat {
  fileprivate func assertIsNormalized() {
    assert(self >= 0 && self <= 1)
  }
}

extension RGBAColor: Equatable {
  public static func ==(lhs: RGBAColor, rhs: RGBAColor) -> Bool {
    lhs.intRed == rhs.intRed
      && lhs.intGreen == rhs.intGreen
      && lhs.intBlue == rhs.intBlue
      && lhs.intAlpha == rhs.intAlpha
  }
}

extension RGBAColor: Hashable {
  public func hash(into hasher: inout Hasher) {
    red.hash(into: &hasher)
    green.hash(into: &hasher)
    blue.hash(into: &hasher)
    alpha.hash(into: &hasher)
  }
}

extension UInt8 {
  fileprivate init(normalized source: CGFloat) {
    self.init(round(source * CGFloat(UInt8.max)))
  }
}
