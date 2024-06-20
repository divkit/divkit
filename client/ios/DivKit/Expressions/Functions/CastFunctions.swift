import Foundation

import CommonCorePublic

extension [String: Function] {
  mutating func addCastFunctions() {
    addFunction("toBoolean", _toBoolean)
    addFunction("toColor", _toColor)
    addFunction("toInteger", _toInteger)
    addFunction("toNumber", _toNumber)
    addFunction("toUrl", _toUrl)
  }
}

private let _toBoolean = OverloadedFunction(
  functions: [
    FunctionUnary<String, Bool> {
      switch $0 {
      case "true":
        return true
      case "false":
        return false
      default:
        throw ExpressionError("Unable to convert value to Boolean.")
      }
    },
    FunctionUnary<Int, Bool> {
      switch $0 {
      case 1:
        return true
      case 0:
        return false
      default:
        throw ExpressionError("Unable to convert value to Boolean.")
      }
    },
  ]
)

private let _toInteger = OverloadedFunction(
  functions: [
    FunctionUnary<Bool, Int> { $0 ? 1 : 0 },
    FunctionUnary<Double, Int> {
      guard Double(Int.min) <= $0, $0 <= Double(Int.max) else {
        throw ExpressionError("Unable to convert value to Integer.")
      }
      return Int($0)
    },
    FunctionUnary<String, Int> {
      guard let number = Int($0) else {
        throw ExpressionError("Unable to convert value to Integer.")
      }
      return number
    },
  ]
)

private let _toColor = FunctionUnary {
  guard let color = Color.color(withHexString: $0) else {
    throw ExpressionError("Unable to convert value to Color, expected format #AARRGGBB.")
  }
  return color
}

private let _toNumber = OverloadedFunction(
  functions: [
    FunctionUnary<Int, Double> { Double($0) },
    FunctionUnary<String, Double> {
      guard let number = Double($0), number.isFinite else {
        throw ExpressionError("Unable to convert value to Number.")
      }
      return number
    },
  ]
)

private let _toUrl = FunctionUnary {
  guard let url = URL(string: $0) else {
    throw ExpressionError("Unable to convert value to URL.")
  }
  return url
}
