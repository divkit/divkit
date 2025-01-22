//
//  CalcExpression.swift
//  Expression
//
//  Version 0.13.5
//
//  Created by Nick Lockwood on 15/09/2016.
//  Copyright © 2016 Nick Lockwood. All rights reserved.
//
//  Distributed under the permissive MIT license
//  Get the latest version from here:
//
//  https://github.com/nicklockwood/Expression
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.
//

import Foundation
import VGSL

struct CalcExpression {
  static func parse(_ expression: String) throws -> CalcExpression {
    var unicodeScalarView = UnicodeScalarView(expression.unicodeScalars)
    return try CalcExpression(root: unicodeScalarView.parseSubexpression(upTo: []))
  }

  private let root: Subexpression

  var variableNames: [String] {
    root.symbols.compactMap {
      if case let .variable(name) = $0 {
        return name
      }
      return nil
    }
  }

  func evaluate(_ context: ExpressionContext) throws -> Any {
    try root.evaluate(context)
  }
}

enum Subexpression {
  case literal(Any)
  case symbol(CalcExpression.Symbol, [Subexpression])

  var isOperand: Bool {
    switch self {
    case let .symbol(symbol, args) where args.isEmpty:
      switch symbol {
      case .infix, .prefix:
        false
      default:
        true
      }
    case .symbol, .literal:
      true
    }
  }

  func evaluate(_ context: ExpressionContext) throws -> Any {
    switch self {
    case let .literal(value):
      return value
    case let .symbol(symbol, args):
      if let evaluator = context.evaluators(symbol) {
        return try evaluator.invoke(args: args, context: context)
      }
      throw ExpressionError("Undefined symbol: \(symbol.name).")
    }
  }

  var symbols: Set<CalcExpression.Symbol> {
    switch self {
    case .literal:
      return []
    case let .symbol(symbol, subexpressions):
      var symbols = Set([symbol])
      for subexpression in subexpressions {
        symbols.formUnion(subexpression.symbols)
      }
      return symbols
    }
  }
}

// MARK: Expression parsing

// Workaround for horribly slow Substring.UnicodeScalarView perf
fileprivate struct UnicodeScalarView {
  typealias Index = String.UnicodeScalarView.Index

  private let characters: String.UnicodeScalarView
  private(set) var startIndex: Index
  private(set) var endIndex: Index

  init(_ unicodeScalars: String.UnicodeScalarView) {
    characters = unicodeScalars
    startIndex = characters.startIndex
    endIndex = characters.endIndex
  }

  private var first: UnicodeScalar? {
    isEmpty ? nil : characters[startIndex]
  }

  private var isEmpty: Bool {
    startIndex >= endIndex
  }

  private subscript(_ index: Index) -> UnicodeScalar {
    characters[index]
  }

  private func index(after index: Index) -> Index {
    characters.index(after: index)
  }

  func prefix(upTo index: Index) -> UnicodeScalarView {
    var view = UnicodeScalarView(characters)
    view.startIndex = startIndex
    view.endIndex = index
    return view
  }

  func suffix(from index: Index) -> UnicodeScalarView {
    var view = UnicodeScalarView(characters)
    view.startIndex = index
    view.endIndex = endIndex
    return view
  }

  private mutating func popFirst() -> UnicodeScalar? {
    if isEmpty {
      return nil
    }
    let char = characters[startIndex]
    startIndex = characters.index(after: startIndex)
    return char
  }

  /// Returns the remaining characters
  var unicodeScalars: Substring.UnicodeScalarView {
    characters[startIndex..<endIndex]
  }
}

extension String {
  fileprivate init(_ unicodeScalarView: DivKit.UnicodeScalarView) {
    self.init(unicodeScalarView.unicodeScalars)
  }
}

extension UnicodeScalarView {
  private enum Number {
    case number(String)
    case integer(String)

    var value: String {
      switch self {
      case let .number(value):
        value
      case let .integer(value):
        value
      }
    }
  }

  private mutating func scanCharacters(_ matching: (UnicodeScalar) -> Bool) -> String? {
    var index = startIndex
    while index < endIndex {
      if !matching(self[index]) {
        break
      }
      index = self.index(after: index)
    }
    if index > startIndex {
      let string = String(prefix(upTo: index))
      self = suffix(from: index)
      return string
    }
    return nil
  }

  private mutating func scanCharacter(
    _ matching: (UnicodeScalar) -> Bool = { _ in true }
  ) -> String? {
    if let c = first, matching(c) {
      self = suffix(from: index(after: startIndex))
      return String(c)
    }
    return nil
  }

  private mutating func scanCharacter(_ character: UnicodeScalar) -> Bool {
    scanCharacter { $0 == character } != nil
  }

  private mutating func scanToEndOfToken() -> String? {
    scanCharacters {
      switch $0 {
      case " ", "\t", "\n", "\r", "(", ")":
        false
      default:
        true
      }
    }
  }

  private mutating func skipWhitespace() -> Bool {
    if let _ = scanCharacters({
      switch $0 {
      case " ", "\t", "\n", "\r":
        true
      default:
        false
      }
    }) {
      return true
    }
    return false
  }

  private mutating func scanMethodName() -> String? {
    _ = skipWhitespace()
    var name = ""
    if let head = scanCharacter(isIdentifierHead) {
      name = head
    } else {
      return nil
    }
    if let tail = scanCharacters(isIdentifier) {
      name += tail
    }
    _ = skipWhitespace()
    return name
  }

  private mutating func parseDelimiter(_ delimiters: [String]) -> Bool {
    outer: for delimiter in delimiters {
      let start = self
      for char in delimiter.unicodeScalars {
        guard scanCharacter(char) else {
          self = start
          continue outer
        }
      }
      self = start
      return true
    }
    return false
  }

  private mutating func parseToken(isOperandExpected: Bool) throws -> Subexpression? {
    if !isOperandExpected, let op = parseOperator() {
      return op
    }
    if let identifier = try parseNumericLiteral() ?? parseIdentifier() ?? parseStringLiteral() {
      return identifier
    }
    if isOperandExpected {
      return parseOperator()
    }
    return nil
  }

  private mutating func parseNumericLiteral() throws -> Subexpression? {
    func scanInteger() -> String? {
      scanCharacters {
        if case "0"..."9" = $0 {
          return true
        }
        return false
      }
    }

    func scanExponent() -> String? {
      let start = self
      if let e = scanCharacter({ $0 == "e" || $0 == "E" }) {
        let sign = scanCharacter { $0 == "-" || $0 == "+" } ?? ""
        if let exponent = scanInteger() {
          return e + sign + exponent
        }
      }
      self = start
      return nil
    }

    func scanNumber() throws -> Number? {
      var number: Number
      var endOfInt = self
      let sign = scanCharacter { $0 == "-" } ?? ""
      if let integer = scanInteger() {
        endOfInt = self
        if scanCharacter(".") {
          guard let fraction = scanInteger() else {
            self = endOfInt
            return .integer(sign + integer)
          }
          number = .number("\(sign)\(integer).\(fraction)")
        } else {
          number = .integer(sign + integer)
        }
      } else if scanCharacter(".") {
        guard let fraction = scanInteger() else {
          self = endOfInt
          return nil
        }
        number = .number("\(sign).\(fraction)")
      } else {
        self = endOfInt
        return nil
      }
      if let exponent = scanExponent() {
        number = .number(number.value + exponent)
      }
      return number
    }

    guard let number = try scanNumber() else {
      return nil
    }
    switch number {
    case let .integer(value):
      guard let intValue = Int(value) else {
        throw ExpressionError("Value \(value) can't be converted to Integer type.")
      }
      return .literal(intValue)
    case let .number(value):
      guard let doubleValue = Double(value) else {
        throw ExpressionError.unexpectedToken(value)
      }
      return .literal(doubleValue)
    }
  }

  private mutating func parseOperator() -> Subexpression? {
    guard let op1 = scanCharacter({ "+-*/%=<>!&|?:(.".unicodeScalars.contains($0) }) else {
      return nil
    }

    let op2: String? = switch op1 {
    case "=", "<", ">":
      scanCharacter { $0 == "=" }
    case "|":
      scanCharacter { $0 == "|" }
    case "&":
      scanCharacter { $0 == "&" }
    case "!":
      scanCharacter { "=:".unicodeScalars.contains($0) }
    case "+", "-":
      scanCharacter { "+-".unicodeScalars.contains($0) }
    default:
      nil
    }

    if let op2 {
      return .symbol(.infix(op1 + op2), [])
    }

    return .symbol(.infix(op1), [])
  }

  private mutating func parseIdentifier() throws -> Subexpression? {
    var identifier = ""
    if let head = scanCharacter(isIdentifierHead) {
      identifier = head
    } else {
      return nil
    }

    var prevChar: UInt32 = 0
    if let tail = scanCharacters({
      let char = $0.value
      if char == 0x2E, prevChar == 0x2E {
        return false
      }
      prevChar = $0.value
      switch char {
      case 0x2E: // .
        return true
      default:
        return isIdentifier($0)
      }
    }) {
      identifier += tail
    }

    if prevChar == 0x2E {
      throw ExpressionError.unexpectedToken(".")
    }

    return makeVariable(identifier)
  }

  private mutating func parseStringLiteral() throws -> Subexpression? {
    if !scanCharacter("'") {
      return nil
    }
    var string = ""
    var part: String?
    repeat {
      part = scanCharacters { $0 != "'" && $0 != "\\" }
      string += part ?? ""
      if scanCharacter("\\"), let c = popFirst() {
        switch c {
        case "'", "\\":
          string.append(Character(c))
        case "@" where scanCharacter("{"):
          string += "@{"
        default:
          throw ExpressionError("Incorrect string escape.")
        }
        part = ""
      }
    } while part != nil
    if scanCharacter("'") {
      return .literal(string)
    }
    throw ExpressionError("Closing ' expected.")
  }

  mutating func parseSubexpression(
    upTo delimiters: [String]
  ) throws -> Subexpression {
    var stack: [Subexpression] = []

    func collapseStack(from i: Int) throws {
      guard stack.count > i + 1 else {
        return
      }
      let lhs = stack[i]
      let rhs = stack[i + 1]
      if lhs.isOperand {
        if rhs.isOperand {
          throw ExpressionError.unexpectedToken("\(rhs)")
        }
        if case let .symbol(symbol, _) = rhs {
          let rhs = stack[i + 2]
          if rhs.isOperand {
            if stack.count > i + 3 {
              switch stack[i + 3] {
              case let .symbol(.infix(op2), _),
                   let .symbol(.prefix(op2), _):
                guard stack.count > i + 4,
                      takesPrecedence(symbol.name, over: op2)
                else {
                  fallthrough
                }
              default:
                try collapseStack(from: i + 2)
                return
              }
            }
            stack[i...i + 2] = [.symbol(.infix(symbol.name), [lhs, rhs])]
            try collapseStack(from: 0)
          } else if case let .symbol(symbol2, _) = rhs {
            if case .prefix = symbol2 {
              try collapseStack(from: i + 2)
            } else {
              stack[i + 2] = .symbol(.prefix(symbol2.name), [])
              try collapseStack(from: i + 2)
            }
          }
        }
      } else if case let .symbol(symbol, _) = lhs {
        // Treat as prefix operator
        if rhs.isOperand {
          stack[i...i + 1] = [.symbol(.prefix(symbol.name), [rhs])]
          try collapseStack(from: 0)
        } else if case .symbol = rhs {
          // Nested prefix operator?
          try collapseStack(from: i + 1)
        }
      }
    }
    
    func collapseStackToSingleExpression() throws -> Subexpression {
      try collapseStack(from: 0)
      if let first = stack.first {
        if first.isOperand {
          return first
        }
        throw ExpressionError("Operand expected")
      }
      throw ExpressionError("Empty expression")
    }

    func scanArguments() throws -> [Subexpression] {
      let delimiter: Unicode.Scalar = ")"
      var args = [Subexpression]()
      if first != delimiter {
        let delimiters = [",", String(delimiter)]
        repeat {
          try args.append(parseSubexpression(upTo: delimiters))
        } while scanCharacter(",")
      }
      guard scanCharacter(delimiter) else {
        throw ExpressionError("Closing ) expected.")
      }
      return args
    }

    _ = skipWhitespace()
    var operandPosition = true
    var precededByWhitespace = true
    while !parseDelimiter(delimiters),
          let expression = try parseToken(isOperandExpected: operandPosition) {
      // Prepare for next iteration
      var followedByWhitespace = skipWhitespace() || isEmpty

      switch expression {
      case let .symbol(.infix(name), _):
        switch name {
        // ternary ?: operator
        case "?":
          let arg0 = try collapseStackToSingleExpression()
          let arg1 = try parseSubexpression(upTo: [":"])
          guard scanCharacter(":") else {
            throw ExpressionError(": expected.")
          }
          let arg2 = try parseSubexpression(upTo: delimiters)
          stack[stack.count - 1] = .symbol(.ternary, [arg0, arg1, arg2])
        // method call
        case ".":
          guard let lastSymbol = stack.last else {
            throw ExpressionError.unexpectedToken(".")
          }
          guard let methodName = scanMethodName(), scanCharacter("(") else {
            throw ExpressionError("Method expected after .")
          }
          var args = try scanArguments()
          args.insert(lastSymbol, at: 0)
          stack[stack.count - 1] = makeMethod(methodName, args)
        // function or method args
        case "(":
          switch stack.last {
          case let .symbol(.variable(name), _)?:
            var args = try scanArguments()
            let parts = name.split(separator: ".")
            if parts.count == 1 {
              // function()
              stack[stack.count - 1] = makeFunction(name, args)
            } else {
              // variable.method()
              let variableName = parts.dropLast().joined(separator: ".")
              args.insert(makeVariable(variableName), at: 0)
              stack[stack.count - 1] = makeMethod(String(parts.last!), args)
            }
          default:
            let subexpression = try parseSubexpression(upTo: [")"])
            stack.append(subexpression)
            guard scanCharacter(")") else {
              throw ExpressionError("Closing ) expected.")
            }
          }
          operandPosition = false
          followedByWhitespace = skipWhitespace()
        default:
          switch (precededByWhitespace, followedByWhitespace) {
          case (true, false):
            stack.append(.symbol(.prefix(name), []))
          default:
            stack.append(expression)
          }
          operandPosition = true
        }
      default:
        operandPosition = false
        stack.append(expression)
      }

      // Next iteration
      precededByWhitespace = followedByWhitespace
    }
    // Check for trailing junk
    let start = self
    if !parseDelimiter(delimiters), let junk = scanToEndOfToken() {
      self = start
      throw ExpressionError.unexpectedToken(junk)
    }
    return try collapseStackToSingleExpression()
  }
}

private let operatorPrecedence: [String: (
  precedence: Int,
  isRightAssociative: Bool
)] = {
  var precedences = [
    "[]": 100,
    "*": 1, "/": 1, "%": 1, // multiplication
    // +, -, |, ^, etc: 0 (also the default)
    "!:": -3,
    // comparison: -4
    "&&": -5, // and
    "||": -6, // or
    ":": -8, // ternary
  ].mapValues { ($0, false) }
  precedences["?"] = (-7, true) // ternary
  for op in ["<", "<=", ">=", ">", "==", "!="] { // comparison
    precedences[op] = (-4, true)
  }
  return precedences
}()

private func takesPrecedence(_ lhs: String, over rhs: String) -> Bool {
  let (p1, rightAssociative) = operatorPrecedence[lhs] ?? (0, false)
  let (p2, _) = operatorPrecedence[rhs] ?? (0, false)
  if p1 == p2 {
    return !rightAssociative
  }
  return p1 > p2
}

private func isIdentifierHead(_ c: UnicodeScalar) -> Bool {
  switch c.value {
  case 0x5F, // _
       0x41...0x5A, // A-Z
       0x61...0x7A: // a-z
    true
  default:
    false
  }
}

private func isIdentifier(_ c: UnicodeScalar) -> Bool {
  switch c.value {
  case 0x2E, // .
       0x30...0x39: // 0-9
    true
  default:
    isIdentifierHead(c)
  }
}

private func makeVariable(_ name: String) -> Subexpression {
  .symbol(.variable(name), [])
}

private func makeFunction(_ name: String, _ args: [Subexpression]) -> Subexpression {
  .symbol(.function(name), args)
}

private func makeMethod(_ name: String, _ args: [Subexpression]) -> Subexpression {
  .symbol(.method(name), args)
}
