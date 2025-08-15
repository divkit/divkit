import Foundation
import VGSL

extension [String: Function] {
  mutating func addGetValueFunctions() {
    addFunction("getBooleanValue", makeFunction() as FunctionBinary<String, Bool, Bool>)
    addFunction("getColorValue", _getColorValue)
    addFunction("getIntegerValue", makeFunction() as FunctionBinary<String, Int, Int>)
    addFunction("getNumberValue", makeFunction() as FunctionBinary<String, Double, Double>)
    addFunction("getStringValue", makeFunction() as FunctionBinary<String, String, String>)
    addFunction("getUrlValue", _getUrlValue)
  }
}

private let _getColorValue = OverloadedFunction(
  functions: [
    makeFunction() as FunctionBinary<String, Color, Color>,
    makeFunction {
      guard let color = Color.color(withHexString: $0) else {
        throw ExpressionError(
          "Failed to get Color from (\($0)). Unable to convert value to Color."
        )
      }
      return color
    } as FunctionBinary<String, String, Color>,
  ]
)

private let _getUrlValue = OverloadedFunction(
  functions: [
    makeFunction() as FunctionBinary<String, URL, URL>,
    makeFunction {
      guard let url = URL(string: $0) else {
        throw ExpressionError(
          "Failed to get URL from (\($0)). Unable to convert value to URL."
        )
      }
      return url
    } as FunctionBinary<String, String, URL>,
  ]
)

private func makeFunction<T>() -> FunctionBinary<String, T, T> {
  makeFunction { $0 }
}

private func makeFunction<T, U>(
  transform: @escaping (U) throws -> T
) -> FunctionBinary<String, U, T> {
  FunctionBinary { name, fallbackValue, context in
    guard let value = context.variableValueProvider(name) else {
      return try transform(fallbackValue)
    }
    let typpedValue: T? = value as? T
    if typpedValue == nil {
      DivKitLogger.warning("The type of the variable \(name) is not \(T.self)")
    }
    return try typpedValue ?? transform(fallbackValue)
  }
}
