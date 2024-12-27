import Foundation
import VGSL

extension [String: Function] {
  mutating func addGetStoredValueFunctions(
    _ valueProvider: @escaping (String) -> Any?
  ) {
    addFunction("getStoredBooleanValue", makeFunction(Bool.self, valueProvider))
    addFunction("getStoredIntegerValue", makeFunction(Int.self, valueProvider))
    addFunction("getStoredNumberValue", makeFunction(Double.self, valueProvider))
    addFunction("getStoredStringValue", makeFunction(String.self, valueProvider))
    addFunction("getStoredColorValue", makeColorFunction(valueProvider))
    addFunction("getStoredUrlValue", makeUrlFunction(valueProvider))
    addFunction("getStoredArrayValue", makeFunctionWithoutFallback(DivArray.self, valueProvider))
    addFunction(
      "getStoredDictValue",
      makeFunctionWithoutFallback(DivDictionary.self, valueProvider)
    )
  }
}

private func makeFunction<T>(
  _: T.Type,
  _ valueProvider: @escaping (String) -> Any?
) -> FunctionBinary<String, T, T> {
  makeFunction(valueProvider) { $0 }
}

private func makeFunction<T, U>(
  _ valueProvider: @escaping (String) -> Any?,
  transform: @escaping (U) throws -> T
) -> FunctionBinary<String, U, T> {
  FunctionBinary { name, fallbackValue in
    guard let value = valueProvider(name) as? T else {
      return try transform(fallbackValue)
    }
    return value
  }
}

private func makeFunctionWithoutFallback<T>(
  _: T.Type,
  _ valueProvider: @escaping (String) -> Any?
) -> FunctionUnary<String, T> {
  FunctionUnary {
    if let value = valueProvider($0) as? T {
      return value
    }
    throw ExpressionError("Missing value.")
  }
}

private func makeColorFunction(_ valueProvider: @escaping (String) -> Any?) -> Function {
  let fromColorFunction = makeFunction(Color.self, valueProvider)
  let fromStringFunction: FunctionBinary<String, String, Color> =
    makeFunction(valueProvider) {
      guard let color = Color.color(withHexString: $0) else {
        throw ExpressionError(
          "Failed to get Color from (\($0)). Unable to convert value to Color."
        )
      }
      return color
    }
  return OverloadedFunction(functions: [fromColorFunction, fromStringFunction])
}

private func makeUrlFunction(_ valueProvider: @escaping (String) -> Any?) -> Function {
  let fromUrlFunction = makeFunction(URL.self, valueProvider)
  let fromStringFunction: FunctionBinary<String, String, URL> =
    makeFunction(valueProvider) {
      guard let url = URL(string: $0) else {
        throw ExpressionError(
          "Failed to get URL from (\($0)). Unable to convert value to URL."
        )
      }
      return url
    }
  return OverloadedFunction(functions: [fromUrlFunction, fromStringFunction])
}
