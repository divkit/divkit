import Foundation

import CommonCorePublic

enum CastFunctions: String, CaseIterable {
  case toBoolean
  case toNumber
  case toInteger
  case toColor
  case toUrl

  var function: Function {
    switch self {
    case .toBoolean:
      OverloadedFunction(
        functions: [
          FunctionUnary(impl: _stringToBoolean),
          FunctionUnary(impl: _intToBoolean),
        ],
        makeError: {
          CalcExpression.Error.toBooleanUnsupportedType($0.first?.value)
        }
      )
    case .toNumber:
      OverloadedFunction(
        functions: [
          FunctionUnary(impl: _intToNumber),
          FunctionUnary(impl: _stringToNumber),
        ],
        makeError: {
          CalcExpression.Error.toNumberUnsupportedType($0.first?.value)
        }
      )
    case .toInteger:
      OverloadedFunction(
        functions: [
          FunctionUnary(impl: _boolToInteger),
          FunctionUnary(impl: _doubleToInteger),
          FunctionUnary(impl: _stringToInteger),
        ],
        makeError: {
          CalcExpression.Error.toInteger($0.first?.value)
        }
      )
    case .toColor:
      FunctionUnary(impl: _stringToColor)
    case .toUrl:
      FunctionUnary(impl: _stringToUrl)
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
    throw CalcExpression.Error.toBooleanIncorrectValue(value)
  }
}

private func _intToBoolean(value: Int) throws -> Bool {
  switch value {
  case 1:
    return true
  case 0:
    return false
  default:
    throw CalcExpression.Error.toBooleanIncorrectValue(value)
  }
}

private func _intToNumber(value: Int) throws -> Double {
  Double(value)
}

private func _stringToNumber(value: String) throws -> Double {
  guard let number = Double(value), number.isFinite else {
    throw CalcExpression.Error.toNumberIncorrectValue(value)
  }
  return number
}

private func _boolToInteger(value: Bool) throws -> Int {
  value.toInteger()
}

private func _stringToInteger(value: String) throws -> Int {
  guard let number = Int(value) else {
    throw CalcExpression.Error.toInteger("'\(value)'")
  }
  return number
}

private func _doubleToInteger(value: Double) throws -> Int {
  guard let number = value.toInt() else {
    throw CalcExpression.Error.toInteger(value.toScientificFormat() ?? "\(value)")
  }
  return number
}

private func _stringToColor(value: String) throws -> Color {
  guard let color = Color.color(withHexString: value) else {
    throw CalcExpression.Error.toColor(value)
  }
  return color
}

private func _stringToUrl(value: String) throws -> URL {
  guard let url = URL(string: value) else {
    throw CalcExpression.Error.toUrl(value)
  }
  return url
}

extension Bool {
  fileprivate func toInteger() -> Int {
    switch self {
    case true:
      1
    case false:
      0
    }
  }
}

extension Double {
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

extension CalcExpression.Error {
  fileprivate static func toBooleanIncorrectValue(_ value: Any?) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toBoolean(\(formatValue(value)))]. Unable to convert value to Boolean."
    )
  }

  fileprivate static func toBooleanUnsupportedType(_ value: Any?) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toBoolean(\(formatValue(value)))]. Function 'toBoolean' has no matching override for given argument types: \(formatType(value))."
    )
  }

  fileprivate static func toNumberIncorrectValue(_ value: Any?) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toNumber(\(formatValue(value)))]. Unable to convert value to Number."
    )
  }

  fileprivate static func toNumberUnsupportedType(_ value: Any?) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toNumber(\(formatValue(value)))]. Function 'toNumber' has no matching override for given argument types: \(formatType(value))."
    )
  }

  fileprivate static func toInteger(_ value: Any?) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toInteger(\(value ?? ""))]. Unable to convert value to Integer."
    )
  }

  fileprivate static func toColor(_ value: String) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toColor('\(value)')]. Unable to convert value to Color, expected format #AARRGGBB."
    )
  }

  fileprivate static func toUrl(_ value: String) -> CalcExpression.Error {
    .message(
      "Failed to evaluate [toUrl('\(value)')]. Unable to convert value to URL."
    )
  }

  private static func formatValue(_ value: Any?) -> String {
    switch value {
    case let strValue as String:
      "'\(strValue)'"
    case let doubleValue as Double:
      String(doubleValue)
    default:
      "\(value ?? "")"
    }
  }

  private static func formatType(_ value: Any?) -> String {
    switch value {
    case is Double:
      "Number"
    case is Bool:
      "Boolean"
    default:
      "\(type(of: value))"
    }
  }
}
