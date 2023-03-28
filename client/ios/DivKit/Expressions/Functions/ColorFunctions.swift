import Foundation

import CommonCorePublic

enum ColorFunctions: CaseIterable {
  enum Error {
    case cast(String)
    case outOfRange(String)
    case get(Channel, String)
    case set(Channel, String, Double)

    var message: AnyCalcExpression.Error {
      AnyCalcExpression.Error.message(description)
    }

    private var description: String {
      switch self {
      case let .cast(type):
        return "Argument couldn't be casted to \(type)"
      case let .outOfRange(expression):
        return "Failed to evaluate [\(expression)]. Value out of range 0..1."
      case let .get(channel, value):
        return makeErrorDescription("[getColor\(channel.description)('\(value)')]")
      case let .set(channel, first, second):
        return makeErrorDescription("[setColor\(channel.description)('\(first)', \(second))]")
      }
    }
  }

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
      return "argb"
    case .rgb:
      return "rgb"
    case let .get(channel):
      return "getColor\(channel.description)"
    case let .set(channel):
      return "setColor\(channel.description)"
    }
  }

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .argb:
      return FunctionQuaternary(impl: _argb)
    case .rgb:
      return FunctionTernary(impl: _rgb)
    case let .get(channel):
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: { try getChannel(channel, from: $0) }),
          FunctionUnary(impl: { getChannelFromColor(channel, from: $0) }),
        ]
      ) {
        ColorFunctions.Error.get(channel, String(describing: $0.first)).message
      }
    case let .set(channel):
      return OverloadedFunction(
        functions: [
          FunctionBinary(impl: { try setChannel(channel, to: $0, value: $1) }),
          FunctionBinary(impl: { try setChannelForColor(channel, to: $0, value: $1) }),
        ]
      ) {
        ColorFunctions.Error
          .outOfRange(
            "setColor\(channel.description)(\(String(describing: $0.first)), \(String(describing: $0[1]))"
          )
          .message
      }
    }
  }
}

private func _argb(alpha: Double, red: Double, green: Double, blue: Double) throws -> Color {
  guard isNormalized(alpha), isNormalized(red), isNormalized(green), isNormalized(blue) else {
    throw ColorFunctions.Error.outOfRange("argb(\(alpha), \(red), \(green), \(blue))").message
  }
  return Color(red: red, green: green, blue: blue, alpha: alpha)
}

private func _rgb(red: Double, green: Double, blue: Double) throws -> Color {
  guard isNormalized(red), isNormalized(green), isNormalized(blue) else {
    throw ColorFunctions.Error.outOfRange("rgb(\(red), \(green), \(blue))").message
  }
  return Color(red: red, green: green, blue: blue, alpha: 1)
}

private func getChannel(_ channel: ColorFunctions.Channel, from color: String) throws -> Double {
  guard let color = Color.color(withHexString: color) else {
    throw ColorFunctions.Error.get(channel, color).message
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
    throw ColorFunctions.Error.set(channel, color, value).message
  }

  guard isNormalized(value) else {
    throw ColorFunctions.Error
      .outOfRange("setColor\(channel.description)('\(color.argbString)', \(value))").message
  }

  return color.set(value, for: channel)
}

private func setChannelForColor(
  _ channel: ColorFunctions.Channel,
  to color: Color,
  value: Double
) throws -> Color {
  guard isNormalized(value) else {
    throw ColorFunctions.Error
      .outOfRange("setColor\(channel.description)('\(color.argbString)', \(value))").message
  }

  return color.set(value, for: channel)
}

extension Color {
  fileprivate func makeChannel(_ channel: ColorFunctions.Channel) -> Double {
    switch channel {
    case .alpha:
      return alpha
    case .red:
      return red
    case .green:
      return green
    case .blue:
      return blue
    }
  }

  fileprivate func set(_ value: Double, for channel: ColorFunctions.Channel) -> Color {
    switch channel {
    case .alpha:
      return Color(red: red, green: green, blue: blue, alpha: value)
    case .red:
      return Color(red: value, green: green, blue: blue, alpha: alpha)
    case .green:
      return Color(red: red, green: value, blue: blue, alpha: alpha)
    case .blue:
      return Color(red: red, green: green, blue: value, alpha: alpha)
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

private func makeErrorDescription(_ value: String) -> String {
  "Failed to evaluate \(value). Unable to convert value to Color, expected format #AARRGGBB."
}
