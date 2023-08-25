import CommonCorePublic
import Foundation

enum ArrayFunctions: String, CaseIterable {
  case getArrayInteger
  case getArrayNumber
  case getArrayString
  case getArrayColor
  case getArrayBoolean
  case getArrayUrl
  case getArrayOptInteger
  case getArrayOptNumber
  case getArrayOptString
  case getArrayOptColor
  case getArrayOptBoolean
  case getArrayOptUrl

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .getArrayString:
      return FunctionBinary(impl: _getArrayString)
    case .getArrayNumber:
      return FunctionBinary(impl: _getArrayNumber)
    case .getArrayInteger:
      return FunctionBinary(impl: _getArrayInteger)
    case .getArrayBoolean:
      return FunctionBinary(impl: _getArrayBoolean)
    case .getArrayColor:
      return FunctionBinary(impl: _getArrayColor)
    case .getArrayUrl:
      return FunctionBinary(impl: _getArrayUrl)
    case .getArrayOptString:
      return FunctionTernary(impl: _getArrayOptString)
    case .getArrayOptNumber:
      return FunctionTernary(impl: _getArrayOptNumber)
    case .getArrayOptInteger:
      return FunctionTernary(impl: _getArrayOptInteger)
    case .getArrayOptBoolean:
      return FunctionTernary(impl: _getArrayOptBoolean)
    case .getArrayOptColor:
      return OverloadedFunction(
        functions: [
          FunctionTernary(impl: _getArrayOptColorWithColorFallback),
          FunctionTernary(impl: _getArrayOptColorWithStringFallback),
        ]
      )
    case .getArrayOptUrl:
      return OverloadedFunction(
        functions: [
          FunctionTernary(impl: _getArrayOptUrlWithURLFallback),
          FunctionTernary(impl: _getArrayOptUrlWithStringFallback),
        ]
      )
    }
  }

  private func _getArrayString(array: [AnyHashable], index: Int) throws -> String {
    let expression = makeExpression("getArrayString", index)
    let result = try getValue(array: array, index: index, expression: expression)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "string", result.actualType).message
    }
    return stringValue
  }

  private func _getArrayNumber(array: [AnyHashable], index: Int) throws -> Double {
    let expression = makeExpression("getArrayNumber", index)
    let result = try getValue(array: array, index: index, expression: expression)
    if result.isBool {
      throw Error.incorrectValueType(expression, "number", result.actualType).message
    }
    if let numberValue = result as? Double {
      return numberValue
    }
    if let intValue = result as? Int {
      return Double(intValue)
    }
    throw Error.incorrectValueType(expression, "number", result.actualType).message
  }

  private func _getArrayInteger(array: [AnyHashable], index: Int) throws -> Int {
    let expression = makeExpression("getArrayInteger", index)
    let result = try getValue(array: array, index: index, expression: expression)
    if result.isBool {
      throw Error.incorrectValueType(expression, "integer", result.actualType).message
    }
    guard let intValue = result as? Int else {
      if let doubleValue = result as? Double {
        if doubleValue < Double(Int.min) || doubleValue > Double(Int.max) {
          throw Error.integerOverflow(expression).message
        }
        throw Error.cannotConvertToInteger(expression).message
      }
      throw Error.incorrectValueType(expression, "integer", result.actualType).message
    }
    return intValue
  }

  private func _getArrayBoolean(array: [AnyHashable], index: Int) throws -> Bool {
    let expression = makeExpression("getArrayBoolean", index)
    let result = try getValue(array: array, index: index, expression: expression)
    guard result.isBool else {
      throw Error.incorrectValueType(expression, "boolean", result.actualType).message
    }
    guard let boolValue = result as? Bool else {
      throw Error.incorrectValueType(expression, "boolean", result.actualType).message
    }
    return boolValue
  }

  private func _getArrayColor(array: [AnyHashable], index: Int) throws -> Color {
    let expression = makeExpression("getArrayColor", index)
    let result = try getValue(array: array, index: index, expression: expression)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "color", result.actualType).message
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw Error.incorrectColorFormat(expression).message
    }
    return color
  }

  private func _getArrayUrl(array: [AnyHashable], index: Int) throws -> URL {
    let expression = makeExpression("getArrayUrl", index)
    let result = try getValue(array: array, index: index, expression: expression)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "url", result.actualType).message
    }
    guard let url = URL(string: stringValue) else {
      throw Error.incorrectValueType(expression, "url", result.actualType).message
    }
    return url
  }

  private func _getArrayOptString(
    array: [AnyHashable],
    index: Int,
    fallback: String
  ) throws -> String {
    guard let value = try? _getArrayString(array: array, index: index) else {
      return fallback
    }
    return value
  }

  private func _getArrayOptNumber(
    array: [AnyHashable],
    index: Int,
    fallback: Double
  ) throws -> Double {
    guard let value = try? _getArrayNumber(array: array, index: index) else {
      return fallback
    }
    return value
  }

  private func _getArrayOptInteger(array: [AnyHashable], index: Int, fallback: Int) throws -> Int {
    guard let value = try? _getArrayInteger(array: array, index: index) else {
      return fallback
    }
    return value
  }

  private func _getArrayOptBoolean(
    array: [AnyHashable],
    index: Int,
    fallback: Bool
  ) throws -> Bool {
    guard let value = try? _getArrayBoolean(array: array, index: index) else {
      return fallback
    }
    return value
  }

  private func _getArrayOptColorWithColorFallback(
    array: [AnyHashable],
    index: Int,
    fallback: Color
  ) throws -> Color {
    guard let value = try? _getArrayColor(array: array, index: index) else {
      return fallback
    }
    return value
  }

  private func _getArrayOptColorWithStringFallback(
    array: [AnyHashable],
    index: Int,
    fallback: String
  ) throws -> Color {
    guard let value = try? _getArrayColor(array: array, index: index) else {
      return Color.color(withHexString: fallback)!
    }
    return value
  }

  private func _getArrayOptUrlWithURLFallback(
    array: [AnyHashable],
    index: Int,
    fallback: URL
  ) throws -> URL {
    guard let value = try? _getArrayUrl(array: array, index: index) else {
      return fallback
    }
    return value
  }

  private func _getArrayOptUrlWithStringFallback(
    array: [AnyHashable],
    index: Int,
    fallback: String
  ) throws -> URL {
    guard let value = try? _getArrayUrl(array: array, index: index) else {
      return URL(string: fallback)!
    }
    return value
  }

  private func getValue(
    array: [AnyHashable],
    index: Int,
    expression: String
  ) throws -> AnyHashable {
    guard index >= 0 && index < array.count
    else { throw Error.indexOutOfBounds(expression, index, array.count).message }
    return array[index]
  }

  private func makeExpression(_ function: String, _ index: Int) -> String {
    "\(function)(<array>, \(index))"
  }

  private enum Error {
    case indexOutOfBounds(String, Int, Int)
    case incorrectValueType(String, String, String)
    case cannotConvertToInteger(String)
    case integerOverflow(String)
    case incorrectColorFormat(String)

    private var description: String {
      switch self {
      case let .indexOutOfBounds(expression, requstedIndex, arraySize):
        return "Failed to evaluate [\(expression)]. Requested index (\(requstedIndex)) out of bounds array size (\(arraySize))"
      case let .incorrectValueType(expression, expectedType, actualType):
        return "Failed to evaluate [\(expression)]. Incorrect value type: expected \"\(expectedType)\", got \"\(actualType)\""
      case let .cannotConvertToInteger(expression):
        return "Failed to evaluate [\(expression)]. Cannot convert value to integer."
      case let .integerOverflow(expression):
        return "Failed to evaluate [\(expression)]. Integer overflow."
      case let .incorrectColorFormat(expression):
        return "Failed to evaluate [\(expression)]. Unable to convert value to Color, expected format #AARRGGBB."
      }
    }

    var message: AnyCalcExpression.Error {
      AnyCalcExpression.Error.message(description)
    }
  }
}
