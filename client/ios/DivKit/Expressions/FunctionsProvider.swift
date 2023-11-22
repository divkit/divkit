import Foundation

import CommonCorePublic

final class FunctionsProvider {
  private let variableValueProvider: (String) -> Any?
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let lock = AllocatedUnfairLock()

  init(
    variableValueProvider: @escaping (String) -> Any?,
    persistentValuesStorage: DivPersistentValuesStorage
  ) {
    self.variableValueProvider = variableValueProvider
    self.persistentValuesStorage = persistentValuesStorage
  }

  lazy var functions: [AnyCalcExpression.Symbol: Function] =
    lock.withLock {
      var functions = staticFunctions
      GetValueFunctions.allCases.forEach {
        functions.put(
          $0.rawValue,
          $0.getFunction(variableValueProvider)
        )
      }
      GetStoredValueFunctions.allCases.forEach {
        functions.put(
          $0.rawValue,
          $0.getFunction { self.persistentValuesStorage.get(name: $0) }
        )
      }
      return functions
    }
}

private let staticFunctions: [AnyCalcExpression.Symbol: Function] = {
  var functions: [AnyCalcExpression.Symbol: Function] = [:]
  ArrayFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  CastFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  ColorFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  DatetimeFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  DictFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  IntervalFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  MathFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  StringFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  return functions
}()

extension Dictionary where Key == AnyCalcExpression.Symbol, Value == Function {
  fileprivate mutating func put(_ name: String, _ function: Function) {
    self[.function(name, arity: function.arity)] = function
  }
}
