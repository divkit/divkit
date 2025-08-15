import Foundation
import VGSL

final class FunctionsProvider {
  static let methods: [String: Function] = {
    var methods: [String: Function] = [:]
    methods.addArrayMethods()
    methods.addDictMethods()
    methods.addToStringFunctions()
    return methods
  }()

  lazy var functions: [String: Function] =
    lock.withLock {
      var functions = staticFunctions
      functions.addGetStoredValueFunctions(persistentValuesStorage.get)
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
          return FunctionNullary { context in
            if let value = context.variableValueProvider(name) {
              return value
            }
            throw ExpressionError("Variable '\(name)' is missing.")
          }
        case .infix, .prefix, .ternary:
          return operators[symbol]
        case let .function(name):
          guard let self else {
            return nil
          }
          return CustomFunctionEvaluator(
            symbol,
            fallbackEvaluator: FunctionEvaluator(
              symbol,
              functions: self.functions
            )
          )
        case let .method(name):
          return FunctionEvaluator(symbol, functions: FunctionsProvider.methods)
        }
      }
    }

  lazy var dynamicVariablesEvaluator: ((CalcExpression.Symbol) -> Function?) = { symbol in
    guard getValueFunctions.contains(symbol.name) else { return nil }
    return DynamicVariablesEvaluator()
  }

  private let persistentValuesStorage: DivPersistentValuesStorage
  private let lock = AllocatedUnfairLock()

  init(
    persistentValuesStorage: DivPersistentValuesStorage
  ) {
    self.persistentValuesStorage = persistentValuesStorage
  }

}

private struct CustomFunctionEvaluator: Function {
  private let symbol: CalcExpression.Symbol
  private let fallbackEvaluator: Function

  init(
    _ symbol: CalcExpression.Symbol,
    fallbackEvaluator: Function
  ) {
    self.symbol = symbol
    self.fallbackEvaluator = fallbackEvaluator
  }

  func invoke(_ args: [Any], context: ExpressionContext) throws -> Any {
    let name = symbol.name
    var customFunctionError: Error?
    if let customFunctionsStorage = context.customFunctionsStorageProvider(name) {
      var storage: DivFunctionsStorage? = customFunctionsStorage

      var result: Any?
      while result == nil, storage != nil, let functions = storage?.getFunctions(with: name),
            !functions.isEmpty {
        do {
          result = try OverloadedFunction(
            functions: functions
          ).invoke(args, context: context)
        } catch {
          if error is NoMatchingSignatureError {
            if functions.count == 1, let function = functions.first {
              customFunctionError = ExpressionError(
                "Failed to evaluate [\(symbol.formatExpression(args))]. Exactly \(function.signature.arguments.count) argument(s) expected."
              )
            } else {
              customFunctionError = ExpressionError(
                "Failed to evaluate [\(symbol.formatExpression(args))]. Function has no matching overload for given argument types: \(args.map { formatTypeForError($0) }.joined(separator: ", "))."
              )
            }
            storage = storage?.outerStorage
          } else {
            throw error
          }
        }
      }

      if let result {
        return result
      }
    }

    do {
      return try fallbackEvaluator.invoke(args, context: context)
    } catch {
      throw customFunctionError ?? error
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

  func invoke(_ args: [Any], context: ExpressionContext) throws -> Any {
    let name = symbol.name
    guard let function = functions[name] else {
      throw ExpressionError(
        "Failed to evaluate [\(symbol.formatExpression(args))]. Unknown \(symbol.type) name: \(name)."
      )
    }
    do {
      return try function.invoke(args, context: context)
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

private struct DynamicVariablesEvaluator: Function {
  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    guard let arg = args.first else {
      throw ExpressionError("There is no arguments in getValueFunction")
    }
    return arg
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
  functions.addGetValueFunctions()
  functions.addIntervalFunctions()
  functions.addStringFunctions()
  functions.addToStringFunctions()
  functions.addTrigonometricFunctions()
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

extension [String: Function] {
  mutating func addFunction(_ name: String, _ function: Function) {
    var functions: [SimpleFunction] = []
    if let existingFunction = self[name] {
      functions += existingFunction.simpleFunctions
    }
    functions += function.simpleFunctions
    if functions.count > 1 {
      self[name] = OverloadedFunction(functions: functions)
    } else if functions.count == 1 {
      self[name] = functions[0]
    }
  }
}

private let getValueFunctions = [
  "getBooleanValue",
  "getColorValue",
  "getIntegerValue",
  "getNumberValue",
  "getStringValue",
  "getUrlValue",
]
