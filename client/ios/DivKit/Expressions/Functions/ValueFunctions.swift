import BaseTinyPublic
import Foundation

fileprivate typealias GetOrDefaultWithTransform<U, T> = (String, U) throws -> T
fileprivate typealias GetOrDefault<T> = GetOrDefaultWithTransform<T, T>

enum ValueFunctions: String, CaseIterable {
  case getIntegerValue
  case getNumberValue
  case getStringValue
  case getUrlValue
  case getColorValue
  case getBooleanValue

  func getDeclaration(resolver: ExpressionResolver)
    -> [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [
      .function(
        rawValue,
        arity: getFunction(resolver: resolver).arity
      ): getFunction(resolver: resolver)
        .symbolEvaluator,
    ]
  }

  func getFunction(resolver: ExpressionResolver) -> Function {
    switch self {
    case .getIntegerValue:
      let function: GetOrDefault<Int> = resolver.getValueFunction()
      return FunctionBinary(impl: function)
    case .getNumberValue:
      let function: GetOrDefault<Double> = resolver.getValueFunction()
      return FunctionBinary(impl: function)
    case .getStringValue:
      let function: GetOrDefault<String> = resolver.getValueFunction()
      return FunctionBinary(impl: function)
    case .getUrlValue:
      let fromUrlFunction: GetOrDefault<URL> = resolver.getValueFunction()
      let fromStringFunction: GetOrDefaultWithTransform<String, URL> = resolver
        .getValueFunctionWithTransform {
          guard let url = URL(string: $0) else {
            throw AnyCalcExpression.Error.toURL($0)
          }
          return url
        }
      return OverloadedFunction(functions: [
        FunctionBinary(impl: fromUrlFunction),
        FunctionBinary(impl: fromStringFunction),
      ])
    case .getColorValue:
      let fromColorFunction: GetOrDefault<Color> = resolver.getValueFunction()
      let fromStringFunction: GetOrDefaultWithTransform<String, Color> = resolver
        .getValueFunctionWithTransform {
          guard let color = Color.color(withHexString: $0) else {
            throw AnyCalcExpression.Error.toColor($0)
          }
          return color
        }
      return OverloadedFunction(functions: [
        FunctionBinary(impl: fromColorFunction),
        FunctionBinary(impl: fromStringFunction),
      ])
    case .getBooleanValue:
      let function: GetOrDefault<Bool> = resolver.getValueFunction()
      return FunctionBinary(impl: function)
    }
  }
}

extension ExpressionResolver {
  fileprivate func getValueFunction<T>() -> GetOrDefault<T> {
    { name, fallbackValue in
      self.variableTracker([DivVariableName(rawValue: name)])
      guard let value = self.getValue(name) else {
        return fallbackValue
      }
      let typpedValue: T? = value as? T
      if typpedValue == nil {
        DivKitLogger.warning("The type of the variable \(name) is not \(T.self)")
      }
      return typpedValue ?? fallbackValue
    }
  }

  fileprivate func getValueFunctionWithTransform<T, U>(
    transform: @escaping (U) throws -> T
  ) -> GetOrDefaultWithTransform<U, T> {
    { name, fallbackValue in
      self.variableTracker([DivVariableName(rawValue: name)])
      guard let value = self.getValue(name) else {
        return try transform(fallbackValue)
      }
      let typpedValue: T? = value as? T
      if typpedValue == nil {
        DivKitLogger.warning("The type of the variable \(name) is not \(T.self)")
      }
      return try typpedValue ?? transform(fallbackValue)
    }
  }
}

extension AnyCalcExpression.Error {
  fileprivate static func toColor(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to get Color from (\(formatValue(value))). Unable to convert value to Color."
    )
  }

  fileprivate static func toURL(_ value: Any?) -> AnyCalcExpression.Error {
    .message(
      "Failed to get URL from (\(formatValue(value))). Unable to convert value to URL."
    )
  }

  private static func formatValue(_ value: Any?) -> String {
    (value as? CustomStringConvertible)?.description ?? ""
  }
}
