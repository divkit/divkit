import Foundation

import CommonCorePublic

public typealias ExpressionErrorTracker = (ExpressionError) -> Void

public final class ExpressionResolver {
  public typealias VariableTracker = (Set<DivVariableName>) -> Void

  private let variableValueProvider: AnyCalcExpression.ValueProvider
  private let functionsProvider: FunctionsProvider
  private let errorTracker: ExpressionErrorTracker
  private let variableTracker: VariableTracker

  init(
    variableValueProvider: @escaping AnyCalcExpression.ValueProvider,
    functionsProvider: FunctionsProvider,
    errorTracker: @escaping ExpressionErrorTracker,
    variableTracker: @escaping VariableTracker
  ) {
    self.variableValueProvider = variableValueProvider
    self.functionsProvider = functionsProvider
    self.errorTracker = errorTracker
    self.variableTracker = variableTracker
  }

  public init(
    variables: DivVariables,
    persistentValuesStorage: DivPersistentValuesStorage,
    errorTracker: ExpressionErrorTracker? = nil,
    variableTracker: @escaping VariableTracker = { _ in }
  ) {
    let variableValueProvider: AnyCalcExpression.ValueProvider = {
      variables[DivVariableName(rawValue: $0)]?.typedValue()
    }
    self.variableValueProvider = variableValueProvider
    self.functionsProvider = FunctionsProvider(
      variableValueProvider: {
        variableTracker([DivVariableName(rawValue: $0)])
        return variableValueProvider($0)
      },
      persistentValuesStorage: persistentValuesStorage
    )
    self.errorTracker = { errorTracker?($0) }
    self.variableTracker = variableTracker
  }

  public func resolveString(_ expression: String) -> String {
    resolveString(expression, initializer: { $0 }) ?? expression
  }

  public func resolveColor(_ expression: String) -> Color? {
    resolveString(expression, initializer: Color.color(withHexString:))
  }

  public func resolveUrl(_ expression: String) -> URL? {
    resolveString(expression, initializer: URL.init(string:))
  }

  public func resolveEnum<T: RawRepresentable>(
    _ expression: String
  ) -> T? where T.RawValue == String {
    resolveString(expression, initializer: T.init(rawValue:))
  }

  public func resolveNumeric<T>(_ expression: String) -> T? {
    if let link = try? ExpressionLink<T>(rawValue: expression) {
      return resolveNumeric(.link(link))
    }
    return nil
  }

  func resolveString<T>(
    _ expression: Expression<T>?,
    initializer: (String) -> T? = { $0 }
  ) -> T? {
    switch expression {
    case let .value(value):
      return resolveEscaping(value)
    case let .link(link):
      variableTracker(Set(link.variablesNames.map(DivVariableName.init(rawValue:))))
      return evaluateString(link: link, initializer: initializer)
    case .none:
      return nil
    }
  }

  func resolveEnum<T: RawRepresentable>(
    _ expression: Expression<T>?
  ) -> T? where T.RawValue == String {
    resolveString(expression, initializer: T.init(rawValue:))
  }

  func resolveColor(
    _ expression: Expression<Color>?
  ) -> Color? {
    resolveString(expression, initializer: Color.color(withHexString:))
  }

  func resolveUrl(
    _ expression: Expression<URL>?
  ) -> URL? {
    resolveString(expression, initializer: URL.init(string:))
  }

  func resolveNumeric<T>(
    _ expression: Expression<T>?
  ) -> T? {
    switch expression {
    case let .value(value):
      return value
    case let .link(link):
      variableTracker(Set(link.variablesNames.map(DivVariableName.init(rawValue:))))
      return evaluateSingleItem(link: link)
    case .none:
      return nil
    }
  }

  func resolveArray(
    _ expression: Expression<[Any]>?
  ) -> [Any]? {
    switch expression {
    case let .value(value):
      return value
    case let .link(link):
      variableTracker(Set(link.variablesNames.map(DivVariableName.init(rawValue:))))
      return evaluateSingleItem(link: link)
    case .none:
      return nil
    }
  }

  private func resolveEscaping<T>(_ value: T?) -> T? {
    guard var value = value as? String, value.contains("\\") else {
      return value
    }

    var index = value.startIndex
    let escapingValues = ["@{", "'", "\\"]

    while index < value.endIndex {
      if value[index] == "\\" {
        let nextIndex = value.index(index, offsetBy: 1)
        let next = value[nextIndex...]

        if let escaped = escapingValues.first(where: { next.starts(with: $0) }) {
          value.remove(at: index)
          index = value.index(index, offsetBy: escaped.count)
        } else {
          if next.isEmpty {
            errorTracker(ExpressionError("Error tokenizing '\(value)'.", expression: value))
          } else {
            errorTracker(ExpressionError("Incorrect string escape", expression: value))
          }
          return nil
        }
      } else {
        index = value.index(after: index)
      }
    }

    return value as? T
  }

  private func evaluateSingleItem<T>(link: ExpressionLink<T>) -> T? {
    guard link.items.count == 1,
          case let .calcExpression(parsedExpression) = link.items.first
    else {
      errorTracker(ExpressionError("Incorrect single item expression", expression: link.rawValue))
      return nil
    }
    do {
      return try validatedValue(value: evaluate(parsedExpression), link: link)
    } catch let error as CalcExpression.Error {
      let expression = parsedExpression.description
      errorTracker(
        ExpressionError(error.makeOutputMessage(for: expression), expression: link.rawValue)
      )
      return nil
    } catch {
      errorTracker(ExpressionError(error.localizedDescription, expression: link.rawValue))
      return nil
    }
  }

  private func evaluateString<T>(
    link: ExpressionLink<T>,
    initializer: (String) -> T? = { $0 }
  ) -> T? {
    var stringValue = ""
    for item in link.items {
      switch item {
      case let .calcExpression(parsedExpression):
        do {
          stringValue += try evaluate(parsedExpression)
        } catch let error as CalcExpression.Error {
          let expression = parsedExpression.description
          errorTracker(
            ExpressionError(error.makeOutputMessage(for: expression), expression: link.rawValue)
          )
          return nil
        } catch {
          errorTracker(ExpressionError(error.localizedDescription, expression: link.rawValue))
          return nil
        }
      case let .string(value):
        stringValue += value
      case let .nestedCalcExpression(link):
        if let expression = evaluateString(link: link) {
          let link = try? ExpressionLink<String>(
            rawValue: "@{\(expression)}",
            errorTracker: errorTracker,
            resolveNested: false
          )
          if let link, let value = evaluateString(link: link) {
            stringValue += value
          }
        }
      }
    }
    guard let result = initializer(stringValue) else {
      errorTracker(
        ExpressionError(
          "Failed to initalize \(T.self) from string value: \(stringValue)",
          expression: link.rawValue
        )
      )
      return nil
    }
    return validatedValue(value: result, link: link)
  }

  private func evaluate<T>(_ parsedExpression: ParsedCalcExpression) throws -> T {
    try AnyCalcExpression(
      parsedExpression,
      variables: variableValueProvider,
      functions: functionsProvider.functions
    ).evaluate()
  }

  private func validatedValue<T>(
    value: T?,
    link: ExpressionLink<T>
  ) -> T? {
    if let validator = link.validator, let value {
      if validator.isValid(value) {
        return value
      } else {
        errorTracker(
          ExpressionError("Failed to validate value: \(value)", expression: link.rawValue)
        )
        return nil
      }
    }
    return value
  }

  private func resolveString<T>(
    _ expression: String,
    initializer: (String) -> T?
  ) -> T? {
    if let link = try? ExpressionLink<T>(rawValue: expression) {
      return resolveString(.link(link), initializer: initializer)
    }
    return initializer(expression)
  }
}
