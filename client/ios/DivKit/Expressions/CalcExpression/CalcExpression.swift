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

import CommonCorePublic

struct CalcExpression {
  typealias SymbolEvaluator = (_ args: [Any]) throws -> Any

  static func parse(_ expression: String) -> CalcExpression {
    var unicodeScalarView = UnicodeScalarView(expression.unicodeScalars)
    let start = unicodeScalarView
    var subexpression: Subexpression
    do {
      subexpression = try unicodeScalarView.parseSubexpression(upTo: [])
    } catch {
      let expression = String(start.prefix(upTo: unicodeScalarView.startIndex))
      subexpression = .error(error as! Error, expression)
    }
    return CalcExpression(root: subexpression)
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
  
  func evaluate<T>(
    evaluators: @escaping (Symbol) -> SymbolEvaluator?
  ) throws -> T {
    let value = root.evaluate(evaluators)
    if let castedValue: T = CalcExpression.cast(value) {
      return castedValue
    }

    if let error = value as? Error {
      throw error
    }

    if T.self is String.Type {
      return CalcExpression.stringify(value) as! T
    }

    throw CalcExpression.Error.message(
      "Result type \(Swift.type(of: value)) is not compatible with expected type \(T.self)"
    )
  }
  
  private static func cast<T>(_ anyValue: Any) -> T? {
    if let value = anyValue as? T {
      return value
    }

    var type: Any.Type = T.self
    if let optionalType = type as? _Optional.Type {
      type = optionalType.wrappedType
    }
    switch type {
    case let numericType as _Numeric.Type:
      if anyValue is Bool {
        return nil
      }
      return (anyValue as? NSNumber).map { numericType.init(truncating: $0) as! T }
    default:
      return nil
    }
  }
}

private enum Subexpression: CustomStringConvertible {
  case literal(Any)
  case symbol(CalcExpression.Symbol, [Subexpression])
  case error(CalcExpression.Error, String)

  var isOperand: Bool {
    switch self {
    case let .symbol(symbol, args) where args.isEmpty:
      switch symbol {
      case .infix, .prefix, .postfix:
        false
      default:
        true
      }
    case .symbol, .literal:
      true
    case .error:
      false
    }
  }

  func evaluate(
    _ evaluators: (CalcExpression.Symbol) -> CalcExpression.SymbolEvaluator?
  ) -> Any {
    switch self {
    case let .literal(value):
      return value
    case let .symbol(symbol, args):
      guard let evaluator = evaluators(symbol) else {
        return CalcExpression.Error.message("Undefined symbol: \(symbol.name)")
      }
      do {
        return try evaluator(args.map { $0.evaluate(evaluators) })
      } catch {
        return error
      }
    case let .error(error, _):
      return error
    }
  }

  var description: String {
    func arguments(_ args: any Sequence<Subexpression>) -> String {
      args.map(\.description)
        .joined(separator: ", ")
    }
    switch self {
    case let .literal(value):
      return CalcExpression.stringify(value)
    case let .symbol(symbol, args):
      guard isOperand else {
        return symbol.name
      }
      func needsSeparation(_ lhs: String, _ rhs: String) -> Bool {
        let lhs = lhs.unicodeScalars.last!, rhs = rhs.unicodeScalars.first!
        return isOperator(lhs) == isOperator(rhs)
      }
      switch symbol {
      case let .prefix(name):
        let arg = args[0]
        let description = "\(arg)"
        switch arg {
        case .symbol(.infix, _), .symbol(.postfix, _), .error,
             .symbol where needsSeparation(name, description):
          return "\(symbol.name)(\(description))" // Parens required
        case .symbol, .literal:
          return "\(symbol.name)\(description)" // No parens needed
        }
      case let .postfix(name):
        let arg = args[0]
        let description = "\(arg)"
        switch arg {
        case .symbol(.infix, _), .symbol(.postfix, _), .error,
             .symbol where needsSeparation(description, name):
          return "(\(description))\(symbol.name)" // Parens required
        case .symbol, .literal:
          return "\(description)\(symbol.name)" // No parens needed
        }
      case .infix("?:") where args.count == 3:
        return "\(args[0]) ? \(args[1]) : \(args[2])"
      case let .infix(name):
        let lhs = args[0]
        let lhsDescription = switch lhs {
        case let .symbol(.infix(opName), _)
          where !takesPrecedence(opName, over: name):
          "(\(lhs))"
        default:
          "\(lhs)"
        }
        let rhs = args[1]
        let rhsDescription = switch rhs {
        case let .symbol(.infix(opName), _)
          where takesPrecedence(name, over: opName):
          "(\(rhs))"
        default:
          "\(rhs)"
        }
        return "\(lhsDescription) \(symbol.name) \(rhsDescription)"
      case .variable:
        return symbol.name
      case .function:
        return "\(symbol.name)(\(arguments(args)))"
      case .method:
        let thisArg = args.first?.description ?? ""
        return "\(thisArg).\(symbol.name)(\(arguments(args.dropFirst())))"
      }
    case let .error(_, expression):
      return expression
    }
  }

  var symbols: Set<CalcExpression.Symbol> {
    switch self {
    case .literal, .error:
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
struct UnicodeScalarView {
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
  fileprivate var unicodeScalars: Substring.UnicodeScalarView {
    characters[startIndex..<endIndex]
  }
}

private typealias _UnicodeScalarView = UnicodeScalarView

extension String {
  fileprivate init(_ unicodeScalarView: _UnicodeScalarView) {
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
    while let tail = scanCharacters(isIdentifier) {
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
    if let identifier = try parseNumericLiteral() ?? parseIdentifier() ?? parseEscapedIdentifier() {
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

    func scanHex() -> String? {
      scanCharacters {
        switch $0 {
        case "0"..."9", "A"..."F", "a"..."f":
          true
        default:
          false
        }
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
        if integer == "0", scanCharacter("x") {
          return .integer("\(sign)0x\(scanHex() ?? "")")
        }
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
        throw CalcExpression.Error.message("Value \(value) can't be converted to Integer type.")
      }
      return .literal(intValue)
    case let .number(value):
      guard let doubleValue = Double(value) else {
        throw CalcExpression.Error.unexpectedToken(value)
      }
      return .literal(doubleValue)
    }
  }

  private mutating func parseOperator() -> Subexpression? {
    if let op = scanCharacters(isOperator)
      ?? scanCharacter({ "(.".unicodeScalars.contains($0) }) {
      return .symbol(.infix(op), [])
    }
    return nil
  }

  private mutating func parseIdentifier() -> Subexpression? {
    var identifier = ""
    if let head = scanCharacter(isIdentifierHead) {
      identifier = head
    } else {
      return nil
    }
    while let tail = scanCharacters(isIdentifierWithDot) {
      identifier += tail
    }
    return makeVariable(identifier)
  }

  private mutating func parseEscapedIdentifier() -> Subexpression? {
    guard let delimiter = first,
          var string = scanCharacter({ "`'\"".unicodeScalars.contains($0) })
    else {
      return nil
    }
    var part: String?
    repeat {
      part = scanCharacters { $0 != delimiter && $0 != "\\" }
      string += part ?? ""
      if scanCharacter("\\"), let c = popFirst() {
        switch c {
        case "0":
          string += "\0"
        case "t":
          string += "\t"
        case "n":
          string += "\n"
        case "r":
          string += "\r"
        case "u" where scanCharacter("{"):
          let hex = scanCharacters {
            switch $0 {
            case "0"..."9", "A"..."F", "a"..."f":
              true
            default:
              false
            }
          } ?? ""
          guard scanCharacter("}") else {
            guard let junk = scanToEndOfToken() else {
              return .error(.missingDelimiter("}"), string)
            }
            return .error(.unexpectedToken(junk), string)
          }
          guard !hex.isEmpty else {
            return .error(.unexpectedToken("}"), string)
          }
          guard let codepoint = Int(hex, safeRadix: .hex),
                let c = UnicodeScalar(codepoint)
          else {
            // TODO: better error for invalid codepoint?
            return .error(.unexpectedToken(hex), string)
          }
          string.append(Character(c))
        case "'", "\\":
          string.append(Character(c))
        case "@" where scanCharacter("{"):
          string += "@{"
        default:
          return .error(.message("Incorrect string escape"), string)
        }
        part = ""
      }
    } while part != nil
    guard scanCharacter(delimiter) else {
      return .error(
        string == String(delimiter) ?
          .unexpectedToken(string) : .missingDelimiter(String(delimiter)),
        string
      )
    }
    string.append(Character(delimiter))
    return makeVariable(string)
  }

  fileprivate mutating func parseSubexpression(
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
          guard case let .symbol(.postfix(op), args) = lhs else {
            // Cannot follow an operand
            throw CalcExpression.Error.unexpectedToken("\(rhs)")
          }
          // Assume postfix operator was actually an infix operator
          stack[i] = args[0]
          stack.insert(.symbol(.infix(op), []), at: i + 1)
          try collapseStack(from: i)
        } else if case let .symbol(symbol, _) = rhs {
          switch symbol {
          case _ where stack.count <= i + 2, .postfix:
            stack[i...i + 1] = [.symbol(.postfix(symbol.name), [lhs])]
            try collapseStack(from: 0)
          default:
            let rhs = stack[i + 2]
            if rhs.isOperand {
              if stack.count > i + 3 {
                switch stack[i + 3] {
                case let .symbol(.infix(op2), _),
                     let .symbol(.prefix(op2), _),
                     let .symbol(.postfix(op2), _):
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
              if symbol.name == ":", // ternary
                 case let .symbol(.infix(_), args) = lhs {
                stack[i...i + 2] = [.symbol(.infix("?:"), [args[0], args[1], rhs])]
              } else {
                stack[i...i + 2] = [.symbol(.infix(symbol.name), [lhs, rhs])]
              }
              let from = symbol.name == "?" ? i : 0
              try collapseStack(from: from)
            } else if case let .symbol(symbol2, _) = rhs {
              if case .prefix = symbol2 {
                try collapseStack(from: i + 2)
              } else if ["+", "-", "/", "*"].contains(symbol.name) { // Assume infix
                stack[i + 2] = .symbol(.prefix(symbol2.name), [])
                try collapseStack(from: i + 2)
              } else { // Assume postfix
                stack[i + 1] = .symbol(.postfix(symbol.name), [])
                try collapseStack(from: i)
              }
            } else if case let .error(error, _) = rhs {
              throw error
            }
          }
        } else if case let .error(error, _) = rhs {
          throw error
        }
      } else if case let .symbol(symbol, _) = lhs {
        // Treat as prefix operator
        if rhs.isOperand {
          stack[i...i + 1] = [.symbol(.prefix(symbol.name), [rhs])]
          try collapseStack(from: 0)
        } else if case .symbol = rhs {
          // Nested prefix operator?
          try collapseStack(from: i + 1)
        } else if case let .error(error, _) = rhs {
          throw error
        }
      } else if case let .error(error, _) = lhs {
        throw error
      }
    }

    func scanArguments() throws -> [Subexpression] {
      let delimiter: Unicode.Scalar = ")"
      var args = [Subexpression]()
      if first != delimiter {
        let delimiters = [",", String(delimiter)]
        repeat {
          do {
            try args.append(parseSubexpression(upTo: delimiters))
          } catch CalcExpression.Error.unexpectedToken("") {
            if let token = scanCharacter() {
              throw CalcExpression.Error.unexpectedToken(token)
            }
          }
        } while scanCharacter(",")
      }
      guard scanCharacter(delimiter) else {
        throw CalcExpression.Error.missingDelimiter(String(delimiter))
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
        case ".":
          guard let lastSymbol = stack.last else {
            throw CalcExpression.Error.unexpectedToken(".")
          }
          guard let methodName = scanMethodName(), scanCharacter("(") else {
            throw CalcExpression.Error.message("Method expected after .")
          }
          var args = try scanArguments()
          args.insert(lastSymbol, at: 0)
          stack[stack.count - 1] = makeMethod(methodName, args)
        case "(":
          switch stack.last {
          case let .symbol(.variable(name), _)?:
            var args = try scanArguments()
            let parts = name.split(separator: ".")
            if parts.count == 1 {
              // function()
              stack[stack.count - 1] = makeFunction(name, args)
            } else {
              // variable.function()
              let variableName = parts.dropLast().joined(separator: ".")
              args.insert(makeVariable(variableName), at: 0)
              stack[stack.count - 1] = makeMethod(String(parts.last!), args)
            }
          default:
            let subexpression = try parseSubexpression(upTo: [")"])
            stack.append(subexpression)
            guard scanCharacter(")") else {
              throw CalcExpression.Error.missingDelimiter(")")
            }
          }
          operandPosition = false
          followedByWhitespace = skipWhitespace()
        default:
          switch (precededByWhitespace, followedByWhitespace) {
          case (true, true), (false, false):
            stack.append(expression)
          case (true, false):
            stack.append(.symbol(.prefix(name), []))
          case (false, true):
            stack.append(.symbol(.postfix(name), []))
          }
          operandPosition = true
        }
      case let .symbol(.variable(name), _) where !operandPosition:
        operandPosition = true
        stack.append(.symbol(.infix(name), []))
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
      throw CalcExpression.Error.unexpectedToken(junk)
    }
    try collapseStack(from: 0)
    switch stack.first {
    case let .error(error, _)?:
      throw error
    case let result?:
      if result.isOperand {
        return result
      }
      throw CalcExpression.Error.unexpectedToken(result.description)
    case nil:
      throw CalcExpression.Error.message("Empty expression")
    }
  }
}

private protocol _Numeric {
  init(truncating: NSNumber)
}

extension Int: _Numeric {}
extension Double: _Numeric {}

private protocol _Optional {
  static var wrappedType: Any.Type { get }
}

extension Optional: _Optional {
  fileprivate static var wrappedType: Any.Type { Wrapped.self }
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

private func isOperator(_ char: UnicodeScalar) -> Bool {
  // Strangely, this is faster than switching on value
  "/=­+-!*%<>&|^~?:".unicodeScalars.contains(char)
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

private func isIdentifierWithDot(_ c: UnicodeScalar) -> Bool {
  switch c.value {
  case 0x2E: // .
    true
  default:
    isIdentifier(c)
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
