import Foundation

import CommonCorePublic

private typealias Dict = [String: AnyHashable]

extension [String: Function] {
  mutating func addDictFunctions() {
    addFunction("getArrayFromDict", _getArray)
    addFunction("getOptArrayFromDict", _getOptArray)

    addFunction("getDictFromDict", _getDict)
    addFunction("getOptDictFromDict", _getOptDict)

    addFunctions("Boolean", _getBoolean)
    addFunctions("OptBoolean", _getOptBoolean)

    addFunctions("Color", _getColor)
    addFunctions("OptColor", _getOptColor)

    addFunctions("Integer", _getInteger)
    addFunctions("OptInteger", _getOptInteger)

    addFunctions("Number", _getNumber)
    addFunctions("OptNumber", _getOptNumber)

    addFunctions("String", _getString)
    addFunctions("OptString", _getOptString)

    addFunctions("Url", _getUrl)
    addFunctions("OptUrl", _getOptUrl)
  }

  mutating func addDictMethods() {
    addFunction("getArray", _getArray)
    addFunction("getBoolean", _getBoolean)
    addFunction("getColor", _getColor)
    addFunction("getDict", _getDict)
    addFunction("getInteger", _getInteger)
    addFunction("getNumber", _getNumber)
    addFunction("getString", _getString)
    addFunction("getUrl", _getUrl)
  }

  private mutating func addFunctions(
    _ typeName: String,
    _ function: Function
  ) {
    self["get\(typeName)FromDict"] = function
    self["getDict\(typeName)"] = function
  }
}

private let _getArray = FunctionVarBinary<Dict, String, [AnyHashable]> {
  try $0.getArray(path: $1)
}

private let _getBoolean = FunctionVarBinary<Dict, String, Bool> {
  try $0.getBoolean(path: $1)
}

private let _getColor = FunctionVarBinary<Dict, String, Color> {
  try $0.getColor(path: $1)
}

private let _getDict = FunctionVarBinary<Dict, String, Dict> {
  try $0.getDict(path: $1)
}

private let _getInteger = FunctionVarBinary<Dict, String, Int> {
  try $0.getInteger(path: $1)
}

private let _getNumber = FunctionVarBinary<Dict, String, Double> {
  try $0.getNumber(path: $1)
}

private let _getString = FunctionVarBinary<Dict, String, String> {
  try $0.getString(path: $1)
}

private let _getUrl = FunctionVarBinary<Dict, String, URL> {
  try $0.getUrl(path: $1)
}

private let _getOptArray = FunctionVarBinary<Dict, String, [AnyHashable]> {
  (try? $0.getArray(path: $1)) ?? []
}

private let _getOptBoolean = FunctionVarTernary<Bool, Dict, String, Bool> {
  (try? $1.getBoolean(path: $2)) ?? $0
}

private let _getOptColor = OverloadedFunction(functions: [
  FunctionVarTernary<Color, Dict, String, Color> {
    (try? $1.getColor(path: $2)) ?? $0
  },
  FunctionVarTernary<String, Dict, String, Color> {
    if let value = try? $1.getColor(path: $2) {
      return value
    }
    return Color.color(withHexString: $0)!
  },
])

private let _getOptDict = FunctionVarBinary<Dict, String, Dict> {
  (try? $0.getDict(path: $1)) ?? [:]
}

private let _getOptInteger = FunctionVarTernary<Int, Dict, String, Int> {
  (try? $1.getInteger(path: $2)) ?? $0
}

private let _getOptNumber = FunctionVarTernary<Double, Dict, String, Double> {
  (try? $1.getNumber(path: $2)) ?? $0
}

private let _getOptString = FunctionVarTernary<String, Dict, String, String> {
  (try? $1.getString(path: $2)) ?? $0
}

private let _getOptUrl = OverloadedFunction(functions: [
  FunctionVarTernary<URL, Dict, String, URL> {
    (try? $1.getUrl(path: $2)) ?? $0
  },
  FunctionVarTernary<String, Dict, String, URL> {
    if let value = try? $1.getUrl(path: $2) {
      return value
    }
    return URL(string: $0)!
  },
])

extension Dict {
  fileprivate func getArray(path: [String]) throws -> [AnyHashable] {
    let value = try getValue(path: path)
    guard let dictValue = value as? [AnyHashable] else {
      throw ExpressionError.incorrectType("Array", value)
    }
    return dictValue
  }

  fileprivate func getBoolean(path: [String]) throws -> Bool {
    let value = try getValue(path: path)
    guard value.isBool, let boolValue = value as? Bool else {
      throw ExpressionError.incorrectType("Boolean", value)
    }
    return boolValue
  }

  fileprivate func getColor(path: [String]) throws -> Color {
    let value = try getValue(path: path)
    guard let stringValue = value as? String else {
      throw ExpressionError.incorrectType("Color", value)
    }
    guard let color = Color.color(withHexString: stringValue) else {
      throw ExpressionError("Unable to convert value to Color, expected format #AARRGGBB.")
    }
    return color
  }

  fileprivate func getDict(path: [String]) throws -> Dict {
    let value = try getValue(path: path)
    guard let dictValue = value as? Dict else {
      throw ExpressionError.incorrectType("Dict", value)
    }
    return dictValue
  }

  fileprivate func getInteger(path: [String]) throws -> Int {
    let value = try getValue(path: path)
    if value.isBool {
      throw ExpressionError.incorrectType("Integer", value)
    }
    guard let intValue = value as? Int else {
      if let doubleValue = value as? Double {
        if doubleValue < Double(Int.min) || doubleValue > Double(Int.max) {
          throw ExpressionError.integerOverflow()
        }
        throw ExpressionError("Cannot convert value to integer.")
      }
      throw ExpressionError.incorrectType("Integer", value)
    }
    return intValue
  }

  fileprivate func getNumber(path: [String]) throws -> Double {
    let value = try getValue(path: path)
    if value.isBool {
      throw ExpressionError.incorrectType("Number", value)
    }
    if let numberValue = value as? Double {
      return numberValue
    }
    if let intValue = value as? Int {
      return Double(intValue)
    }
    throw ExpressionError.incorrectType("Number", value)
  }

  fileprivate func getString(path: [String]) throws -> String {
    let value = try getValue(path: path)
    guard let stringValue = value as? String else {
      throw ExpressionError.incorrectType("String", value)
    }
    return stringValue
  }

  fileprivate func getUrl(path: [String]) throws -> URL {
    let value = try getValue(path: path)
    guard
      let stringValue = value as? String,
      let url = URL(string: stringValue)
    else {
      throw ExpressionError.incorrectType("Url", value)
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
      throw ExpressionError("Path is wrong.")
    }
    if let result = dictionary?[key] {
      return result
    }
    throw missingPropertyError(key)
  }
}

private func missingPropertyError(_ property: String) -> Error {
  ExpressionError("Missing property \"\(property)\" in the dict.")
}
