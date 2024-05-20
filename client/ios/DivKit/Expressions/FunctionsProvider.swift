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

  init(
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

  lazy var functions: [String: Function] =
    lock.withLock {
      var functions = staticFunctions
      for item in GetValueFunctions.allCases {
        functions[item.rawValue] = item.getFunction(variableValueProvider)
      }
      for item in GetStoredValueFunctions.allCases {
        functions[item.rawValue] = item.getFunction(persistentValuesStorage.get)
      }
      return functions
    }

  lazy var evaluators: ((CalcExpression.Symbol) -> Function?) =
    lock.withLock {
      { [weak self] symbol in
        switch symbol {
        case .variable("true"):
          return ConstantFunction(true)
        case .variable("false"):
          return ConstantFunction(false)
        case let .variable(name):
          if let value = self?.variableValueProvider(name) {
            return ConstantFunction(value)
          }
          return FunctionNullary {
            throw ExpressionError("Variable '\(name)' is missing.")
          }
        case .infix, .prefix:
          return operators[symbol]
        case let .function(name):
          guard let self else {
            return nil
          }
          return FunctionEvaluator(symbol, functions: self.functions)
        case let .method(name):
          return FunctionEvaluator(symbol, functions: methods)
        case .postfix:
          return nil
        }
      }
    }
}

private struct FunctionEvaluator: Function {
  private let symbol: CalcExpression.Symbol
  private let functions: [String: Function]

  init(_ symbol: CalcExpression.Symbol, functions: [String: Function]) {
    self.symbol = symbol
    self.functions = functions
  }

  func invoke(_ args: [Any]) throws -> Any {
    let name = symbol.name
    guard let function = functions[name] else {
      throw ExpressionError(
        "Failed to evaluate [\(symbol.formatExpression(args))]. Unknown \(symbol.type) name: \(name)."
      )
    }
    do {
      return try function.invoke(args)
    } catch {
      let message = "Failed to evaluate [\(symbol.formatExpression(args))]."
      let correctedArgs: [Any] = if case .method = symbol {
        Array(args.dropFirst())
      } else {
        args
      }
      if error is NoMatchingSignatureError {
        if correctedArgs.count == 0 {
          throw ExpressionError(
            "\(message) \(symbol.type.capitalized) requires non empty argument list."
          )
        }
        let argTypes = correctedArgs
          .map { formatTypeForError($0) }
          .joined(separator: ", ")
        throw ExpressionError(
          "\(message) \(symbol.type.capitalized) has no matching overload for given argument types: \(argTypes)."
        )
      }
      throw ExpressionError("\(message) \(error.localizedDescription)")
    }
  }
}

private let staticFunctions: [String: Function] = {
  var functions: [String: Function] = [:]
  MathFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  functions.addArrayFunctions()
  functions.addCastFunctions()
  functions.addColorFunctions()
  functions.addDateTimeFunctions()
  functions.addDictFunctions()
  functions.addIntervalFunctions()
  functions.addStringFunctions()
  functions.addToStringFunctions()
  return functions
}()

private let operators: [CalcExpression.Symbol: Function] = {
  var operators: [CalcExpression.Symbol: Function] = [:]
  ComparisonOperators.allCases.forEach { operators[.infix($0.rawValue)] = $0.function }
  EqualityOperators.allCases.forEach { operators[.infix($0.rawValue)] = $0.function }
  MathOperators.allCases.forEach { operators[$0.symbol] = $0.function }
  operators.addBooleanOperators()
  operators.addTryOperator()
  return operators
}()

private let methods: [String: Function] = {
  var methods: [String: Function] = [:]
  methods.addArrayMethods()
  methods.addDictMethods()
  methods.addToStringFunctions()
  return methods
}()

extension [String: Function] {
  mutating func addFunction(_ name: String, _ function: Function) {
    var functions: [SimpleFunction] = []
    if let existingFunction = self[name] {
      functions.appendFunctions(existingFunction)
    }
    functions.appendFunctions(function)
    if functions.count > 1 {
      self[name] = OverloadedFunction(functions: functions)
    } else if functions.count == 1 {
      self[name] = functions[0]
    }
  }
}

extension [SimpleFunction] {
  fileprivate mutating func appendFunctions(_ function: Function) {
    if let overloadedFunction = function as? OverloadedFunction {
      append(contentsOf: overloadedFunction.functions)
    } else if let simpleFunction = function as? SimpleFunction {
      append(simpleFunction)
    }
  }
}
