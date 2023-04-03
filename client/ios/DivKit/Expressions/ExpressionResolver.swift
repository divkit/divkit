import Foundation

import CommonCorePublic

public typealias ExpressionValueValidator<T> = (T) -> Bool

public typealias ExpressionErrorTracker = (ExpressionError) -> Void

public final class ExpressionResolver {
  @usableFromInline
  let variables: DivVariables
  private let errorTracker: ExpressionErrorTracker

  public init(
    variables: DivVariables,
    errorTracker: ExpressionErrorTracker? = nil
  ) {
    self.variables = variables
    self.errorTracker = {
      DivKitLogger.error($0.description)
      errorTracker?($0)
    }
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
      return evaluateStringBasedValue(
        link: link,
        initializer: initializer
      )
    case .none:
      return nil
    }
  }

  func resolveEscaping<T>(_ value: T?) -> T? {
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
            errorTracker(ExpressionError.tokenizing(expression: value))
          } else {
            errorTracker(ExpressionError.escaping)
          }
          return nil
        }
      } else {
        index = value.index(after: index)
      }
    }

    return value as? T
  }

  @inlinable
  func resolveNumericValue<T>(
    expression: Expression<T>?
  ) -> T? {
    switch expression {
    case let .value(val):
      return val
    case let .link(link):
      return evaluateSingleItem(
        link: link
      )
    case .none:
      return nil
    }
  }

  @usableFromInline
  func evaluateSingleItem<T>(link: ExpressionLink<T>) -> T? {
    guard link.items.count == 1,
          case let .calcExpression(parsedExpression) = link.items.first
    else {
      errorTracker(.incorrectSingleItemExpression(expression: link.rawValue, type: T.self))
      return nil
    }
    do {
      let result: T = try AnyCalcExpression(
        parsedExpression,
        constants: self,
        symbols: supportedFunctions
      ).evaluate()
      return validatedValue(value: result, validator: link.validator, rawValue: link.rawValue)
    } catch let error as CalcExpression.Error {
      let expression = parsedExpression.description
      errorTracker(
        .calculating(
          expression: link.rawValue,
          scriptInject: expression,
          description: error.makeOutputMessage(for: expression)
        )
      )
      return nil
    } catch {
      errorTracker(.unknown(error: error))
      return nil
    }
  }

  @usableFromInline
  func evaluateStringBasedValue<T>(
    link: ExpressionLink<T>,
    initializer: (String) -> T?
  ) -> T? {
    var stringValue = ""
    for item in link.items {
      switch item {
      case let .calcExpression(parsedExpression):
        do {
          let value: String = try AnyCalcExpression(
            parsedExpression,
            constants: self,
            symbols: supportedFunctions
          ).evaluate()
          stringValue += value
        } catch let error as CalcExpression.Error {
          let expression = parsedExpression.description
          errorTracker(
            .calculating(
              expression: link.rawValue,
              scriptInject: expression,
              description: error.makeOutputMessage(for: expression)
            )
          )
          return nil
        } catch {
          errorTracker(.unknown(error: error))
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
            expression: expression,
            validator: nil,
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
      errorTracker(.initializingValue(
        expression: link.rawValue,
        stringValue: stringValue,
        type: T.self
      ))
      return nil
    }
    return validatedValue(value: result, validator: link.validator, rawValue: link.rawValue)
  }

  @inlinable
  func getVariableValue<T>(_ name: String) -> T? {
    guard let value: T = variables[DivVariableName(rawValue: name)]?.typedValue() else {
      return nil
    }
    return value
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
        errorTracker(.validating(expression: rawValue, value: "\(value)", type: T.self))
        return nil
      }
    }
    return value
  }

  private func resolveStringBasedValue<T>(
    expression: String,
    initializer: (String) -> T?
  ) -> T? {
    guard let expressionLink = try? ExpressionLink<T>(rawValue: expression, validator: nil) else {
      return nil
    }
    return resolveStringBasedValue(
      expression: .link(expressionLink),
      initializer: initializer
    )
  }
}

extension ExpressionResolver: ConstantsProvider {
  func getValue(_ name: String) -> Any? {
    getVariableValue(name)
  }
}

private let supportedFunctions: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] =
  CastFunctions.allCases.map(\.declaration).flat()
    + StringFunctions.allCases.map(\.declaration).flat()
    + ColorFunctions.allCases.map(\.declaration).flat()
    + DatetimeFunctions.allCases.map(\.declaration).flat()
    + MathFunctions.allCases.map(\.declaration).flat()
    + IntervalFunctions.allCases.map(\.declaration).flat()

extension Array where Element == [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
  fileprivate func flat() -> [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    reduce([:], +)
  }
}
