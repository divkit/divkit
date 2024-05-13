import Foundation

import CommonCorePublic

public typealias ExpressionErrorTracker = (ExpressionError) -> Void

public final class ExpressionResolver {
  public typealias VariableTracker = (Set<DivVariableName>) -> Void

  private let functionsProvider: FunctionsProvider
  private let errorTracker: ExpressionErrorTracker

  init(
    functionsProvider: FunctionsProvider,
    errorTracker: @escaping ExpressionErrorTracker
  ) {
    self.functionsProvider = functionsProvider
    self.errorTracker = errorTracker
  }

  public init(
    variableValueProvider: @escaping (String) -> Any?,
    persistentValuesStorage: DivPersistentValuesStorage,
    errorTracker: @escaping ExpressionErrorTracker
  ) {
    self.functionsProvider = FunctionsProvider(
      variableValueProvider: variableValueProvider,
      persistentValuesStorage: persistentValuesStorage
    )
    self.errorTracker = errorTracker
  }

  public init(
    variables: DivVariables,
    persistentValuesStorage: DivPersistentValuesStorage,
    errorTracker: ExpressionErrorTracker? = nil,
    variableTracker: @escaping VariableTracker = { _ in }
  ) {
    self.functionsProvider = FunctionsProvider(
      variableValueProvider: {
        let variableName = DivVariableName(rawValue: $0)
        variableTracker([variableName])
        return variables[variableName]?.typedValue()
      },
      persistentValuesStorage: persistentValuesStorage
    )
    self.errorTracker = { errorTracker?($0) }
  }

  public func resolve(_ expression: String) -> Any? {
    if let link: ExpressionLink<Any> = makeLink(expression) {
      return resolveAnyLink(link)
    }
    return nil
  }

  public func resolveString(_ expression: String) -> String? {
    resolveString(expression, initializer: { $0 })
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
    if let link: ExpressionLink<T> = makeLink(expression) {
      return resolveNumeric(.link(link))
    }
    return nil
  }

  func resolveString(_ expression: Expression<String>?) -> String? {
    resolveString(expression, initializer: { $0 })
  }

  func resolveColor(_ expression: Expression<Color>?) -> Color? {
    resolveString(expression, initializer: Color.color(withHexString:))
  }

  func resolveUrl(_ expression: Expression<URL>?) -> URL? {
    resolveString(expression, initializer: URL.init(string:))
  }

  func resolveEnum<T: RawRepresentable>(
    _ expression: Expression<T>?
  ) -> T? where T.RawValue == String {
    resolveString(expression, initializer: T.init(rawValue:))
  }

  func resolveNumeric<T>(_ expression: Expression<T>?) -> T? {
    switch expression {
    case let .value(value):
      value
    case let .link(link):
      resolveNotStringBasedLink(link)
    case .none:
      nil
    }
  }

  func resolveArray(_ expression: Expression<[Any]>?) -> [Any]? {
    resolveNumeric(expression)
  }

  func resolveDict(_ expression: Expression<[String: Any]>?) -> [String: Any]? {
    resolveNumeric(expression)
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
          let distance = value.distance(from: value.startIndex, to: index)
          value.remove(at: index)
          index = value.index(value.startIndex, offsetBy: distance + escaped.count)
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

  private func resolveAnyLink(_ link: ExpressionLink<Any>) -> Any? {
    var result: Any?

    func appendResult(_ value: Any?) {
      guard let value else {
        return
      }
      if let prevResult = result {
        result = ExpressionValueConverter.stringify(prevResult)
          + ExpressionValueConverter.stringify(value)
      } else {
        result = value
      }
    }

    for item in link.items {
      switch item {
      case let .calcExpression(expression):
        do {
          try appendResult(evaluate(expression))
        } catch {
          errorTracker(ExpressionError(error.localizedDescription, expression: link.rawValue))
          return nil
        }
      case let .string(value):
        appendResult(value)
      case let .nestedExpression(expression):
        if let nestedLink: ExpressionLink<Any> = resolveNestedLink(expression) {
          appendResult(resolveAnyLink(nestedLink))
        }
      }
    }
    return result
  }

  private func resolveNotStringBasedLink<T>(_ link: ExpressionLink<T>) -> T? {
    guard link.items.count == 1, let item = link.items.first else {
      errorTracker(ExpressionError(
        "Not string based expression expected",
        expression: link.rawValue
      ))
      return nil
    }

    switch item {
    case let .calcExpression(expression):
      do {
        return try validate(evaluate(expression), link: link)
      } catch {
        errorTracker(ExpressionError(error.localizedDescription, expression: link.rawValue))
      }
    case let .nestedExpression(expression):
      if let nestedLink: ExpressionLink<T> = resolveNestedLink(expression) {
        return validate(resolveNotStringBasedLink(nestedLink), link: link)
      }
    case .string:
      errorTracker(ExpressionError("Expression expected", expression: link.rawValue))
    }
    return nil
  }

  private func resolveStringBasedLink<T>(
    _ link: ExpressionLink<T>,
    initializer: (String) -> T? = { $0 }
  ) -> T? {
    var stringValue = ""
    for item in link.items {
      switch item {
      case let .calcExpression(expression):
        do {
          if let value: String = try evaluate(expression) {
            stringValue += value
          }
        } catch {
          errorTracker(ExpressionError(error.localizedDescription, expression: link.rawValue))
          return nil
        }
      case let .string(value):
        stringValue += value
      case let .nestedExpression(expression):
        if let nestedLink: ExpressionLink<String> = resolveNestedLink(expression),
           let value: String = resolveStringBasedLink(nestedLink) {
          stringValue += value
        }
      }
    }

    if let value = initializer(stringValue) {
      return validate(value, link: link)
    }

    errorTracker(ExpressionError(
      "Failed to initialize \(T.self) from string: \(stringValue).",
      expression: link.rawValue
    ))
    return nil
  }

  private func resolveNestedLink<T>(_ nestedLink: ExpressionLink<String>) -> ExpressionLink<T>? {
    guard let expressionString: String = resolveStringBasedLink(nestedLink) else {
      return nil
    }
    return ExpressionLink<T>(
      rawValue: "@{\(expressionString)}",
      errorTracker: errorTracker,
      resolveNested: false
    )
  }

  private func evaluate<T>(_ expression: CalcExpression) throws -> T? {
    let value = try expression.evaluate(evaluators: functionsProvider.evaluators)
    if let castedValue: T = ExpressionValueConverter.cast(value) {
      return castedValue
    }
    throw ExpressionError(
      "Result type \(Swift.type(of: value)) is not compatible with expected type \(T.self)."
    )
  }

  private func validate<T>(_ value: T?, link: ExpressionLink<T>) -> T? {
    guard let validator = link.validator, let value else {
      return value
    }

    if validator.isValid(value) {
      return value
    }

    errorTracker(ExpressionError("Failed to validate value: \(value).", expression: link.rawValue))
    return nil
  }

  private func makeLink<T>(_ expression: String) -> ExpressionLink<T>? {
    ExpressionLink<T>(rawValue: expression, errorTracker: errorTracker)
  }

  private func resolveString<T>(
    _ expression: String,
    initializer: (String) -> T?
  ) -> T? {
    if let link: ExpressionLink<T> = makeLink(expression) {
      return resolveStringBasedLink(link, initializer: initializer)
    }
    return initializer(expression)
  }

  private func resolveString<T>(
    _ expression: Expression<T>?,
    initializer: (String) -> T?
  ) -> T? {
    switch expression {
    case let .value(value):
      resolveEscaping(value)
    case let .link(link):
      resolveStringBasedLink(link, initializer: initializer)
    case .none:
      nil
    }
  }
}
