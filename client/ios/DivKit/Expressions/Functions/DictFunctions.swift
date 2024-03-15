import CommonCorePublic
import Foundation

enum DictFunctions: String, CaseIterable {
  case getDictString
  case getDictNumber
  case getDictInteger
  case getDictBoolean
  case getDictColor
  case getDictUrl
  case getDictOptString
  case getDictOptNumber
  case getDictOptInteger
  case getDictOptBoolean
  case getDictOptColor
  case getDictOptUrl

  case getArrayFromDict
  case getOptArrayFromDict
  case getDictFromDict
  case getOptDictFromDict
  case getStringFromDict
  case getOptStringFromDict
  case getIntegerFromDict
  case getOptIntegerFromDict
  case getNumberFromDict
  case getOptNumberFromDict
  case getBooleanFromDict
  case getOptBooleanFromDict
  case getColorFromDict
  case getOptColorFromDict
  case getUrlFromDict
  case getOptUrlFromDict

  var function: Function {
    switch self {
    case .getDictString:
      FunctionVarBinary(impl: _getDictString)
    case .getDictNumber:
      FunctionVarBinary(impl: _getDictNumber)
    case .getDictInteger:
      FunctionVarBinary(impl: _getDictInteger)
    case .getDictBoolean:
      FunctionVarBinary(impl: _getDictBoolean)
    case .getDictColor:
      FunctionVarBinary(impl: _getDictColor)
    case .getDictUrl:
      FunctionVarBinary(impl: _getDictUrl)
    case .getDictOptString:
      FunctionVarTernary(impl: _getDictOptString)
    case .getDictOptNumber:
      FunctionVarTernary(impl: _getDictOptNumber)
    case .getDictOptInteger:
      FunctionVarTernary(impl: _getDictOptInteger)
    case .getDictOptBoolean:
      FunctionVarTernary(impl: _getDictOptBoolean)
    case .getDictOptColor:
      OverloadedFunction(
        functions: [
          FunctionVarTernary(impl: _getDictOptColorWithColorFallback),
          FunctionVarTernary(impl: _getDictOptColorWithStringFallback),
        ]
      )
    case .getDictOptUrl:
      OverloadedFunction(
        functions: [
          FunctionVarTernary(impl: _getDictOptUrlWithURLFallback),
          FunctionVarTernary(impl: _getDictOptUrlWithStringFallback),
        ]
      )

    case .getStringFromDict:
      FunctionVarBinary(impl: _getStringFromDict)
    case .getOptStringFromDict:
      FunctionVarTernary(impl: _getOptStringFromDict)
    case .getIntegerFromDict:
      FunctionVarBinary(impl: _getIntegerFromDict)
    case .getOptIntegerFromDict:
      FunctionVarTernary(impl: _getOptIntegerFromDict)
    case .getNumberFromDict:
      FunctionVarBinary(impl: _getNumberFromDict)
    case .getOptNumberFromDict:
      FunctionVarTernary(impl: _getOptNumberFromDict)
    case .getBooleanFromDict:
      FunctionVarBinary(impl: _getBooleanFromDict)
    case .getOptBooleanFromDict:
      FunctionVarTernary(impl: _getOptBooleanFromDict)
    case .getColorFromDict:
      FunctionVarBinary(impl: _getColorFromDict)
    case .getOptColorFromDict:
      OverloadedFunction(
        functions: [
          FunctionVarTernary(impl: _getOptColorFromDictWithStringFallback),
          FunctionVarTernary(impl: _getOptColorFromDictWithColorFallback),
        ]
      )
    case .getUrlFromDict:
      FunctionVarBinary(impl: _getUrlFromDict)
    case .getOptUrlFromDict:
      OverloadedFunction(
        functions: [
          FunctionVarTernary(impl: _getOptUrlFromDictWithURLFallback),
          FunctionVarTernary(impl: _getOptUrlFromDictWithStringFallback),
        ]
      )
    case .getArrayFromDict:
      FunctionVarBinary(impl: _getArrayFromDict)
    case .getOptArrayFromDict:
      FunctionVarBinary(impl: _getOptArrayFromDict)
    case .getDictFromDict:
      FunctionVarBinary(impl: _getDictFromDict)
    case .getOptDictFromDict:
      FunctionVarBinary(impl: _getOptDictFromDict)
    }
  }

  private func _getDictString(dict: [String: AnyHashable], path: [String]) throws -> String {
    let expression = makeExpression("getDictString", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "string", result.actualType).message
    }
    return stringValue
  }

  private func _getStringFromDict(dict: [String: AnyHashable], path: [String]) throws -> String {
    let expression = makeExpression("getStringFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "string", result.actualType).message
    }
    return stringValue
  }

  private func _getDictNumber(dict: [String: AnyHashable], path: [String]) throws -> Double {
    let expression = makeExpression("getDictNumber", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
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

  private func _getNumberFromDict(dict: [String: AnyHashable], path: [String]) throws -> Double {
    let expression = makeExpression("getNumberFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
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

  private func _getDictInteger(dict: [String: AnyHashable], path: [String]) throws -> Int {
    let expression = makeExpression("getDictInteger", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
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

  private func _getIntegerFromDict(dict: [String: AnyHashable], path: [String]) throws -> Int {
    let expression = makeExpression("getIntegerFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
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

  private func _getDictBoolean(dict: [String: AnyHashable], path: [String]) throws -> Bool {
    let expression = makeExpression("getDictBoolean", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard result.isBool else {
      throw Error.incorrectValueType(expression, "boolean", result.actualType).message
    }
    guard let boolValue = result as? Bool else {
      throw Error.incorrectValueType(expression, "boolean", result.actualType).message
    }
    return boolValue
  }

  private func _getBooleanFromDict(dict: [String: AnyHashable], path: [String]) throws -> Bool {
    let expression = makeExpression("getBooleanFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard result.isBool else {
      throw Error.incorrectValueType(expression, "boolean", result.actualType).message
    }
    guard let boolValue = result as? Bool else {
      throw Error.incorrectValueType(expression, "boolean", result.actualType).message
    }
    return boolValue
  }

  private func _getDictColor(dict: [String: AnyHashable], path: [String]) throws -> Color {
    let expression = makeExpression("getDictColor", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "color", result.actualType).message
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw Error.incorrectColorFormat(expression).message
    }
    return color
  }

  private func _getColorFromDict(dict: [String: AnyHashable], path: [String]) throws -> Color {
    let expression = makeExpression("getColorFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "color", result.actualType).message
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw Error.incorrectColorFormat(expression).message
    }
    return color
  }

  private func _getDictUrl(dict: [String: AnyHashable], path: [String]) throws -> URL {
    let expression = makeExpression("getDictUrl", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "url", result.actualType).message
    }
    guard let url = URL(string: stringValue) else {
      throw Error.incorrectValueType(expression, "url", result.actualType).message
    }
    return url
  }

  private func _getUrlFromDict(dict: [String: AnyHashable], path: [String]) throws -> URL {
    let expression = makeExpression("getUrlFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let stringValue = result as? String else {
      throw Error.incorrectValueType(expression, "url", result.actualType).message
    }
    guard let url = URL(string: stringValue) else {
      throw Error.incorrectValueType(expression, "url", result.actualType).message
    }
    return url
  }

  private func _getDictFromDict(
    dict: [String: AnyHashable],
    path: [String]
  ) throws -> [String: AnyHashable] {
    let expression = makeExpression("getDictFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let dict = result as? [String: AnyHashable] else {
      throw Error.incorrectValueType(expression, "dict", result.actualType).message
    }
    return dict
  }

  private func _getArrayFromDict(
    dict: [String: AnyHashable],
    path: [String]
  ) throws -> [AnyHashable] {
    let expression = makeExpression("getArrayFromDict", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    guard let dict = result as? [AnyHashable] else {
      throw Error.incorrectValueType(expression, "array", result.actualType).message
    }
    return dict
  }

  private func _getDictOptString(
    fallback: String,
    dict: [String: AnyHashable],
    path: [String]
  ) -> String {
    guard let value = try? _getDictString(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptStringFromDict(
    fallback: String,
    dict: [String: AnyHashable],
    path: [String]
  ) -> String {
    guard let value = try? _getStringFromDict(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getDictOptNumber(
    fallback: Double,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Double {
    guard let value = try? _getDictNumber(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptNumberFromDict(
    fallback: Double,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Double {
    guard let value = try? _getNumberFromDict(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getDictOptInteger(
    fallback: Int,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Int {
    guard let value = try? _getDictInteger(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptIntegerFromDict(
    fallback: Int,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Int {
    guard let value = try? _getIntegerFromDict(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getDictOptBoolean(
    fallback: Bool,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Bool {
    guard let value = try? _getDictBoolean(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptBooleanFromDict(
    fallback: Bool,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Bool {
    guard let value = try? _getBooleanFromDict(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptDictFromDict(
    dict: [String: AnyHashable],
    path: [String]
  ) -> [String: AnyHashable] {
    guard let value = try? _getDictFromDict(dict: dict, path: path) else {
      return [:]
    }
    return value
  }

  private func _getOptArrayFromDict(
    dict: [String: AnyHashable],
    path: [String]
  ) -> [AnyHashable] {
    guard let value = try? _getArrayFromDict(dict: dict, path: path) else {
      return []
    }
    return value
  }

  private func _getDictOptColorWithColorFallback(
    fallback: Color,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Color {
    guard let value = try? _getDictColor(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptColorFromDictWithColorFallback(
    fallback: Color,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Color {
    guard let value = try? _getColorFromDict(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getDictOptColorWithStringFallback(
    fallback: String,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Color {
    guard let value = try? _getDictColor(dict: dict, path: path) else {
      return Color.color(withHexString: fallback)!
    }
    return value
  }

  private func _getOptColorFromDictWithStringFallback(
    fallback: String,
    dict: [String: AnyHashable],
    path: [String]
  ) -> Color {
    guard let value = try? _getColorFromDict(dict: dict, path: path) else {
      return Color.color(withHexString: fallback)!
    }
    return value
  }

  private func _getDictOptUrlWithURLFallback(
    fallback: URL,
    dict: [String: AnyHashable],
    path: [String]
  ) -> URL {
    guard let value = try? _getDictUrl(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getOptUrlFromDictWithURLFallback(
    fallback: URL,
    dict: [String: AnyHashable],
    path: [String]
  ) -> URL {
    guard let value = try? _getUrlFromDict(dict: dict, path: path) else {
      return fallback
    }
    return value
  }

  private func _getDictOptUrlWithStringFallback(
    fallback: String,
    dict: [String: AnyHashable],
    path: [String]
  ) -> URL {
    guard let value = try? _getDictUrl(dict: dict, path: path) else {
      return URL(string: fallback)!
    }
    return value
  }

  private func _getOptUrlFromDictWithStringFallback(
    fallback: String,
    dict: [String: AnyHashable],
    path: [String]
  ) -> URL {
    guard let value = try? _getUrlFromDict(dict: dict, path: path) else {
      return URL(string: fallback)!
    }
    return value
  }

  private func getProp(
    dict: [String: AnyHashable],
    path: [String],
    expression: String
  ) throws -> AnyHashable {
    var dictionary: [String: AnyHashable]? = dict
    var i = 0
    while i < path.count - 1 {
      let key = path[i]
      if let current = dictionary {
        dictionary = current[key] as? [String: AnyHashable]
      } else {
        throw Error.missingProperty(expression, key).message
      }
      i += 1
    }
    guard let key = path.last else {
      throw Error.wrongPath(expression).message
    }
    if let current = dictionary,
       let result = current[key] {
      return result
    } else {
      throw Error.missingProperty(expression, key).message
    }
  }
}

private func makeExpression(_ function: String, _ path: [String]) -> String {
  "\(function)(<dict>, \(path.map { "'\($0)'" }.joined(separator: ", ")))"
}

private enum Error {
  case missingProperty(String, String)
  case wrongPath(String)
  case incorrectValueType(String, String, String)
  case cannotConvertToInteger(String)
  case integerOverflow(String)
  case incorrectColorFormat(String)

  private var description: String {
    switch self {
    case let .missingProperty(expression, missing):
      "Failed to evaluate [\(expression)]. Missing property \"\(missing)\" in the dict."
    case let .wrongPath(expression):
      "Failed to evaluate [\(expression)]. Path is wrong."
    case let .incorrectValueType(expression, expectedType, actualType):
      "Failed to evaluate [\(expression)]. Incorrect value type: expected \"\(expectedType)\", got \"\(actualType)\"."
    case let .cannotConvertToInteger(expression):
      "Failed to evaluate [\(expression)]. Cannot convert value to integer."
    case let .integerOverflow(expression):
      "Failed to evaluate [\(expression)]. Integer overflow."
    case let .incorrectColorFormat(expression):
      "Failed to evaluate [\(expression)]. Unable to convert value to Color, expected format #AARRGGBB."
    }
  }

  var message: CalcExpression.Error {
    CalcExpression.Error.message(description)
  }
}
