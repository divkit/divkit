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
          return { _ in true }
        case .variable("false"):
          return { _ in false }
        case let .variable(name):
          // CalcExpression stores string values as Symbol.variable
          if name.starts(with: "'") {
            return nil
          }
          return self?.variableValueProvider(name).map { value in { _ in value } }
        case .infix, .prefix:
          return operators[symbol]?.invoke
        case let .function(name), let .method(name):
          guard let self else {
            return nil
          }
          return { args in
            guard let function = self.functions[symbol] else {
              throw CalcExpression.Error.message(
                "Failed to evaluate [\(formatExpression(name, args))]. Unknown function name: \(name)."
              )
            }
            do {
              return try function.invoke(args: args)
            } catch let error as CalcExpression.Error {
              let message = "Failed to evaluate [\(formatExpression(name, args))]."
              if error == .noMatchingSignature {
                if args.count == 0 {
                  throw CalcExpression.Error.message(
                    "\(message) Non empty argument list is required for function '\(name)'."
                  )
                }
                throw CalcExpression.Error.message(
                  "\(message) Function '\(name)' has no matching override for given argument types: \(formatTypes(args))."
                )
              }
              throw CalcExpression.Error.message(
                "\(message) \(error.description)"
              )
            }
          }
        case .postfix:
          return nil
        }
      }
    }
}

private func formatExpression(_ name: String, _ args: [Any]) -> String {
  let argsString = args
    .map { formatValue($0) }
    .joined(separator: ", ")
  return "\(name)(\(argsString))"
}

private func formatValue(_ value: Any) -> String {
  switch value {
  case is String:
    return "'\(value)'".replacingOccurrences(of: "\\", with: "\\\\")
  case is [Any]:
    return "<array>"
  case is [String: Any]:
    return "<dict>"
  default:
    return AnyCalcExpression.stringify(value)
  }
}

private func formatTypes(_ args: [Any]) -> String {
  args.map {
    switch $0 {
    case is Double:
      "Number"
    case is Bool:
      "Boolean"
    default:
      "\(type(of: $0))"
    }
  }.joined(separator: ", ")
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
  ToStringFunctions.all.forEach { functions[$0] = $1 }
  return functions
}()

private let operators: [CalcExpression.Symbol: Function] = {
  var operators: [CalcExpression.Symbol: Function] = [:]
  ComparisonOperators.allCases.forEach { operators[.infix($0.rawValue)] = $0.function }
  EqualityOperators.allCases.forEach { operators[.infix($0.rawValue)] = $0.function }
  BooleanOperators.allCases.forEach { operators[$0.symbol] = $0.function }
  MathOperators.allCases.forEach { operators[$0.symbol] = $0.function }
  return operators
}()

extension [CalcExpression.Symbol: Function] {
  fileprivate mutating func put(_ name: String, _ function: Function) {
    self[.function(name)] = function
  }
}
