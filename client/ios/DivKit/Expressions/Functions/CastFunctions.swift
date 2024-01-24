import Foundation

import CommonCorePublic

enum CastFunctions: String, CaseIterable {
  case toBoolean
  case toString
  case toNumber
  case toInteger
  case toColor
  case toUrl

  var function: Function {
    switch self {
    case .toBoolean:
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: _stringToBoolean),
          FunctionUnary(impl: _intToBoolean),
        ],
        makeError: {
          AnyCalcExpression.Error.toBooleanUnsupportedType($0.first?.value)
        }
      )
    case .toString:
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: _boolToString),
          FunctionUnary(impl: _doubleToString),
          FunctionUnary(impl: _intToString),
          FunctionUnary(impl: _colorToString),
          FunctionUnary(impl: _urlToString),
        ],
        makeError: {
          AnyCalcExpression.Error.toString($0.first?.value)
        }
      )
    case .toNumber:
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: _intToNumber),
          FunctionUnary(impl: _stringToNumber),
        ],
        makeError: {
          AnyCalcExpression.Error.toNumberUnsupportedType($0.first?.value)
        }
      )
    case .toInteger:
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: _boolToInteger),
          FunctionUnary(impl: _doubleToInteger),
          FunctionUnary(impl: _stringToInteger),
        ],
        makeError: {
          AnyCalcExpression.Error.toInteger($0.first?.value)
        }
      )
    case .toColor:
      return FunctionUnary(impl: _stringToColor)
    case .toUrl:
      return FunctionUnary(impl: _stringToUrl)
    }
  }
}

private func _stringToBoolean(value: String) throws -> Bool {
  switch value {
  case "true":
    return true
  case "false":
    return false
  default:
    throw AnyCalcExpression.Error.toBooleanIncorrectValue(value)
  }
}

private func _intToBoolean(value: Int) throws -> Bool {
  switch value {
  case 1:
    return true
  case 0:
    return false
  default:
    throw AnyCalcExpression.Error.toBooleanIncorrectValue(value)
  }
}

private func _boolToString(value: Bool) throws -> String {
  value.description
}

private func _doubleToString(value: Double) throws -> String {
  guard let string = value.toString() else {
    throw AnyCalcExpression.Error.toString(value)
  }
  return string
}

private func _intToString(value: Int) throws -> String {
  value.description
}

private func _colorToString(value: Color) throws -> String {
  value.argbString
}

private func _urlToString(value: URL) throws -> String {
  value.description
}

private func _intToNumber(value: Int) throws -> Double {
  Double(value)
}

private func _stringToNumber(value: String) throws -> Double {
  guard let number = Double(value), number.isFinite else {
    throw AnyCalcExpression.Error.toNumberIncorrectValue(value)
  }
  return number
}

private func _boolToInteger(value: Bool) throws -> Int {
  value.toInteger()
}

private func _stringToInteger(value: String) throws -> Int {
  guard let number = Int(value) else {
    throw AnyCalcExpression.Error.toInteger("'\(value)'")
  }
  return number
}

private func _doubleToInteger(value: Double) throws -> Int {
  guard let number = value.toInt() else {
    throw AnyCalcExpression.Error.toInteger(value.toScientificFormat() ?? "\(value)")
  }
  return number
}

private func _stringToColor(value: String) throws -> Color {
  guard let color = Color.color(withHexString: value) else {
    throw AnyCalcExpression.Error.toColor(value)
  }
  return color
}

private func _stringToUrl(value: String) throws -> URL {
  guard let url = URL(string: value) else {
    throw AnyCalcExpression.Error.toUrl(value)
  }
  return url
}

extension Bool {
  fileprivate func toInteger() -> Int {
    switch self {
    case true:
      return 1
    case false:
      return 0
    }
  }
}

extension Double {
  fileprivate func toString() -> String? {
    let formatter = NumberFormatter()
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 14
    formatter.decimalSeparator = "."
    return formatter.string(from: NSNumber(value: self))
  }

  fileprivate func toScientificFormat() -> String? {
    let formatter = NumberFormatter()
    formatter.numberStyle = NumberFormatter.Style.scientific
    formatter.decimalSeparator = "."
    return formatter.string(from: NSNumber(value: self))
  }

  fileprivate func toInt() -> Int? {
    guard Double(Int.min) <= self, self <= Double(Int.max) else { return nil }
    return Int(self)
  }
}

extension AnyCalcExpression.Error {
  fileprivate static func toBooleanIncorrectValue(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toBoolean(\(formatValue(value)))]. Unable to convert value to Boolean."
    )
  }

  fileprivate static func toBooleanUnsupportedType(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toBoolean(\(formatValue(value)))]. Function 'toBoolean' has no matching override for given argument types: \(formatType(value))."
    )
  }

  fileprivate static func toString(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toString(\(formatValue(value)))]. Unable to convert value to String."
    )
  }

  fileprivate static func toNumberIncorrectValue(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toNumber(\(formatValue(value)))]. Unable to convert value to Number."
    )
  }

  fileprivate static func toNumberUnsupportedType(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toNumber(\(formatValue(value)))]. Function 'toNumber' has no matching override for given argument types: \(formatType(value))."
    )
  }

  fileprivate static func toInteger(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toInteger(\(value ?? ""))]. Unable to convert value to Integer."
    )
  }

  fileprivate static func toColor(_ value: String) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toColor('\(value)')]. Unable to convert value to Color, expected format #AARRGGBB."
    )
  }

  fileprivate static func toUrl(_ value: String) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [toUrl('\(value)')]. Unable to convert value to URL."
    )
  }

  private static func formatValue(_ value: Any?) -> String {
    switch value {
    case let strValue as String:
      return "'\(strValue)'"
    case let doubleValue as Double:
      return doubleValue.toString() ?? ""
    default:
      return "\(value ?? "")"
    }
  }

  private static func formatType(_ value: Any?) -> String {
    switch value {
    case is Double:
      return "Number"
    case is Bool:
      return "Boolean"
    default:
      return "\(type(of: value))"
    }
  }
}
