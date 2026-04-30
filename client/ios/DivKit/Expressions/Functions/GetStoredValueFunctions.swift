import Foundation
import VGSL

extension [String: Function] {
  mutating func addGetStoredValueFunctions(
    _ valueProvider: @escaping (String, DivStoredValueScope) -> Any?
  ) {
    addFunction("getStoredBooleanValue", makeFunction(Bool.self, valueProvider))
    addFunction("getStoredBooleanValue", makeTernaryWithScope(Bool.self, valueProvider))
    addFunction("getStoredIntegerValue", makeFunction(Int.self, valueProvider))
    addFunction("getStoredIntegerValue", makeTernaryWithScope(Int.self, valueProvider))
    addFunction("getStoredNumberValue", makeFunction(Double.self, valueProvider))
    addFunction("getStoredNumberValue", makeTernaryWithScope(Double.self, valueProvider))
    addFunction("getStoredStringValue", makeFunction(String.self, valueProvider))
    addFunction("getStoredStringValue", makeTernaryWithScope(String.self, valueProvider))
    addFunction("getStoredColorValue", makeColorFunction(valueProvider))
    addFunction("getStoredUrlValue", makeUrlFunction(valueProvider))
    addFunction(
      "getStoredArrayValue",
      makeFunctionWithoutFallback(DivArray.self, valueProvider)
    )
    addFunction(
      "getStoredDictValue",
      makeFunctionWithoutFallback(DivDictionary.self, valueProvider)
    )
  }
}

private func parseStoredValueScope(_ raw: String) throws -> DivStoredValueScope {
  switch raw.lowercased() {
  case "global":
    return .global
  case "card":
    return .card
  default:
    throw ExpressionError("Unknown stored value scope '\(raw)'. Expected 'global' or 'card'.")
  }
}

private func makeFunction<T>(
  _: T.Type,
  _ valueProvider: @escaping (String, DivStoredValueScope) -> Any?
) -> FunctionBinary<String, T, T> {
  makeFunction(valueProvider) { $0 }
}

private func makeFunction<T, U>(
  _ valueProvider: @escaping (String, DivStoredValueScope) -> Any?,
  transform: @escaping (U) throws -> T
) -> FunctionBinary<String, U, T> {
  FunctionBinary { name, fallbackValue in
    guard let value = valueProvider(name, .global) as? T else {
      return try transform(fallbackValue)
    }
    return value
  }
}

private func makeTernaryWithScope<T>(
  _: T.Type,
  _ valueProvider: @escaping (String, DivStoredValueScope) -> Any?
) -> FunctionTernary<String, String, T, T> {
  makeTernaryWithScope(valueProvider) { $0 }
}

private func makeTernaryWithScope<T, U>(
  _ valueProvider: @escaping (String, DivStoredValueScope) -> Any?,
  transform: @escaping (U) throws -> T
) -> FunctionTernary<String, String, U, T> {
  FunctionTernary { name, scopeString, fallbackValue in
    let scope = try parseStoredValueScope(scopeString)
    guard let value = valueProvider(name, scope) as? T else {
      return try transform(fallbackValue)
    }
    return value
  }
}

private func makeFunctionWithoutFallback<T>(
  _: T.Type,
  _ valueProvider: @escaping (String, DivStoredValueScope) -> Any?
) -> Function {
  let unary: FunctionUnary<String, T> = FunctionUnary {
    if let value = valueProvider($0, .global) as? T {
      return value
    }
    throw ExpressionError("Missing value.")
  }
  let binary: FunctionBinary<String, String, T> = FunctionBinary { name, scopeString in
    let scope = try parseStoredValueScope(scopeString)
    if let value = valueProvider(name, scope) as? T {
      return value
    }
    throw ExpressionError("Missing value.")
  }
  return OverloadedFunction(functions: [unary, binary])
}

private func makeColorFunction(_ valueProvider: @escaping (String, DivStoredValueScope) -> Any?) -> Function {
  let fromColorBinary: FunctionBinary<String, Color, Color> = makeFunction(Color.self, valueProvider)
  let fromStringBinary: FunctionBinary<String, String, Color> =
    makeFunction(valueProvider) {
      guard let color = Color.color(withHexString: $0) else {
        throw ExpressionError(
          "Failed to get Color from (\($0)). Unable to convert value to Color."
        )
      }
      return color
    }
  let fromColorTernary: FunctionTernary<String, String, Color, Color> =
    makeTernaryWithScope(Color.self, valueProvider)
  let fromStringTernary: FunctionTernary<String, String, String, Color> =
    makeTernaryWithScope(valueProvider) {
      guard let color = Color.color(withHexString: $0) else {
        throw ExpressionError(
          "Failed to get Color from (\($0)). Unable to convert value to Color."
        )
      }
      return color
    }
  return OverloadedFunction(functions: [
    fromColorBinary,
    fromStringBinary,
    fromColorTernary,
    fromStringTernary,
  ])
}

private func makeUrlFunction(_ valueProvider: @escaping (String, DivStoredValueScope) -> Any?) -> Function {
  let fromUrlBinary: FunctionBinary<String, URL, URL> = makeFunction(URL.self, valueProvider)
  let fromStringBinary: FunctionBinary<String, String, URL> =
    makeFunction(valueProvider) {
      guard let url = URL(string: $0) else {
        throw ExpressionError(
          "Failed to get URL from (\($0)). Unable to convert value to URL."
        )
      }
      return url
    }
  let fromUrlTernary: FunctionTernary<String, String, URL, URL> =
    makeTernaryWithScope(URL.self, valueProvider)
  let fromStringTernary: FunctionTernary<String, String, String, URL> =
    makeTernaryWithScope(valueProvider) {
      guard let url = URL(string: $0) else {
        throw ExpressionError(
          "Failed to get URL from (\($0)). Unable to convert value to URL."
        )
      }
      return url
    }
  return OverloadedFunction(functions: [
    fromUrlBinary,
    fromStringBinary,
    fromUrlTernary,
    fromStringTernary,
  ])
}
