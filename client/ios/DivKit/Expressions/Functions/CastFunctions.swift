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
        ]
      )
    case .toNumber:
      OverloadedFunction(
        functions: [
          FunctionUnary(impl: _intToNumber),
          FunctionUnary(impl: _stringToNumber),
        ]
      )
    case .toInteger:
      OverloadedFunction(
        functions: [
          FunctionUnary(impl: _boolToInteger),
          FunctionUnary(impl: _doubleToInteger),
          FunctionUnary(impl: _stringToInteger),
        ]
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
    throw CalcExpression.Error.message("Unable to convert value to Boolean.")
  }
}

private func _intToBoolean(value: Int) throws -> Bool {
  switch value {
  case 1:
    return true
  case 0:
    return false
  default:
    throw CalcExpression.Error.message("Unable to convert value to Boolean.")
  }
}

private func _intToNumber(value: Int) throws -> Double {
  Double(value)
}

private func _stringToNumber(value: String) throws -> Double {
  guard let number = Double(value), number.isFinite else {
    throw CalcExpression.Error.message("Unable to convert value to Number.")
  }
  return number
}

private func _boolToInteger(value: Bool) throws -> Int {
  value ? 1 : 0
}

private func _stringToInteger(value: String) throws -> Int {
  guard let number = Int(value) else {
    throw CalcExpression.Error.message("Unable to convert value to Integer.")
  }
  return number
}

private func _doubleToInteger(value: Double) throws -> Int {
  guard Double(Int.min) <= value, value <= Double(Int.max) else {
    throw CalcExpression.Error.message("Unable to convert value to Integer.")
  }
  return Int(value)
}

private func _stringToColor(value: String) throws -> Color {
  guard let color = Color.color(withHexString: value) else {
    throw CalcExpression.Error.message(
      "Unable to convert value to Color, expected format #AARRGGBB."
    )
  }
  return color
}

private func _stringToUrl(value: String) throws -> URL {
  guard let url = URL(string: value) else {
    throw CalcExpression.Error.message("Unable to convert value to URL.")
  }
  return url
}
