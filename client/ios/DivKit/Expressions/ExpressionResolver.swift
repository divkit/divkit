import Foundation

import CommonCore

extension ParsedCalcExpression {
  var variablesNames: [String] {
    symbols.filter {
      guard case .variable = $0 else { return false }
      return true
    }.map { $0.name }
  }
}

public struct ExpressionLink<T> {
  public enum Item {
    case string(String)
    case calcExpression(ParsedCalcExpression)
    case nestedCalcExpression(ExpressionLink<String>)
  }

  public let items: [Item]
  public let variablesNames: [String]
  public let rawValue: String
  public let validator: ExpressionValueValidator<T>?
  public let errorTracker: ExpressionErrorTracker?
  public init?(
    expression: String,
    validator: ExpressionValueValidator<T>?,
    errorTracker: ExpressionErrorTracker? = nil,
    resolveNested: Bool = true
  ) {
    self.init(
      rawValue: "@{\(expression)}",
      validator: validator,
      errorTracker: errorTracker,
      resolveNested: resolveNested
    )
  }

  public init?(
    rawValue: String,
    validator: ExpressionValueValidator<T>?,
    errorTracker: ExpressionErrorTracker? = nil,
    resolveNested: Bool = true
  ) {
    guard !rawValue.isEmpty else {
      errorTracker?(.emptyValue)
      return nil
    }
    guard rawValue.contains(expressionPrefix) else { return nil }
    var index = rawValue.startIndex
    var items = [Item]()
    var variablesNames = [String]()
    var currentString = ""
    let endIndex = rawValue.endIndex
    while index < endIndex {
      let currentValue = rawValue[index..<endIndex]
      if rawValue.hasExpression(at: index) {
        guard let (start, end) = currentValue.makeLinkIndices() else {
          errorTracker?(.incorrectExpression(expression: rawValue))
          return nil
        }
        if !currentString.isEmpty {
          items.append(.string(currentString))
          currentString = ""
        }
        if start > end {
          items.append(.string(""))
        } else {
          let value = String(currentValue[start...end])
          if resolveNested, let link = ExpressionLink<String>(
            rawValue: value,
            validator: nil,
            errorTracker: errorTracker
          ) {
            items.append(.nestedCalcExpression(link))
          } else {
            let parsedCalcExpression = CalcExpression.parse(value)
            items.append(.calcExpression(parsedCalcExpression))
            variablesNames.append(contentsOf: parsedCalcExpression.variablesNames)
          }
        }
        index = currentValue.index(end, offsetBy: 2)
      } else {
        currentString = currentString + String(currentValue[index])
        index = currentValue.index(after: index)
        if index == endIndex {
          items.append(.string(currentString))
        }
      }
    }
    self.items = items
    self.variablesNames = variablesNames
    self.rawValue = rawValue
    self.validator = validator
    self.errorTracker = errorTracker
  }
}

private let expressionPrefix = "@{"

extension StringProtocol {
  fileprivate func makeLinkIndices() -> (String.Index, String.Index)? {
    guard hasPrefix(expressionPrefix), let end = findExpressionPostfix() else { return nil }
    return (index(startIndex, offsetBy: expressionPrefix.count), index(before: end))
  }

  fileprivate func findExpressionPostfix() -> String.Index? {
    var nestedExpressionCounter = 0
    var stringStarted = [0: false]
    for i in self.indices {
      if i != startIndex, self.hasExpression(at: i) {
        nestedExpressionCounter += 1
        stringStarted[nestedExpressionCounter] = false
      }
      let char = self[i]
      if char == "}", !stringStarted[nestedExpressionCounter]! {
        if nestedExpressionCounter > 0 {
          nestedExpressionCounter -= 1
        } else {
          return i
        }
      }
      if char == "'" {
        if i == startIndex || self[index(before: i)] != "\\" {
          stringStarted[nestedExpressionCounter] = !stringStarted[nestedExpressionCounter]!
        }
      }
    }
    return nil
  }

  fileprivate func hasExpression(at i: String.Index) -> Bool {
    self[i...].hasPrefix(expressionPrefix) && (i == startIndex || self[index(before: i)] != "\\")
  }
}

extension ExpressionLink: Equatable {
  public static func ==(lhs: Self, rhs: Self) -> Bool {
    type(of: lhs) == type(of: rhs) && lhs.rawValue == rhs.rawValue
  }
}

extension ExpressionLink.Item: Equatable {
  public static func ==(lhs: ExpressionLink.Item, rhs: ExpressionLink.Item) -> Bool {
    switch (lhs, rhs) {
    case let (.calcExpression(left), .calcExpression(right)):
      return left.description == right.description
    case let (.string(left), .string(right)):
      return left == right
    case let (.nestedCalcExpression(left), .nestedCalcExpression(right)):
      return left == right
    case (.calcExpression, _), (.string, _), (.nestedCalcExpression, _):
      return false
    }
  }
}

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

  @inlinable
  public func resolveStringBasedValue<T>(
    expression: Expression<T>?,
    initializer: (String) -> T?
  ) -> T? {
    switch expression {
    case let .value(val):
      return val
    case let .link(link):
      return evaluateStringBasedValue(
        link: link,
        initializer: initializer
      )
    case .none:
      return nil
    }
  }

  @inlinable
  public func resolveNumericValue<T>(
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
}

private let supportedFunctions: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] =
  CastFunctions.allCases.map(\.declaration).flat()
    + StringFunctions.allCases.map(\.declaration).flat()
    + ColorFunctions.allCases.map(\.declaration).flat()
    + DatetimeFunctions.allCases.map(\.declaration).flat()
    + MathFunctions.allCases.map(\.declaration).flat()

extension Array where Element == [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
  fileprivate func flat() -> [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    reduce([:], +)
  }
}

extension ExpressionResolver {
  @usableFromInline
  func evaluateSingleItem<T>(link: ExpressionLink<T>) -> T? {
    guard link.items.count == 1,
          case let .calcExpression(parsedExpression) = link.items.first
    else {
      errorTracker(.incorrectSingleItemExpression(expression: link.rawValue, type: T.self))
      return nil
    }
    do {
      let constants = { [variables] in variables[DivVariableName(rawValue: $0)] }
      let result: T = try AnyCalcExpression(
        parsedExpression,
        constants: constants,
        symbols: supportedFunctions
      ).evaluate()
      return validatedValue(value: result, validator: link.validator, rawValue: link.rawValue)
    } catch let error as CalcExpression.Error {
      errorTracker(
        .calculating(
          expression: link.rawValue,
          scriptInject: parsedExpression.description,
          description: error.description
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
          let constants = { [variables] in variables[DivVariableName(rawValue: $0)] }
          let value: String = try AnyCalcExpression(
            parsedExpression,
            constants: constants,
            symbols: supportedFunctions
          ).evaluate()
          stringValue += value
        } catch let error as CalcExpression.Error {
          errorTracker(
            .calculating(
              expression: link.rawValue,
              scriptInject: parsedExpression.description,
              description: error.description
            )
          )
          return nil
        } catch {
          errorTracker(.unknown(error: error))
          return nil
        }
      case let .string(value):
        stringValue += value.replacingOccurrences(of: "\\@{", with: "@{")
      case let .nestedCalcExpression(link):
        if let expression = evaluateStringBasedValue(
          link: link,
          initializer: { $0 }
        ) {
          let link = ExpressionLink<String>(
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

  @inlinable
  public func getVariableValue<T>(name: String) -> T? {
    guard let value: T = variables[DivVariableName(rawValue: name)]?.typedValue() else {
      assertionFailure("Can't get variable from context")
      return nil
    }
    return value
  }
}
