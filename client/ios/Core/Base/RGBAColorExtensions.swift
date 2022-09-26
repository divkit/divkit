// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import CoreImage

extension RGBAColor {
  private enum HexStringFormat: Int {
    case rgb = 3
    case argb = 4
    case rrggbb = 6
    case aarrggbb = 8
  }

  public static func colorWithARGBHexCode(_ argb: UInt32) -> RGBAColor {
    let alphaFirstColorValue = argb
    let alphaValue = (alphaFirstColorValue & 0xFF_00_00_00) >> 24
    let alphaLastColorValue = alphaFirstColorValue << 8 | alphaValue
    return colorWithHexCode(alphaLastColorValue)
  }

  public static func color(withHexString hexString: String) -> RGBAColor? {
    guard hexString.hasPrefix("#") else { return nil }
    // exclude hash
    let colorValue = hexString.dropFirst()
    guard let format = HexStringFormat(rawValue: colorValue.count) else { return nil }
    do {
      let argb: UInt32
      switch format {
      case .rgb:
        argb = try colorValueForShortFormat(colorValue) | 0xFF_00_00_00
      case .argb:
        argb = try colorValueForShortFormat(colorValue)
      case .rrggbb:
        argb = try colorValueForFullFormat(colorValue) | 0xFF_00_00_00
      case .aarrggbb:
        argb = try colorValueForFullFormat(colorValue)
      }
      return colorWithARGBHexCode(argb)
    } catch {
      return nil
    }
  }

  public func opaqueColor(forBackgroundColor backgroundColor: RGBAColor) -> RGBAColor {
    assert(backgroundColor.alpha.isApproximatelyEqualTo(1), "Background color must be opaque")
    return RGBAColor(
      red: (1 - alpha) * backgroundColor.red + alpha * red,
      green: (1 - alpha) * backgroundColor.green + alpha * green,
      blue: (1 - alpha) * backgroundColor.blue + alpha * blue,
      alpha: 1
    )
  }

  public func multiplyingRGBComponents(by factor: CGFloat) -> RGBAColor {
    let red = (self.red * factor).clamp(0...1)
    let green = (self.green * factor).clamp(0...1)
    let blue = (self.blue * factor).clamp(0...1)
    return RGBAColor(red: red, green: green, blue: blue, alpha: alpha)
  }

  public var ciColor: CIColor {
    CIColor(
      red: self.red,
      green: self.green,
      blue: self.blue,
      alpha: self.alpha
    )
  }

  public var argbString: String {
    // ARGB format - according to RGBAColor.color(withHexString:)
    String(format: "#%02X%02X%02X%02X", intAlpha, intRed, intGreen, intBlue)
  }
}

extension RGBAColor: Codable {
  public init(from decoder: Decoder) throws {
    let container = try decoder.singleValueContainer()
    let colorString = try container.decode(String.self)
    self = RGBAColor.color(withHexString: colorString)!
  }

  public func encode(to encoder: Encoder) throws {
    var container = encoder.singleValueContainer()
    try container.encode(hexString)
  }
}

private func colorValueForShortFormat<S: StringProtocol>(_ string: S) throws -> UInt32 {
  guard let value = UInt32(string, safeRadix: .hex) else {
    throw ColorError.unexpectedCharacter
  }

  let a = value & 0xF0_00
  let r = value & 0xF_00
  let g = value & 0xF0
  let b = value & 0xF

  let a16 = a << 16
  let a12 = a << 12
  let r12 = r << 12
  let r8 = r << 8
  let g8 = g << 8
  let g4 = g << 4
  let b4 = b << 4

  let aComp = a16 | a12
  let rComp = r12 | r8
  let gComp = g8 | g4
  let bComp = b4 | b
  return aComp | rComp | gComp | bComp
}

private func colorValueForFullFormat<S: StringProtocol>(_ string: S) throws -> UInt32 {
  guard let value = UInt32(string, safeRadix: .hex) else {
    throw ColorError.unexpectedCharacter
  }

  return value
}

private enum ColorError: Error {
  case unexpectedCharacter
}
