import BaseTinyPublic
import Foundation

enum GetStoredValueFunctions: String, CaseIterable {
  case getStoredIntegerValue
  case getStoredNumberValue
  case getStoredStringValue
  case getStoredUrlValue
  case getStoredColorValue
  case getStoredBooleanValue

  func getFunction(
    _ valueProvider: @escaping (String) -> Any?
  ) -> Function {
    switch self {
    case .getStoredIntegerValue:
      return makeFunction(valueProvider) as FunctionBinary<String, Int, Int>
    case .getStoredNumberValue:
      return makeFunction(valueProvider) as FunctionBinary<String, Double, Double>
    case .getStoredStringValue:
      return makeFunction(valueProvider) as FunctionBinary<String, String, String>
    case .getStoredUrlValue:
      let fromUrlFunction: FunctionBinary<String, URL, URL> = makeFunction(valueProvider)
      let fromStringFunction: FunctionBinary<String, String, URL> =
        makeFunction(valueProvider) {
          guard let url = URL(string: $0) else {
            throw CalcExpression.Error.message(
              "Failed to get URL from (\($0)). Unable to convert value to URL."
            )
          }
          return url
        }
      return OverloadedFunction(functions: [fromUrlFunction, fromStringFunction])
    case .getStoredColorValue:
      let fromColorFunction: FunctionBinary<String, Color, Color> = makeFunction(valueProvider)
      let fromStringFunction: FunctionBinary<String, String, Color> =
        makeFunction(valueProvider) {
          guard let color = Color.color(withHexString: $0) else {
            throw CalcExpression.Error.message(
              "Failed to get Color from (\($0)). Unable to convert value to Color."
            )
          }
          return color
        }
      return OverloadedFunction(functions: [fromColorFunction, fromStringFunction])
    case .getStoredBooleanValue:
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
    guard let value = valueProvider(name) as? T else {
      return try transform(fallbackValue)
    }
    return value
  }
}
