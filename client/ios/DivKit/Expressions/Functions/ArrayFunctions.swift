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

  case getStringFromArray
  case getOptStringFromArray
  case getIntegerFromArray
  case getOptIntegerFromArray
  case getNumberFromArray
  case getOptNumberFromArray
  case getBooleanFromArray
  case getOptBooleanFromArray
  case getColorFromArray
  case getOptColorFromArray
  case getUrlFromArray
  case getOptUrlFromArray
  case getArrayFromArray
  case getOptArrayFromArray
  case getDictFromArray
  case getOptDictFromArray

  var function: Function {
    switch self {
    case .getArrayString:
      FunctionBinary(impl: _getArrayString)
    case .getArrayNumber:
      FunctionBinary(impl: _getArrayNumber)
    case .getArrayInteger:
      FunctionBinary(impl: _getArrayInteger)
    case .getArrayBoolean:
      FunctionBinary(impl: _getArrayBoolean)
    case .getArrayColor:
      FunctionBinary(impl: _getArrayColor)
    case .getArrayUrl:
      FunctionBinary(impl: _getArrayUrl)
    case .getArrayOptString:
      FunctionTernary(impl: _getArrayOptString)
    case .getArrayOptNumber:
      FunctionTernary(impl: _getArrayOptNumber)
    case .getArrayOptInteger:
      FunctionTernary(impl: _getArrayOptInteger)
    case .getArrayOptBoolean:
      FunctionTernary(impl: _getArrayOptBoolean)
    case .getArrayOptColor:
      OverloadedFunction(
        functions: [
          FunctionTernary(impl: _getArrayOptColorWithColorFallback),
          FunctionTernary(impl: _getArrayOptColorWithStringFallback),
        ]
      )
    case .getArrayOptUrl:
      OverloadedFunction(
        functions: [
          FunctionTernary(impl: _getArrayOptUrlWithURLFallback),
          FunctionTernary(impl: _getArrayOptUrlWithStringFallback),
        ]
      )

    case .getStringFromArray:
      FunctionBinary(impl: _getStringFromArray)
    case .getOptStringFromArray:
      FunctionTernary(impl: _getOptStringFromArray)
    case .getIntegerFromArray:
      FunctionBinary(impl: _getIntegerFromArray)
    case .getOptIntegerFromArray:
      FunctionTernary(impl: _getOptIntegerFromArray)
    case .getNumberFromArray:
      FunctionBinary(impl: _getNumberFromArray)
    case .getOptNumberFromArray:
      FunctionTernary(impl: _getOptNumberFromArray)
    case .getBooleanFromArray:
      FunctionBinary(impl: _getBooleanFromArray)
    case .getOptBooleanFromArray:
      FunctionTernary(impl: _getOptBooleanFromArray)
    case .getColorFromArray:
      FunctionBinary(impl: _getColorFromArray)
    case .getOptColorFromArray:
      OverloadedFunction(
        functions: [
          FunctionTernary(impl: _getOptColorFromArrayWithColorFallback),
          FunctionTernary(impl: _getOptColorFromArrayWithStringFallback),
        ]
      )
    case .getUrlFromArray:
      FunctionBinary(impl: _getUrlFromArray)
    case .getOptUrlFromArray:
      OverloadedFunction(
        functions: [
          FunctionTernary(impl: _getOptUrlFromArrayWithURLFallback),
          FunctionTernary(impl: _getOptUrlFromArrayWithStringFallback),
        ]
      )
    case .getArrayFromArray:
      FunctionBinary(impl: _getArrayFromArray)
    case .getOptArrayFromArray:
      FunctionBinary(impl: _getOptArrayFromArray)
    case .getDictFromArray:
      FunctionBinary(impl: _getDictFromArray)
    case .getOptDictFromArray:
      FunctionBinary(impl: _getOptDictFromArray)
    }
  }

  private func _getArrayString(array: [AnyHashable], index: Int) throws -> String {
    let result = try getValue(array: array, index: index)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType("string", result.actualType).message
    }
    return stringValue
  }

  private func _getStringFromArray(array: [AnyHashable], index: Int) throws -> String {
    let result = try getValue(array: array, index: index)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType("string", result.actualType).message
    }
    return stringValue
  }

  private func _getArrayNumber(array: [AnyHashable], index: Int) throws -> Double {
    let result = try getValue(array: array, index: index)
    if result.isBool {
      throw Error.incorrectValueType("number", result.actualType).message
    }
    if let numberValue = result as? Double {
      return numberValue
    }
    if let intValue = result as? Int {
      return Double(intValue)
    }
    throw Error.incorrectValueType("number", result.actualType).message
  }

  private func _getNumberFromArray(array: [AnyHashable], index: Int) throws -> Double {
    let result = try getValue(array: array, index: index)
    if result.isBool {
      throw Error.incorrectValueType("number", result.actualType).message
    }
    if let numberValue = result as? Double {
      return numberValue
    }
    if let intValue = result as? Int {
      return Double(intValue)
    }
    throw Error.incorrectValueType("number", result.actualType).message
  }

  private func _getArrayInteger(array: [AnyHashable], index: Int) throws -> Int {
    let result = try getValue(array: array, index: index)
    if result.isBool {
      throw Error.incorrectValueType("integer", result.actualType).message
    }
    guard let intValue = result as? Int else {
      if let doubleValue = result as? Double {
        if doubleValue < Double(Int.min) || doubleValue > Double(Int.max) {
          throw Error.integerOverflow.message
        }
        throw Error.cannotConvertToInteger.message
      }
      throw Error.incorrectValueType("integer", result.actualType).message
    }
    return intValue
  }

  private func _getIntegerFromArray(array: [AnyHashable], index: Int) throws -> Int {
    let result = try getValue(array: array, index: index)
    if result.isBool {
      throw Error.incorrectValueType("integer", result.actualType).message
    }
    guard let intValue = result as? Int else {
      if let doubleValue = result as? Double {
        if doubleValue < Double(Int.min) || doubleValue > Double(Int.max) {
          throw Error.integerOverflow.message
        }
        throw Error.cannotConvertToInteger.message
      }
      throw Error.incorrectValueType("integer", result.actualType).message
    }
    return intValue
  }

  private func _getArrayBoolean(array: [AnyHashable], index: Int) throws -> Bool {
    let result = try getValue(array: array, index: index)
    guard result.isBool else {
      throw Error.incorrectValueType("boolean", result.actualType).message
    }
    guard let boolValue = result as? Bool else {
      throw Error.incorrectValueType("boolean", result.actualType).message
    }
    return boolValue
  }

  private func _getBooleanFromArray(array: [AnyHashable], index: Int) throws -> Bool {
    let result = try getValue(array: array, index: index)
    guard result.isBool else {
      throw Error.incorrectValueType("boolean", result.actualType).message
    }
    guard let boolValue = result as? Bool else {
      throw Error.incorrectValueType("boolean", result.actualType).message
    }
    return boolValue
  }

  private func _getArrayColor(array: [AnyHashable], index: Int) throws -> Color {
    let result = try getValue(array: array, index: index)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType("color", result.actualType).message
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw Error.incorrectColorFormat.message
    }
    return color
  }

  private func _getColorFromArray(array: [AnyHashable], index: Int) throws -> Color {
    let result = try getValue(array: array, index: index)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType("color", result.actualType).message
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw Error.incorrectColorFormat.message
    }
    return color
  }

  private func _getArrayUrl(array: [AnyHashable], index: Int) throws -> URL {
    let result = try getValue(array: array, index: index)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType("url", result.actualType).message
    }
    guard let url = URL(string: stringValue) else {
      throw Error.incorrectValueType("url", result.actualType).message
    }
    return url
  }

  private func _getUrlFromArray(array: [AnyHashable], index: Int) throws -> URL {
    let result = try getValue(array: array, index: index)
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType("url", result.actualType).message
    }
    guard let url = URL(string: stringValue) else {
      throw Error.incorrectValueType("url", result.actualType).message
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

  private func _getOptStringFromArray(
    array: [AnyHashable],
    index: Int,
    fallback: String
  ) throws -> String {
    guard let value = try? _getStringFromArray(array: array, index: index) else {
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

  private func _getOptNumberFromArray(
    array: [AnyHashable],
    index: Int,
    fallback: Double
  ) throws -> Double {
    guard let value = try? _getNumberFromArray(array: array, index: index) else {
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

  private func _getOptIntegerFromArray(
    array: [AnyHashable],
    index: Int,
    fallback: Int
  ) throws -> Int {
    guard let value = try? _getIntegerFromArray(array: array, index: index) else {
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

  private func _getOptBooleanFromArray(
    array: [AnyHashable],
    index: Int,
    fallback: Bool
  ) throws -> Bool {
    guard let value = try? _getBooleanFromArray(array: array, index: index) else {
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

  private func _getOptColorFromArrayWithColorFallback(
    array: [AnyHashable],
    index: Int,
    fallback: Color
  ) throws -> Color {
    guard let value = try? _getColorFromArray(array: array, index: index) else {
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

  private func _getOptColorFromArrayWithStringFallback(
    array: [AnyHashable],
    index: Int,
    fallback: String
  ) throws -> Color {
    guard let value = try? _getColorFromArray(array: array, index: index) else {
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

  private func _getOptUrlFromArrayWithURLFallback(
    array: [AnyHashable],
    index: Int,
    fallback: URL
  ) throws -> URL {
    guard let value = try? _getUrlFromArray(array: array, index: index) else {
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

  private func _getOptUrlFromArrayWithStringFallback(
    array: [AnyHashable],
    index: Int,
    fallback: String
  ) throws -> URL {
    guard let value = try? _getUrlFromArray(array: array, index: index) else {
      return URL(string: fallback)!
    }
    return value
  }

  private func _getArrayFromArray(
    array: [AnyHashable],
    index: Int
  ) throws -> [AnyHashable] {
    let result = try getValue(array: array, index: index)
    guard let arrayValue = result as? [AnyHashable] else {
      throw Error.incorrectValueType("array", result.actualType).message
    }
    return arrayValue
  }

  private func _getOptArrayFromArray(
    array: [AnyHashable],
    index: Int
  ) throws -> [AnyHashable] {
    guard let value = try? _getArrayFromArray(array: array, index: index) else {
      return []
    }
    return value
  }

  private func _getDictFromArray(
    array: [AnyHashable],
    index: Int
  ) throws -> [String: AnyHashable] {
    let result = try getValue(array: array, index: index)
    guard let dictValue = result as? [String: AnyHashable] else {
      throw Error.incorrectValueType("dict", result.actualType).message
    }
    return dictValue
  }

  private func _getOptDictFromArray(
    array: [AnyHashable],
    index: Int
  ) throws -> [String: AnyHashable] {
    guard let value = try? _getDictFromArray(array: array, index: index) else {
      return [:]
    }
    return value
  }

  private func getValue(
    array: [AnyHashable],
    index: Int
  ) throws -> AnyHashable {
    guard index >= 0, index < array.count else {
      throw Error.indexOutOfBounds(index, array.count).message
    }
    return array[index]
  }

  private enum Error {
    case indexOutOfBounds(Int, Int)
    case incorrectValueType(String, String)
    case cannotConvertToInteger
    case integerOverflow
    case incorrectColorFormat

    private var description: String {
      switch self {
      case let .indexOutOfBounds(requstedIndex, arraySize):
        "Requested index (\(requstedIndex)) out of bounds array size (\(arraySize))."
      case let .incorrectValueType(expectedType, actualType):
        "Incorrect value type: expected \"\(expectedType)\", got \"\(actualType)\"."
      case .cannotConvertToInteger:
        "Cannot convert value to integer."
      case .integerOverflow:
        "Integer overflow."
      case .incorrectColorFormat:
        "Unable to convert value to Color, expected format #AARRGGBB."
      }
    }

    var message: CalcExpression.Error {
      CalcExpression.Error.message(description)
    }
  }
}
