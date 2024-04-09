import Foundation

import CommonCorePublic

enum ColorFunctions: CaseIterable {
  enum Channel: String, CaseIterable {
    case alpha
    case red
    case green
    case blue

    var description: String {
      rawValue.capitalized
    }
  }

  case argb
  case rgb
  case get(Channel)
  case set(Channel)

  static var allCases: [ColorFunctions] {
    [.argb, .rgb] + Channel.allCases.flatMap { [.get($0), .set($0)] }
  }

  var rawValue: String {
    switch self {
    case .argb:
      "argb"
    case .rgb:
      "rgb"
    case let .get(channel):
      "getColor\(channel.description)"
    case let .set(channel):
      "setColor\(channel.description)"
    }
  }

  var function: Function {
    switch self {
    case .argb:
      FunctionQuaternary(impl: _argb)
    case .rgb:
      FunctionTernary(impl: _rgb)
    case let .get(channel):
      OverloadedFunction(
        functions: [
          FunctionUnary(impl: { try getChannel(channel, from: $0) }),
          FunctionUnary(impl: { getChannelFromColor(channel, from: $0) }),
        ]
      )
    case let .set(channel):
      OverloadedFunction(
        functions: [
          FunctionBinary(impl: { try setChannel(channel, to: $0, value: $1) }),
          FunctionBinary(impl: { try setChannelForColor(channel, to: $0, value: $1) }),
        ]
      )
    }
  }
}

private func _argb(alpha: Double, red: Double, green: Double, blue: Double) throws -> Color {
  guard isNormalized(alpha), isNormalized(red), isNormalized(green), isNormalized(blue) else {
    throw valueOutOfRangeError()
  }
  return Color(red: red, green: green, blue: blue, alpha: alpha)
}

private func _rgb(red: Double, green: Double, blue: Double) throws -> Color {
  guard isNormalized(red), isNormalized(green), isNormalized(blue) else {
    throw valueOutOfRangeError()
  }
  return Color(red: red, green: green, blue: blue, alpha: 1)
}

private func getChannel(_ channel: ColorFunctions.Channel, from color: String) throws -> Double {
  guard let color = Color.color(withHexString: color) else {
    throw invalidValueFormatError()
  }
  return color.makeChannel(channel)
}

private func getChannelFromColor(_ channel: ColorFunctions.Channel, from color: Color) -> Double {
  color.makeChannel(channel)
}

private func setChannel(
  _ channel: ColorFunctions.Channel,
  to color: String,
  value: Double
) throws -> Color {
  guard let color = Color.color(withHexString: color) else {
    throw invalidValueFormatError()
  }

  guard isNormalized(value) else {
    throw valueOutOfRangeError()
  }

  return color.set(value, for: channel)
}

private func setChannelForColor(
  _ channel: ColorFunctions.Channel,
  to color: Color,
  value: Double
) throws -> Color {
  guard isNormalized(value) else {
    throw valueOutOfRangeError()
  }
  return color.set(value, for: channel)
}

extension Color {
  fileprivate func makeChannel(_ channel: ColorFunctions.Channel) -> Double {
    switch channel {
    case .alpha:
      alpha
    case .red:
      red
    case .green:
      green
    case .blue:
      blue
    }
  }

  fileprivate func set(_ value: Double, for channel: ColorFunctions.Channel) -> Color {
    switch channel {
    case .alpha:
      Color(red: red, green: green, blue: blue, alpha: value)
    case .red:
      Color(red: value, green: green, blue: blue, alpha: alpha)
    case .green:
      Color(red: red, green: value, blue: blue, alpha: alpha)
    case .blue:
      Color(red: red, green: green, blue: value, alpha: alpha)
    }
  }
}

private func cast(_ value: Any) -> Double? {
  value as? Double
}

private func isNormalized(_ value: Double) -> Bool {
  if value < 0 || value > 1 {
    return false
  }
  return true
}

private func valueOutOfRangeError() -> CalcExpression.Error {
  .message("Value out of range 0..1.")
}

private func invalidValueFormatError() -> CalcExpression.Error {
  .message("Unable to convert value to Color, expected format #AARRGGBB.")
}
