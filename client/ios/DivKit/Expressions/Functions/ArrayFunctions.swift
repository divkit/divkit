import Foundation

import CommonCorePublic

private typealias Dict = [String: AnyHashable]

extension [String: Function] {
  mutating func addArrayFunctions() {
    self["getArrayFromArray"] = ArrayFunctions.getArray()
    self["getOptArrayFromArray"] = getOptArrayFunction()

    self["getDictFromArray"] = ArrayFunctions.getDict()
    self["getOptDictFromArray"] = getOptDictFunction()

    addFunctions("Boolean", ArrayFunctions.getBoolean())
    addFunctions("OptBoolean", getOptBooleanFunction())

    addFunctions("Color", ArrayFunctions.getColor())
    addFunctions("OptColor", getOptColorFunction())

    addFunctions("Integer", ArrayFunctions.getInteger())
    addFunctions("OptInteger", getOptIntegerFunction())

    addFunctions("Number", ArrayFunctions.getNumber())
    addFunctions("OptNumber", getOptNumberFunction())

    addFunctions("String", ArrayFunctions.getString())
    addFunctions("OptString", getOptStringFunction())

    addFunctions("Url", ArrayFunctions.getUrl())
    addFunctions("OptUrl", getOptUrlFunction())
  }

  private mutating func addFunctions(
    _ typeName: String,
    _ function: Function
  ) {
    self["get\(typeName)FromArray"] = function
    self["getArray\(typeName)"] = function
  }
}

enum ArrayFunctions {
  static func getArray() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, [AnyHashable]> {
      try $0.getArray(index: $1)
    }
  }

  static func getBoolean() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, Bool> {
      try $0.getBoolean(index: $1)
    }
  }

  static func getColor() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, Color> {
      try $0.getColor(index: $1)
    }
  }

  static func getDict() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, Dict> {
      try $0.getDict(index: $1)
    }
  }

  static func getInteger() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, Int> {
      try $0.getInteger(index: $1)
    }
  }

  static func getNumber() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, Double> {
      try $0.getNumber(index: $1)
    }
  }

  static func getString() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, String> {
      try $0.getString(index: $1)
    }
  }

  static func getUrl() -> SimpleFunction {
    FunctionBinary<[AnyHashable], Int, URL> {
      try $0.getUrl(index: $1)
    }
  }
}

private func getOptArrayFunction() -> Function {
  FunctionBinary<[AnyHashable], Int, [AnyHashable]> { array, index in
    (try? array.getArray(index: index)) ?? []
  }
}

private func getOptBooleanFunction() -> Function {
  FunctionTernary<[AnyHashable], Int, Bool, Bool> { array, index, fallback in
    (try? array.getBoolean(index: index)) ?? fallback
  }
}

private func getOptColorFunction() -> Function {
  OverloadedFunction(
    functions: [
      FunctionTernary<[AnyHashable], Int, Color, Color> { array, index, fallback in
        (try? array.getColor(index: index)) ?? fallback
      },
      FunctionTernary<[AnyHashable], Int, String, Color> { array, index, fallback in
        if let value = try? array.getColor(index: index) {
          return value
        }
        return Color.color(withHexString: fallback)!
      },
    ]
  )
}

private func getOptDictFunction() -> Function {
  FunctionBinary<[AnyHashable], Int, Dict> { array, index in
    (try? array.getDict(index: index)) ?? [:]
  }
}

private func getOptIntegerFunction() -> Function {
  FunctionTernary<[AnyHashable], Int, Int, Int> { array, index, fallback in
    (try? array.getInteger(index: index)) ?? fallback
  }
}

private func getOptNumberFunction() -> Function {
  FunctionTernary<[AnyHashable], Int, Double, Double> { array, index, fallback in
    (try? array.getNumber(index: index)) ?? fallback
  }
}

private func getOptStringFunction() -> Function {
  FunctionTernary<[AnyHashable], Int, String, String> { array, index, fallback in
    (try? array.getString(index: index)) ?? fallback
  }
}

private func getOptUrlFunction() -> Function {
  OverloadedFunction(
    functions: [
      FunctionTernary<[AnyHashable], Int, URL, URL> { array, index, fallback in
        (try? array.getUrl(index: index)) ?? fallback
      },
      FunctionTernary<[AnyHashable], Int, String, URL> { array, index, fallback in
        if let value = try? array.getUrl(index: index) {
          return value
        }
        return URL(string: fallback)!
      },
    ]
  )
}

extension [AnyHashable] {
  fileprivate func getArray(index: Int) throws -> [AnyHashable] {
    let value = try getValue(index: index)
    guard let arrayValue = value as? [AnyHashable] else {
      throw incorrectTypeError("array", value)
    }
    return arrayValue
  }

  fileprivate func getDict(index: Int) throws -> Dict {
    let value = try getValue(index: index)
    guard let dictValue = value as? Dict else {
      throw incorrectTypeError("dict", value)
    }
    return dictValue
  }

  fileprivate func getBoolean(index: Int) throws -> Bool {
    let value = try getValue(index: index)
    guard value.isBool else {
      throw incorrectTypeError("boolean", value)
    }
    guard let boolValue = value as? Bool else {
      throw incorrectTypeError("boolean", value)
    }
    return boolValue
  }

  fileprivate func getColor(index: Int) throws -> Color {
    let value = try getValue(index: index)
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

  fileprivate func getInteger(index: Int) throws -> Int {
    let value = try getValue(index: index)
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

  fileprivate func getNumber(index: Int) throws -> Double {
    let value = try getValue(index: index)
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

  fileprivate func getString(index: Int) throws -> String {
    let value = try getValue(index: index)
    guard let stringValue = value as? String else {
      throw incorrectTypeError("string", value)
    }
    return stringValue
  }

  fileprivate func getUrl(index: Int) throws -> URL {
    let value = try getValue(index: index)
    guard let stringValue = value as? String else {
      throw incorrectTypeError("url", value)
    }
    guard let url = URL(string: stringValue) else {
      throw incorrectTypeError("url", value)
    }
    return url
  }

  private func getValue(index: Int) throws -> AnyHashable {
    if index >= 0, index < count {
      return self[index]
    }
    throw CalcExpression.Error.message(
      "Requested index (\(index)) out of bounds array size (\(count))."
    )
  }
}

private func incorrectTypeError(
  _ expectedType: String,
  _ value: AnyHashable
) -> CalcExpression.Error {
  .message("Incorrect value type: expected \"\(expectedType)\", got \"\(value.actualType)\".")
}
