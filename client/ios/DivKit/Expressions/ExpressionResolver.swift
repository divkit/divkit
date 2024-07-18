import Foundation

import LayoutKit
import VGSL

public typealias ExpressionErrorTracker = (ExpressionError) -> Void

public final class ExpressionResolver {
  /// Depreacated.
  public typealias VariableTracker = (Set<DivVariableName>) -> Void

  private let functionsProvider: FunctionsProvider
  private let variableValueProvider: (String) -> Any?
  private let errorTracker: ExpressionErrorTracker

  private lazy var context = ExpressionContext(
    evaluators: functionsProvider.evaluators,
    variableValueProvider: variableValueProvider
  )

  init(
    functionsProvider: FunctionsProvider,
    variableValueProvider: @escaping (String) -> Any?,
    errorTracker: @escaping ExpressionErrorTracker
  ) {
    self.functionsProvider = functionsProvider
    self.variableValueProvider = variableValueProvider
    self.errorTracker = errorTracker
  }

  public init(
    variableValueProvider: @escaping (String) -> Any?,
    persistentValuesStorage: DivPersistentValuesStorage,
    errorTracker: @escaping ExpressionErrorTracker
  ) {
    self.functionsProvider = FunctionsProvider(
      persistentValuesStorage: persistentValuesStorage
    )
    self.variableValueProvider = variableValueProvider
    self.errorTracker = errorTracker
  }

  /// Deprecated. Use another initailizer.
  public init(
    variables: DivVariables,
    persistentValuesStorage: DivPersistentValuesStorage,
    errorTracker: ExpressionErrorTracker? = nil,
    variableTracker: @escaping VariableTracker = { _ in }
  ) {
    self.functionsProvider = FunctionsProvider(
      persistentValuesStorage: persistentValuesStorage
    )
    self.variableValueProvider = {
      let variableName = DivVariableName(rawValue: $0)
      variableTracker([variableName])
      return variables[variableName]?.typedValue()
    }
    self.errorTracker = { errorTracker?($0) }
  }

  init(
    path: UIElementPath,
    variablesStorage: DivVariablesStorage,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter
  ) {
    self.functionsProvider = FunctionsProvider(
      persistentValuesStorage: persistentValuesStorage
    )
    self.variableValueProvider = {
      let variableName = DivVariableName(rawValue: $0)
      return variablesStorage.getVariableValue(path: path, name: variableName)
    }
    self.errorTracker = reporter.asExpressionErrorTracker(cardId: path.cardId)
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
      "Failed to initialize \(formatTypeForError(T.self)) from string: \(stringValue).",
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
    let value = try expression.evaluate(context)
    if let castedValue: T = ExpressionValueConverter.cast(value) {
      return castedValue
    }
    throw ExpressionError(
      "Invalid result type: expected \(formatTypeForError(T.self)), got \(formatTypeForError(value))."
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
      if let stringValue = value as? String {
        return ExpressionValueConverter.unescape(stringValue, errorTracker: errorTracker) as? T
      }
      return value
    case let .link(link):
      return resolveStringBasedLink(link, initializer: initializer)
    case .none:
      return nil
    }
  }
}
