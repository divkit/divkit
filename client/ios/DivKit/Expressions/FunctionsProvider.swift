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

  lazy var evaluators: ((CalcExpression.Symbol) -> AnyCalcExpression.SymbolEvaluator?) =
    lock.withLock {
      { [weak self] symbol in
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
        case let .function(name):
          guard let self else {
            return nil
          }
          return makeEvaluator(name: name, functions: self.functions, isMethod: false)
        case let .method(name):
          return makeEvaluator(name: name, functions: methods, isMethod: true)
        case .postfix:
          return nil
        }
      }
    }
}

private func makeEvaluator(
  name: String,
  functions: [String: Function],
  isMethod: Bool
) -> AnyCalcExpression.SymbolEvaluator? {
  { args in
    guard let function = functions[name] else {
      throw CalcExpression.Error.message(
        "Failed to evaluate [\(formatExpression(name, args, isMethod))]. Unknown function name: \(name)."
      )
    }
    do {
      return try function.invoke(args: args)
    } catch let error as CalcExpression.Error {
      let message = "Failed to evaluate [\(formatExpression(name, args, isMethod))]."
      if error == .noMatchingSignature {
        if args.count == 0 {
          throw CalcExpression.Error.message(
            "\(message) Non empty argument list is required for function '\(name)'."
          )
        }
        throw CalcExpression.Error.message(
          "\(message) Function '\(name)' has no matching override for given argument types: \(formatTypes(args, isMethod))."
        )
      }
      throw CalcExpression.Error.message("\(message) \(error.description)")
    }
  }
}

private func formatExpression(
  _ name: String,
  _ args: [Any],
  _ isMethod: Bool
) -> String {
  let argsString = args
    .enumerated()
    .filter { $0.offset > 0 || !isMethod }
    .map { formatValue($0.element) }
    .joined(separator: ", ")
  return "\(name)(\(argsString))"
}

private func formatValue(_ value: Any) -> String {
  switch value {
  case is String:
    "'\(value)'".replacingOccurrences(of: "\\", with: "\\\\")
  case is [Any]:
    "<array>"
  case is [String: Any]:
    "<dict>"
  default:
    AnyCalcExpression.stringify(value)
  }
}

private func formatTypes(
  _ args: [Any],
  _ isMethod: Bool
) -> String {
  args
    .enumerated()
    .filter { $0.offset > 0 || !isMethod }
    .map {
      switch $0.element {
      case is Double:
        "Number"
      case is Bool:
        "Boolean"
      default:
        "\(type(of: $0.element))"
      }
    }
    .joined(separator: ", ")
}

private let staticFunctions: [String: Function] = {
  var functions: [String: Function] = [:]
  CastFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  ColorFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  DatetimeFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  IntervalFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  MathFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  StringFunctions.allCases.forEach { functions[$0.rawValue] = $0.function }
  functions.addArrayFunctions()
  functions.addDictFunctions()
  functions.addToStringFunctions()
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

private let methods: [String: Function] = {
  var methods: [String: Function] = [:]
  methods.addGetMethods()
  methods.addToStringMethods()
  return methods
}()
