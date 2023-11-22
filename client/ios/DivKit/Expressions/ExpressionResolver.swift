import Foundation

import CommonCorePublic

public typealias ExpressionValueValidator<T> = (T) -> Bool

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

  public func resolveString(expression: String) -> String {
    resolveStringBasedValue(
      expression: expression,
      initializer: { $0 }
    ) ?? expression
  }

  public func resolveUrl(expression: String) -> URL? {
    resolveStringBasedValue(
      expression: expression,
      initializer: URL.init(string:)
    ) ?? URL(string: expression)
  }

  public func resolveColor(expression: String) -> Color? {
    resolveStringBasedValue(
      expression: expression,
      initializer: Color.color(withHexString:)
    ) ?? Color.color(withHexString: expression)
  }

  public func resolveEnum<T: RawRepresentable>(
    expression: String
  ) -> T? where T.RawValue == String {
    resolveStringBasedValue(
      expression: expression,
      initializer: T.init(rawValue:)
    ) ?? T(rawValue: expression)
  }

  func resolveStringBasedValue<T>(
    expression: Expression<T>?,
    initializer: (String) -> T?
  ) -> T? {
    switch expression {
    case let .value(val):
      return resolveEscaping(val)
    case let .link(link):
      variableTracker(Set(link.variablesNames.map(DivVariableName.init(rawValue:))))
      return evaluateStringBasedValue(
        link: link,
        initializer: initializer
      )
    case .none:
      return nil
    }
  }

  func resolveNumericValue<T>(
    expression: Expression<T>?
  ) -> T? {
    switch expression {
    case let .value(val):
      return val
    case let .link(link):
      variableTracker(Set(link.variablesNames.map(DivVariableName.init(rawValue:))))
      return evaluateSingleItem(link: link)
    case .none:
      return nil
    }
  }

  func resolveArrayValue(
    expression: Expression<[Any]>?
  ) -> [Any]? {
    switch expression {
    case let .value(val):
      return val
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
      return try validatedValue(
        value: evaluate(parsedExpression),
        validator: link.validator,
        rawValue: link.rawValue
      )
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

  private func evaluateStringBasedValue<T>(
    link: ExpressionLink<T>,
    initializer: (String) -> T?
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
        if let expression = evaluateStringBasedValue(
          link: link,
          initializer: { $0 }
        ) {
          let link = try? ExpressionLink<String>(
            rawValue: "@{\(expression)}",
            errorTracker: link.errorTracker,
            resolveNested: false
          )
          if let link = link, let value = evaluateStringBasedValue(
            link: link,
            initializer: { $0 }
          ) {
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
    return validatedValue(value: result, validator: link.validator, rawValue: link.rawValue)
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
    validator: ExpressionValueValidator<T>?,
    rawValue: String
  ) -> T? {
    if let validator = validator, let value = value {
      if validator(value) {
        return value
      } else {
        errorTracker(ExpressionError("Failed to validate value: \(value)", expression: rawValue))
        return nil
      }
    }
    return value
  }

  private func resolveStringBasedValue<T>(
    expression: String,
    initializer: (String) -> T?
  ) -> T? {
    guard let expressionLink = try? ExpressionLink<T>(rawValue: expression) else {
      return nil
    }
    return resolveStringBasedValue(
      expression: .link(expressionLink),
      initializer: initializer
    )
  }
}
