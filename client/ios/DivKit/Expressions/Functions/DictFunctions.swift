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

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .getDictString:
      return FunctionVarBinary(impl: _getDictString)
    case .getDictNumber:
      return FunctionVarBinary(impl: _getDictNumber)
    case .getDictInteger:
      return FunctionVarBinary(impl: _getDictInteger)
    case .getDictBoolean:
      return FunctionVarBinary(impl: _getDictBoolean)
    case .getDictColor:
      return FunctionVarBinary(impl: _getDictColor)
    case .getDictUrl:
      return FunctionVarBinary(impl: _getDictUrl)
    case .getDictOptString:
      return FunctionVarTernary(impl: _getDictOptString)
    case .getDictOptNumber:
      return FunctionVarTernary(impl: _getDictOptNumber)
    case .getDictOptInteger:
      return FunctionVarTernary(impl: _getDictOptInteger)
    case .getDictOptBoolean:
      return FunctionVarTernary(impl: _getDictOptBoolean)
    case .getDictOptColor:
      return OverloadedFunction(
        functions: [
          FunctionVarTernary(impl: _getDictOptColorWithColorFallback),
          FunctionVarTernary(impl: _getDictOptColorWithStringFallback),
        ]
      )
    case .getDictOptUrl:
      return OverloadedFunction(
        functions: [
          FunctionVarTernary(impl: _getDictOptUrlWithURLFallback),
          FunctionVarTernary(impl: _getDictOptUrlWithStringFallback),
        ]
      )
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
      throw Error.incorrectValueType(expression, "string", getActualType(result)).message
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
      throw Error.incorrectValueType(expression, "number", getActualType(result)).message
    }
    if let numberValue = result as? Double {
      return numberValue
    }
    if let intValue = result as? Int {
      return Double(intValue)
    }
    throw Error.incorrectValueType(expression, "number", getActualType(result)).message
  }

  private func _getDictInteger(dict: [String: AnyHashable], path: [String]) throws -> Int {
    let expression = makeExpression("getDictInteger", path)
    let result = try getProp(
      dict: dict,
      path: path,
      expression: expression
    )
    if result.isBool {
      throw Error.incorrectValueType(expression, "integer", getActualType(result)).message
    }
    guard let intValue = result as? Int else {
      if let doubleValue = result as? Double {
        if doubleValue < Double(Int.min) || doubleValue > Double(Int.max) {
          throw Error.integerOverflow(expression).message
        }
        throw Error.cannotConvertToInteger(expression).message
      }
      throw Error.incorrectValueType(expression, "integer", getActualType(result)).message
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
      throw Error.incorrectValueType(expression, "boolean", getActualType(result)).message
    }
    guard let boolValue = result as? Bool else {
      throw Error.incorrectValueType(expression, "boolean", getActualType(result)).message
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
      throw Error.incorrectValueType(expression, "color", getActualType(result)).message
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
      throw Error.incorrectValueType(expression, "url", getActualType(result)).message
    }
    guard let url = URL(string: stringValue) else {
      throw Error.incorrectValueType(expression, "url", getActualType(result)).message
    }
    return url
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

private func getActualType(_ value: AnyHashable) -> String {
  if value.isBool {
    return "boolean"
  }
  switch value {
  case is [String: AnyHashable]:
    return "dict"
  case is [Any]:
    return "array"
  case is String:
    return "string"
  case is Int, is Double:
    return "number"
  default:
    return "null"
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
      return "Failed to evaluate [\(expression)]. Missing property \"\(missing)\" in the dict."
    case let .wrongPath(expression):
      return "Failed to evaluate [\(expression)]. Path is wrong."
    case let .incorrectValueType(expression, expectedType, actualType):
      return "Failed to evaluate [\(expression)]. Incorrect value type: expected \"\(expectedType)\", got \"\(actualType)\"."
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

extension AnyHashable {
  fileprivate var isBool: Bool {
    Bool(description) != nil && !(self is String)
  }
}
