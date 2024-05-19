import Foundation

import CommonCorePublic

extension [String: Function] {
  mutating func addColorFunctions() {
    addFunction("argb", _argb)
    addFunction("rgb", _rgb)

    for channel in Channel.allCases {
      addFunction("getColor\(channel.description)", _getColor(channel: channel))
      addFunction("setColor\(channel.description)", _setColor(channel: channel))
    }
  }
}

private enum Channel: String, CaseIterable {
  case alpha
  case red
  case green
  case blue

  var description: String {
    rawValue.capitalized
  }
}

private let _argb = FunctionQuaternary { alpha, red, green, blue in
  guard isNormalized(alpha), isNormalized(red), isNormalized(green), isNormalized(blue) else {
    throw valueOutOfRangeError()
  }
  return Color(red: red, green: green, blue: blue, alpha: alpha)
}

private let _rgb = FunctionTernary { red, green, blue in
  guard isNormalized(red), isNormalized(green), isNormalized(blue) else {
    throw valueOutOfRangeError()
  }
  return Color(red: red, green: green, blue: blue, alpha: 1)
}

private func _getColor(channel: Channel) -> Function {
  OverloadedFunction(
    functions: [
      FunctionUnary { try getChannel(channel, from: $0) },
      FunctionUnary<Color, Double> { $0.makeChannel(channel) },
    ]
  )
}

private func _setColor(channel: Channel) -> Function {
  OverloadedFunction(
    functions: [
      FunctionBinary<String, Double, Color> { color, value in
        guard let color = Color.color(withHexString: color) else {
          throw invalidValueFormatError()
        }
        guard isNormalized(value) else {
          throw valueOutOfRangeError()
        }
        return color.set(value, for: channel)
      },
      FunctionBinary<Color, Double, Color> { color, value in
        guard isNormalized(value) else {
          throw valueOutOfRangeError()
        }
        return color.set(value, for: channel)
      }
    ]
  )
}

private func getChannel(_ channel: Channel, from color: String) throws -> Double {
  guard let color = Color.color(withHexString: color) else {
    throw invalidValueFormatError()
  }
  return color.makeChannel(channel)
}

extension Color {
  fileprivate func makeChannel(_ channel: Channel) -> Double {
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

  fileprivate func set(_ value: Double, for channel: Channel) -> Color {
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

private func isNormalized(_ value: Double) -> Bool {
  if value < 0 || value > 1 {
    return false
  }
  return true
}

private func valueOutOfRangeError() -> Error {
  ExpressionError("Value out of range 0..1.")
}

private func invalidValueFormatError() -> Error {
  ExpressionError("Unable to convert value to Color, expected format #AARRGGBB.")
}
