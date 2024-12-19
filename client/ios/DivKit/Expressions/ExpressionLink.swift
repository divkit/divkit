import Foundation
import Serialization
import VGSL

public struct ExpressionLink<T: Sendable>: Sendable {
  enum Item {
    case string(String)
    case calcExpression(CalcExpression)
    case nestedExpression(ExpressionLink<String>)
  }

  let items: [Item]
  let variablesNames: [String]
  let rawValue: String
  let validator: AnyValueValidator<T>?

  @usableFromInline
  init?(
    rawValue: String,
    validator: AnyValueValidator<T>? = nil,
    errorTracker: ExpressionErrorTracker? = nil,
    resolveNested: Bool = true
  ) {
    guard rawValue.contains(expressionPrefix) else {
      return nil
    }

    var items = [Item]()

    func appendString(_ value: String) {
      if let unescapedValue = ExpressionValueConverter.unescape(value, errorTracker: errorTracker) {
        items.append(.string(unescapedValue))
      }
    }

    var index = rawValue.startIndex
    var variablesNames = [String]()
    var currentString = ""
    let endIndex = rawValue.endIndex
    while index < endIndex {
      let currentValue = rawValue[index..<endIndex]
      if rawValue.hasExpression(at: index) {
        guard let (start, end) = currentValue.makeLinkIndices() else {
          errorTracker?(ExpressionError("Error tokenizing '\(rawValue)'.", expression: rawValue))
          return nil
        }
        if !currentString.isEmpty {
          appendString(currentString)
          currentString = ""
        }
        if start <= end {
          let value = String(currentValue[start...end])
          if resolveNested, let link = ExpressionLink<String>(
            rawValue: value,
            errorTracker: errorTracker
          ) {
            items.append(.nestedExpression(link))
            variablesNames.append(contentsOf: link.variablesNames)
          } else {
            do {
              let expression = try CalcExpression.parse(value)
              items.append(.calcExpression(expression))
              variablesNames.append(contentsOf: expression.variableNames)
            } catch {
              errorTracker?(ExpressionError(error.localizedDescription, expression: rawValue))
              return nil
            }
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
          appendString(currentString)
        }
      }
    }
    self.items = items
    self.variablesNames = variablesNames
    self.rawValue = rawValue
    self.validator = validator
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
      if char == "'", notEscaped(at: i) {
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
