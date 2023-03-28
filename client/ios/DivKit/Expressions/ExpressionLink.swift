import Foundation

import CommonCorePublic

public struct ExpressionLink<T> {
  enum Item {
    case string(String)
    case calcExpression(ParsedCalcExpression)
    case nestedCalcExpression(ExpressionLink<String>)
  }

  let items: [Item]
  let variablesNames: [String]
  let rawValue: String
  let validator: ExpressionValueValidator<T>?
  let errorTracker: ExpressionErrorTracker?

  init?(
    expression: String,
    validator: ExpressionValueValidator<T>?,
    errorTracker: ExpressionErrorTracker? = nil,
    resolveNested: Bool = true
  ) throws {
    try self.init(
      rawValue: "@{\(expression)}",
      validator: validator,
      errorTracker: errorTracker,
      resolveNested: resolveNested
    )
  }

  @usableFromInline
  init?(
    rawValue: String,
    validator: ExpressionValueValidator<T>?,
    errorTracker: ExpressionErrorTracker? = nil,
    resolveNested: Bool = true
  ) throws {
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
          errorTracker?(.tokenizing(expression: rawValue))
          throw ExpressionError.tokenizing(expression: rawValue)
        }
        if !currentString.isEmpty {
          items.append(.string(currentString))
          currentString = ""
        }
        if start > end {
          items.append(.string(""))
        } else {
          let value = String(currentValue[start...end])
          if resolveNested, let link = try ExpressionLink<String>(
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
          if currentString == rawValue {
            return nil
          }
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
      if char == "'" && notEscaped(at: i) {
        stringStarted[nestedExpressionCounter] = !stringStarted[nestedExpressionCounter]!
      }
    }
    return nil
  }

  fileprivate func hasExpression(at i: String.Index) -> Bool {
    self[i...].hasPrefix(expressionPrefix) && notEscaped(at: i)
  }

  fileprivate func notEscaped(at i: String.Index) -> Bool {
    i == startIndex || self[index(before: i)] != "\\" || !notEscaped(at: index(before: i))
  }
}

extension ExpressionLink: Equatable {
  public static func ==(lhs: Self, rhs: Self) -> Bool {
    type(of: lhs) == type(of: rhs) && lhs.rawValue == rhs.rawValue
  }
}

extension ExpressionLink.Item: Equatable {
  static func ==(lhs: ExpressionLink.Item, rhs: ExpressionLink.Item) -> Bool {
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
