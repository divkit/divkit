import BaseTinyPublic
import Foundation

enum GetValueFunctions: String, CaseIterable {
  case getIntegerValue
  case getNumberValue
  case getStringValue
  case getUrlValue
  case getColorValue
  case getBooleanValue

  func getFunction(
    _ valueProvider: @escaping (String) -> Any?
  ) -> Function {
    switch self {
    case .getIntegerValue:
      return makeFunction(valueProvider) as FunctionBinary<String, Int, Int>
    case .getNumberValue:
      return makeFunction(valueProvider) as FunctionBinary<String, Double, Double>
    case .getStringValue:
      return makeFunction(valueProvider) as FunctionBinary<String, String, String>
    case .getUrlValue:
      let fromUrlFunction: FunctionBinary<String, URL, URL> = makeFunction(valueProvider)
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
    case .getColorValue:
      let fromColorFunction: FunctionBinary<String, Color, Color> = makeFunction(valueProvider)
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
    case .getBooleanValue:
      return makeFunction(valueProvider) as FunctionBinary<String, Bool, Bool>
    }
  }
}

private func makeFunction<T>(
  _ valueProvider: @escaping (String) -> Any?
) -> FunctionBinary<String, T, T> {
  makeFunction(valueProvider) { $0 }
}

private func makeFunction<T, U>(
  _ valueProvider: @escaping (String) -> Any?,
  transform: @escaping (U) throws -> T
) -> FunctionBinary<String, U, T> {
  FunctionBinary { name, fallbackValue in
    guard let value = valueProvider(name) else {
      return try transform(fallbackValue)
    }
    let typpedValue: T? = value as? T
    if typpedValue == nil {
      DivKitLogger.warning("The type of the variable \(name) is not \(T.self)")
    }
    return try typpedValue ?? transform(fallbackValue)
  }
}
