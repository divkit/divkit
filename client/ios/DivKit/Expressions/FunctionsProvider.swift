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

  init (
    cardId: DivCardID,
    variablesStorage: DivVariablesStorage,
    variableTracker: @escaping ExpressionResolver.VariableTracker,
    persistentValuesStorage: DivPersistentValuesStorage,
    prototypesStorage: [String: Any]? = nil
  ) {
    self.variableValueProvider = {
      if let value: Any = prototypesStorage?[$0] {
        return value
      }
      let variableName = DivVariableName(rawValue: $0)
      variableTracker([variableName])
      return variablesStorage.getVariableValue(cardId: cardId, name: variableName)
    }
    self.persistentValuesStorage = persistentValuesStorage
  }

  lazy var functions: [CalcExpression.Symbol: Function] =
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
          $0.getFunction(persistentValuesStorage.get)
        )
      }
      return functions
    }

  lazy var evaluators: ((CalcExpression.Symbol) -> AnyCalcExpression.SymbolEvaluator?) =
    lock.withLock {
      return { [weak self] symbol in
        switch symbol {
        case .variable("true"):
          { _ in true }
        case .variable("false"):
          { _ in false }
        case let .variable(name):
          // CalcExpression stores string values as Symbol.variable
          if name.starts(with: "'") {
            nil
          } else {
            self?.variableValueProvider(name).map { value in { _ in value } }
          }
        case .function, .method, .infix, .prefix:
          self?.functions[symbol]?.invoke
        case .postfix:
          nil
        }
      }
    }
}

private let staticFunctions: [CalcExpression.Symbol: Function] = {
  var functions: [CalcExpression.Symbol: Function] = [:]
  ArrayFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  CastFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  ColorFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  DatetimeFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  DictFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  IntervalFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  MathFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  StringFunctions.allCases.forEach { functions.put($0.rawValue, $0.function) }
  ComparisonOperators.allCases.forEach { functions[.infix($0.rawValue)] = $0.function }
  EqualityOperators.allCases.forEach { functions[.infix($0.rawValue)] = $0.function }
  BooleanOperators.allCases.forEach { functions[$0.symbol] = $0.function }
  MathOperators.allCases.forEach { functions[$0.symbol] = $0.function }
  ToStringFunctions.all.forEach { functions[$0] = $1 }
  return functions
}()

extension [CalcExpression.Symbol: Function] {
  fileprivate mutating func put(_ name: String, _ function: Function) {
    self[.function(name, arity: function.arity)] = function
  }
}
