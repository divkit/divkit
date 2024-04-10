import Foundation

import CommonCorePublic

private typealias Dict = [String: AnyHashable]

extension [String: Function] {
  mutating func addDictFunctions() {
    self["getArrayFromDict"] = DictFunctions.getArray()
    self["getOptArrayFromDict"] = getOptArrayFunction()

    self["getDictFromDict"] = DictFunctions.getDict()
    self["getOptDictFromDict"] = getOptDictFunction()

    addFunctions("Boolean", DictFunctions.getBoolean())
    addFunctions("OptBoolean", getOptBooleanFunction())

    addFunctions("Color", DictFunctions.getColor())
    addFunctions("OptColor", getOptColorFunction())

    addFunctions("Integer", DictFunctions.getInteger())
    addFunctions("OptInteger", getOptIntegerFunction())

    addFunctions("Number", DictFunctions.getNumber())
    addFunctions("OptNumber", getOptNumberFunction())

    addFunctions("String", DictFunctions.getString())
    addFunctions("OptString", getOptStringFunction())

    addFunctions("Url", DictFunctions.getUrl())
    addFunctions("OptUrl", getOptUrlFunction())
  }

  private mutating func addFunctions(
    _ typeName: String,
    _ function: Function
  ) {
    self["get\(typeName)FromDict"] = function
    self["getDict\(typeName)"] = function
  }
}

enum DictFunctions {
  static func getArray() -> SimpleFunction {
    FunctionVarBinary<Dict, String, [AnyHashable]> {
      try $0.getArray(path: $1)
    }
  }

  static func getBoolean() -> SimpleFunction {
    FunctionVarBinary<Dict, String, Bool> {
      try $0.getBoolean(path: $1)
    }
  }

  static func getColor() -> SimpleFunction {
    FunctionVarBinary<Dict, String, Color> {
      try $0.getColor(path: $1)
    }
  }

  static func getDict() -> SimpleFunction {
    FunctionVarBinary<Dict, String, Dict> {
      try $0.getDict(path: $1)
    }
  }

  static func getInteger() -> SimpleFunction {
    FunctionVarBinary<Dict, String, Int> {
      try $0.getInteger(path: $1)
    }
  }

  static func getNumber() -> SimpleFunction {
    FunctionVarBinary<Dict, String, Double> {
      try $0.getNumber(path: $1)
    }
  }

  static func getString() -> SimpleFunction {
    FunctionVarBinary<Dict, String, String> {
      try $0.getString(path: $1)
    }
  }

  static func getUrl() -> SimpleFunction {
    FunctionVarBinary<Dict, String, URL> {
      try $0.getUrl(path: $1)
    }
  }
}

private func getOptArrayFunction() -> Function {
  FunctionVarBinary<Dict, String, [AnyHashable]> { dict, path in
    (try? dict.getArray(path: path)) ?? []
  }
}

private func getOptBooleanFunction() -> Function {
  FunctionVarTernary<Bool, Dict, String, Bool> { fallback, dict, path in
    (try? dict.getBoolean(path: path)) ?? fallback
  }
}

private func getOptColorFunction() -> Function {
  OverloadedFunction(
    functions: [
      FunctionVarTernary<Color, Dict, String, Color> { fallback, dict, path in
        (try? dict.getColor(path: path)) ?? fallback
      },
      FunctionVarTernary<String, Dict, String, Color> { fallback, dict, path in
        if let value = try? dict.getColor(path: path) {
          return value
        }
        return Color.color(withHexString: fallback)!
      },
    ]
  )
}

private func getOptDictFunction() -> Function {
  FunctionVarBinary<Dict, String, Dict> { dict, path in
    (try? dict.getDict(path: path)) ?? [:]
  }
}

private func getOptIntegerFunction() -> Function {
  FunctionVarTernary<Int, Dict, String, Int> { fallback, dict, path in
    (try? dict.getInteger(path: path)) ?? fallback
  }
}

private func getOptNumberFunction() -> Function {
  FunctionVarTernary<Double, Dict, String, Double> { fallback, dict, path in
    (try? dict.getNumber(path: path)) ?? fallback
  }
}

private func getOptStringFunction() -> Function {
  FunctionVarTernary<String, Dict, String, String> { fallback, dict, path in
    (try? dict.getString(path: path)) ?? fallback
  }
}

private func getOptUrlFunction() -> Function {
  OverloadedFunction(
    functions: [
      FunctionVarTernary<URL, Dict, String, URL> { fallback, dict, path in
        (try? dict.getUrl(path: path)) ?? fallback
      },
      FunctionVarTernary<String, Dict, String, URL> { fallback, dict, path in
        if let value = try? dict.getUrl(path: path) {
          return value
        }
        return URL(string: fallback)!
      },
    ]
  )
}

extension Dict {
  fileprivate func getArray(path: [String]) throws -> [AnyHashable] {
    let value = try getValue(path: path)
    guard let dictValue = value as? [AnyHashable] else {
      throw incorrectTypeError("array", value)
    }
    return dictValue
  }

  fileprivate func getBoolean(path: [String]) throws -> Bool {
    let value = try getValue(path: path)
    guard value.isBool else {
      throw incorrectTypeError("boolean", value)
    }
    guard let boolValue = value as? Bool else {
      throw incorrectTypeError("boolean", value)
    }
    return boolValue
  }

  fileprivate func getColor(path: [String]) throws -> Color {
    let value = try getValue(path: path)
    guard let stringValue = value as? String else {
      throw incorrectTypeError("color", value)
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw CalcExpression.Error.message(
        "Unable to convert value to Color, expected format #AARRGGBB."
      )
    }
    return color
  }

  fileprivate func getDict(path: [String]) throws -> Dict {
    let value = try getValue(path: path)
    guard let dictValue = value as? Dict else {
      throw incorrectTypeError("dict", value)
    }
    return dictValue
  }

  fileprivate func getInteger(path: [String]) throws -> Int {
    let value = try getValue(path: path)
    if value.isBool {
      throw incorrectTypeError("integer", value)
    }
    guard let intValue = value as? Int else {
      if let doubleValue = value as? Double {
        if doubleValue < Double(Int.min) || doubleValue > Double(Int.max) {
          throw CalcExpression.Error.message("Integer overflow.")
        }
        throw CalcExpression.Error.message("Cannot convert value to integer.")
      }
      throw incorrectTypeError("integer", value)
    }
    return intValue
  }

  fileprivate func getNumber(path: [String]) throws -> Double {
    let value = try getValue(path: path)
    if value.isBool {
      throw incorrectTypeError("number", value)
    }
    if let numberValue = value as? Double {
      return numberValue
    }
    if let intValue = value as? Int {
      return Double(intValue)
    }
    throw incorrectTypeError("number", value)
  }

  fileprivate func getString(path: [String]) throws -> String {
    let value = try getValue(path: path)
    guard let stringValue = value as? String else {
      throw incorrectTypeError("string", value)
    }
    return stringValue
  }

  fileprivate func getUrl(path: [String]) throws -> URL {
    let value = try getValue(path: path)
    guard let stringValue = value as? String else {
      throw incorrectTypeError("url", value)
    }
    guard let url = URL(string: stringValue) else {
      throw incorrectTypeError("url", value)
    }
    return url
  }

  private func getValue(path: [String]) throws -> AnyHashable {
    var dictionary: Dict? = self
    var i = 0
    while i < path.count - 1 {
      let key = path[i]
      if let current = dictionary {
        dictionary = current[key] as? Dict
      } else {
        throw missingPropertyError(key)
      }
      i += 1
    }
    guard let key = path.last else {
      throw CalcExpression.Error.message("Path is wrong.")
    }
    if let result = dictionary?[key] {
      return result
    }
    throw missingPropertyError(key)
  }
}

private func incorrectTypeError(
  _ expectedType: String,
  _ value: AnyHashable
) -> CalcExpression.Error {
  .message("Incorrect value type: expected \"\(expectedType)\", got \"\(value.actualType)\".")
}

private func missingPropertyError(_ property: String) -> CalcExpression.Error {
  .message("Missing property \"\(property)\" in the dict.")
}
