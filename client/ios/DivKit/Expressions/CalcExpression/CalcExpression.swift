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

/// Immutable wrapper for a parsed expression
/// Reusing the same CalcExpression instance for multiple evaluations is more efficient
/// than creating a new one each time you wish to evaluate an expression string
final class CalcExpression: CustomStringConvertible {
  private let root: Subexpression

  /// Evaluator for individual symbols
  typealias SymbolEvaluator = (_ args: [Value]) throws -> Value

  /// Type representing the arity (number of arguments) accepted by a function
  enum Arity: ExpressibleByIntegerLiteral, CustomStringConvertible, Hashable {
    /// An exact number of arguments
    case exactly(Int)

    /// A minimum number of arguments
    case atLeast(Int)

    /// Any number of arguments
    static let any = atLeast(0)

    /// ExpressibleByIntegerLiteral constructor
    init(integerLiteral value: Int) {
      self = .exactly(value)
    }

    /// The human-readable description of the arity
    var description: String {
      switch self {
      case let .exactly(value):
        return "\(value) argument\(value == 1 ? "" : "s")"
      case let .atLeast(value):
        return "at least \(value) argument\(value == 1 ? "" : "s")"
      }
    }

    /// No-op Hashable implementation
    /// Required to support custom Equatable implementation
    func hash(into _: inout Hasher) {}

    /// Equatable implementation
    /// Note: this works more like a contains() function if
    /// one side a range and the other is an exact value
    /// This allows foo(x) to match foo(...) in a symbols dictionary
    static func ==(lhs: Arity, rhs: Arity) -> Bool {
      lhs.matches(rhs)
    }

    func matches(_ arity: Arity) -> Bool {
      switch (self, arity) {
      case let (.exactly(lhs), .exactly(rhs)),
           let (.atLeast(lhs), .atLeast(rhs)):
        return lhs == rhs
      case let (.atLeast(min), .exactly(value)),
           let (.exactly(value), .atLeast(min)):
        return value >= min
      }
    }
  }

  /// Symbols that make up an expression
  enum Symbol: CustomStringConvertible, Hashable {
    /// A named variable
    case variable(String)

    /// An infix operator
    case infix(String)

    /// A prefix operator
    case prefix(String)

    /// A postfix operator
    case postfix(String)

    /// A function accepting a number of arguments specified by `arity`
    case function(String, arity: Arity)

    /// A array of values accessed by index
    case array(String)

    /// The symbol name
    var name: String {
      switch self {
      case let .variable(name),
           let .infix(name),
           let .prefix(name),
           let .postfix(name),
           let .function(name, _),
           let .array(name):
        return name
      }
    }

    /// Printable version of the symbol name
    var escapedName: String {
      UnicodeScalarView(name).escapedIdentifier()
    }

    /// The human-readable description of the symbol
    var description: String {
      switch self {
      case .variable:
        return "variable \(escapedName)"
      case .infix("?:"):
        return "ternary operator \(escapedName)"
      case .infix("[]"):
        return "subscript operator \(escapedName)"
      case .infix("()"):
        return "function call operator \(escapedName)"
      case .infix:
        return "infix operator \(escapedName)"
      case .prefix:
        return "prefix operator \(escapedName)"
      case .postfix:
        return "postfix operator \(escapedName)"
      case .function:
        return "function \(escapedName)()"
      case .array:
        return "array \(escapedName)[]"
      }
    }
  }

  /// Runtime error when parsing or evaluating an expression
  enum Error: Swift.Error, CustomStringConvertible, Equatable {
    /// An application-specific error
    case message(String)

    /// An application-specific error without information about failed expression
    case shortMessage(String)

    /// The parser encountered a sequence of characters it didn't recognize
    case unexpectedToken(String)

    /// The parser expected to find a delimiter (e.g. closing paren) but didn't
    case missingDelimiter(String)

    /// The specified constant, operator or function was not recognized
    case undefinedSymbol(Symbol)

    /// A function was called with the wrong number of arguments (arity)
    case arityMismatch(Symbol)

    /// An array was accessed with an index outside the valid range
    case arrayBounds(Symbol, Double)

    case escaping

    /// Empty expression
    static let emptyExpression = unexpectedToken("")

    /// The human-readable description of the error
    var description: String {
      switch self {
      case let .message(message),
           let .shortMessage(message):
        return message
      case .emptyExpression:
        return "Empty expression"
      case let .unexpectedToken(string):
        return "Error tokenizing '\(string)'."
      case let .missingDelimiter(string):
        return "Missing `\(string)`"
      case let .undefinedSymbol(symbol):
        return "Undefined \(symbol)"
      case let .arityMismatch(symbol):
        let arity: Arity
        switch symbol {
        case let .function(_, requiredArity):
          arity = requiredArity
        case .infix("()"):
          arity = .atLeast(1)
        case .array, .infix("[]"):
          arity = 1
        case .infix("?:"):
          arity = 3
        case .infix:
          arity = 2
        case .postfix, .prefix:
          arity = 1
        case .variable:
          arity = 0
        }
        let description = symbol.description
        return "\(description.prefix(1).uppercased())\(description.dropFirst()) expects \(arity)"
      case let .arrayBounds(symbol, index):
        return "Index \(CalcExpression.stringify(index)) out of bounds for \(symbol)"
      case .escaping:
        return "Incorrect string escape"
      }
    }

    func makeOutputMessage(for expression: String) -> String {
      switch self {
      case let .shortMessage(message):
        return "Failed to evaluate [\(expression)]. \(message)"
      default:
        return self.description
      }
    }
  }

  /// Options for configuring an expression
  struct Options: OptionSet {
    /// Disable optimizations such as constant substitution
    static let noOptimize = Options(rawValue: 1 << 1)

    /// Enable standard boolean operators and constants
    static let boolSymbols = Options(rawValue: 1 << 2)

    /// Assume all functions and operators in `symbols` are "pure", i.e.
    /// they have no side effects, and always produce the same output
    /// for a given set of arguments
    static let pureSymbols = Options(rawValue: 1 << 3)

    /// Packed bitfield of options
    let rawValue: Int

    /// Designated initializer
    init(rawValue: Int) {
      self.rawValue = rawValue
    }
  }

  /// Alternative constructor for advanced usage
  /// Allows for dynamic symbol lookup or generation without any performance overhead
  /// Note that both math and boolean symbols are enabled by default - to disable them
  /// return `{ _ in throw CalcExpression.Error.undefinedSymbol(symbol) }` from your lookup function
  init(
    _ expression: ParsedCalcExpression,
    impureSymbols: (Symbol) throws -> SymbolEvaluator?,
    pureSymbols: (Symbol) throws -> SymbolEvaluator? = { _ in nil }
  ) throws {
    root = try expression.root.optimized(
      withImpureSymbols: impureSymbols,
      pureSymbols: {
        if let fn = try pureSymbols($0)
          ?? CalcExpression.mathSymbols[$0]
          ?? CalcExpression.boolSymbols[$0] {
          return fn
        }
        if case let .function(name, _) = $0 {
          for i in 0...10 {
            let symbol = Symbol.function(name, arity: .exactly(i))
            if try (impureSymbols(symbol) ?? pureSymbols(symbol)) != nil {
              return { _ in throw Error.arityMismatch(symbol) }
            }
          }
        }
        return CalcExpression.errorEvaluator(for: $0)
      }
    )
  }

  /// Verify that the string is a valid identifier
  static func isValidIdentifier(_ string: String) -> Bool {
    var characters = UnicodeScalarView(string)
    switch characters.parseIdentifier() ?? characters.parseEscapedIdentifier() {
    case .symbol(.variable, _, _)?:
      return characters.isEmpty
    default:
      return false
    }
  }

  /// Verify that the string is a valid operator
  static func isValidOperator(_ string: String) -> Bool {
    var characters = UnicodeScalarView(string)
    guard case let .symbol(symbol, _, _)? = characters.parseOperator(),
          case let .infix(name) = symbol, name != "(", name != "["
    else {
      return false
    }
    return characters.isEmpty
  }

  /// Parse an expression.
  /// Returns an opaque struct that cannot be evaluated but can be queried
  /// for symbols or used to construct an executable CalcExpression instance
  static func parse(_ expression: String) -> ParsedCalcExpression {
    var characters = Substring.UnicodeScalarView(expression.unicodeScalars)
    return parse(&characters)
  }

  /// Parse an expression directly from the provided UnicodeScalarView,
  /// stopping when it reaches a token matching the `delimiter` string.
  /// This is convenient if you wish to parse expressions that are nested
  /// inside another string, e.g. for implementing string interpolation.
  /// If no delimiter string is specified, the method will throw an error
  /// if it encounters an unexpected token, but won't consume it
  static func parse(
    _ input: inout Substring.UnicodeScalarView,
    upTo delimiters: String...
  ) -> ParsedCalcExpression {
    var unicodeScalarView = UnicodeScalarView(input)
    let start = unicodeScalarView
    var subexpression: Subexpression
    do {
      subexpression = try unicodeScalarView.parseSubexpression(upTo: delimiters)
    } catch {
      let expression = String(start.prefix(upTo: unicodeScalarView.startIndex))
      subexpression = .error(error as! Error, expression)
    }
    input = Substring.UnicodeScalarView(unicodeScalarView)
    return ParsedCalcExpression(root: subexpression)
  }

  /// Returns the optmized, pretty-printed expression if it was valid
  /// Otherwise, returns the original (invalid) expression string
  var description: String { root.description }

  /// All symbols used in the expression
  var symbols: Set<Symbol> { root.symbols }

  /// Evaluate the expression
  func evaluate() throws -> Value {
    try root.evaluate()
  }

  /// Standard math symbols
  static let mathSymbols: [Symbol: SymbolEvaluator] = {
    var symbols: [Symbol: SymbolEvaluator] = [:]

    // constants
    symbols[.variable("pi")] = { _ in .number(.pi) }

    // infix operators
    symbols[.infix("+")] = { try $0[0] + $0[1] }
    symbols[.infix("-")] = { try $0[0] - $0[1] }
    symbols[.infix("*")] = { try $0[0] * $0[1] }
    symbols[.infix("/")] = { try $0[0] / $0[1] }
    symbols[.infix("%")] = { try $0[0] % $0[1] }

    // prefix operators
    symbols[.prefix("-")] = { try -$0[0] }
    symbols[.prefix("+")] = { try +$0[0] }

    return symbols
  }()

  /// Standard boolean symbols
  static let boolSymbols: [Symbol: SymbolEvaluator] = {
    var symbols: [Symbol: SymbolEvaluator] = [:]

    // boolean constants
    symbols[.variable("true")] = { _ in .number(1) }
    symbols[.variable("false")] = { _ in .number(0) }

    // boolean infix operators
    symbols[.infix("==")] = { (args: [Value]) -> Value in
      args[0].value.isApproximatelyEqualTo(args[1].value) ? .number(1) : .number(0)
    }
    symbols[.infix("!=")] = { (args: [Value]) -> Value in
      args[0].value.isApproximatelyNotEqualTo(args[1].value) ? .number(1) : .number(0)
    }
    symbols[.infix(">")] = { (args: [Value]) -> Value in
      args[0].value > args[1].value ? .number(1) : .number(0)
    }
    symbols[.infix(">=")] = { (args: [Value]) -> Value in
      args[0].value >= args[1].value ? .number(1) : .number(0)
    }
    symbols[.infix("<")] = { (args: [Value]) -> Value in
      args[0].value < args[1].value ? .number(1) : .number(0)
    }
    symbols[.infix("<=")] = { (args: [Value]) -> Value in
      args[0].value <= args[1].value ? .number(1) : .number(0)
    }
    symbols[.infix("&&")] = { (args: [Value]) -> Value in
      args[0].value != 0 && args[1].value != 0 ? .number(1) : .number(0)
    }
    symbols[.infix("||")] = { (args: [Value]) -> Value in
      args[0].value != 0 || args[1].value != 0 ? .number(1) : .number(0)
    }

    // boolean prefix operators
    symbols[.prefix("!")] = { (args: [Value]) -> Value in
      args[0].value == 0 ? .number(1) : .number(0)
    }

    // ternary operator
    symbols[.infix("?:")] = { (args: [Value]) -> Value in
      if args.count == 3 {
        return args[0].value != 0 ? args[1] : args[2]
      }
      return args[0].value != 0 ? args[0] : args[1]
    }

    return symbols
  }()
}

// MARK: Internal API

extension CalcExpression {
  // Fallback evaluator for when symbol is not found
  static func errorEvaluator(for symbol: Symbol) -> SymbolEvaluator {
    switch symbol {
    case .infix(","), .infix("[]"), .function("[]", _), .infix("()"):
      return { _ in throw Error.unexpectedToken(String(symbol.name.prefix(1))) }
    case let .function(called, arity):
      let keys = Set(mathSymbols.keys).union(boolSymbols.keys)
      for case let .function(name, expected) in keys
        where name == called && arity != expected {
        return { _ in throw Error.arityMismatch(.function(called, arity: expected)) }
      }
      fallthrough
    default:
      return { _ in throw Error.undefinedSymbol(symbol) }
    }
  }
}

// MARK: Private API

extension CalcExpression {
  fileprivate static func stringify(_ number: Double) -> String {
    if let int = Int64(exactly: number) {
      return "\(int)"
    }
    return "\(number)"
  }

  // https://github.com/apple/swift-evolution/blob/master/proposals/0077-operator-precedence.md
  fileprivate static let operatorPrecedence: [String: (
    precedence: Int,
    isRightAssociative: Bool
  )] = {
    var precedences = [
      "[]": 100,
      "<<": 2, ">>": 2, ">>>": 2, // bitshift
      "*": 1, "/": 1, "%": 1, "&": 1, // multiplication
      // +, -, |, ^, etc: 0 (also the default)
      "..": -1, "...": -1, "..<": -1, // range formation
      "is": -2, "as": -2, "isa": -2, // casting
      "??": -3, "?:": -3, // null-coalescing
      // comparison: -4
      "&&": -5, "and": -5, // and
      "||": -6, "or": -6, // or
      ":": -8, // ternary
      // assignment: -9
      ",": -100,
    ].mapValues { ($0, false) }
    precedences["?"] = (-7, true) // ternary
    let comparisonOperators = [
      "<", "<=", ">=", ">",
      "==", "!=", "===", "!==",
      "lt", "le", "lte", "gt", "ge", "gte", "eq", "ne",
    ]
    for op in comparisonOperators {
      precedences[op] = (-4, true)
    }
    let assignmentOperators = [
      "=", "*=", "/=", "%=", "+=", "-=",
      "<<=", ">>=", "&=", "^=", "|=", ":=",
    ]
    for op in assignmentOperators {
      precedences[op] = (-9, true)
    }
    return precedences
  }()

  fileprivate static func `operator`(_ lhs: String, takesPrecedenceOver rhs: String) -> Bool {
    let (p1, rightAssociative) = operatorPrecedence[lhs] ?? (0, false)
    let (p2, _) = operatorPrecedence[rhs] ?? (0, false)
    if p1 == p2 {
      return !rightAssociative
    }
    return p1 > p2
  }

  fileprivate static func isOperator(_ char: UnicodeScalar) -> Bool {
    // Strangely, this is faster than switching on value
    if "/=­+!*%<>&|^~?:".unicodeScalars.contains(char) {
      return true
    }
    switch char.value {
    case 0x00_A1...0x00_A7,
         0x00_A9, 0x00_AB, 0x00_AC, 0x00_AE,
         0x00_B0...0x00_B1,
         0x00_B6, 0x00_BB, 0x00_BF, 0x00_D7, 0x00_F7,
         0x20_16...0x20_17,
         0x20_20...0x20_27,
         0x20_30...0x20_3E,
         0x20_41...0x20_53,
         0x20_55...0x20_5E,
         0x21_90...0x23_FF,
         0x25_00...0x27_75,
         0x27_94...0x2B_FF,
         0x2E_00...0x2E_7F,
         0x30_01...0x30_03,
         0x30_08...0x30_30:
      return true
    default:
      return false
    }
  }

  fileprivate static func isIdentifierHead(_ c: UnicodeScalar) -> Bool {
    switch c.value {
    case 0x5F, 0x23, 0x24, 0x40, // _ # $ @
         0x41...0x5A, // A-Z
         0x61...0x7A, // a-z
         0x00_A8, 0x00_AA, 0x00_AD, 0x00_AF,
         0x00_B2...0x00_B5,
         0x00_B7...0x00_BA,
         0x00_BC...0x00_BE,
         0x00_C0...0x00_D6,
         0x00_D8...0x00_F6,
         0x00_F8...0x00_FF,
         0x01_00...0x02_FF,
         0x03_70...0x16_7F,
         0x16_81...0x18_0D,
         0x18_0F...0x1D_BF,
         0x1E_00...0x1F_FF,
         0x20_0B...0x20_0D,
         0x20_2A...0x20_2E,
         0x20_3F...0x20_40,
         0x20_54,
         0x20_60...0x20_6F,
         0x20_70...0x20_CF,
         0x21_00...0x21_8F,
         0x24_60...0x24_FF,
         0x27_76...0x27_93,
         0x2C_00...0x2D_FF,
         0x2E_80...0x2F_FF,
         0x30_04...0x30_07,
         0x30_21...0x30_2F,
         0x30_31...0x30_3F,
         0x30_40...0xD7_FF,
         0xF9_00...0xFD_3D,
         0xFD_40...0xFD_CF,
         0xFD_F0...0xFE_1F,
         0xFE_30...0xFE_44,
         0xFE_47...0xFF_FD,
         0x1_00_00...0x1_FF_FD,
         0x2_00_00...0x2_FF_FD,
         0x3_00_00...0x3_FF_FD,
         0x4_00_00...0x4_FF_FD,
         0x5_00_00...0x5_FF_FD,
         0x6_00_00...0x6_FF_FD,
         0x7_00_00...0x7_FF_FD,
         0x8_00_00...0x8_FF_FD,
         0x9_00_00...0x9_FF_FD,
         0xA_00_00...0xA_FF_FD,
         0xB_00_00...0xB_FF_FD,
         0xC_00_00...0xC_FF_FD,
         0xD_00_00...0xD_FF_FD,
         0xE_00_00...0xE_FF_FD:
      return true
    default:
      return false
    }
  }

  fileprivate static func isIdentifier(_ c: UnicodeScalar) -> Bool {
    switch c.value {
    case 0x30...0x39, // 0-9
         0x03_00...0x03_6F,
         0x1D_C0...0x1D_FF,
         0x20_D0...0x20_FF,
         0xFE_20...0xFE_2F:
      return true
    default:
      return isIdentifierHead(c)
    }
  }
}

/// An opaque wrapper for a parsed expression
struct ParsedCalcExpression: CustomStringConvertible {
  fileprivate let root: Subexpression

  /// Returns the pretty-printed expression if it was valid
  /// Otherwise, returns the original (invalid) expression string
  var description: String { root.description }

  /// All symbols used in the expression
  var symbols: Set<CalcExpression.Symbol> { root.symbols }

  /// Any error detected during parsing
  var error: CalcExpression.Error? {
    if case let .error(error, _) = root {
      return error
    }
    return nil
  }

  var variablesNames: [String] {
    symbols
      .filter {
        guard case .variable = $0 else { return false }
        return true
      }
      .map { $0.name }
  }
}

// The internal expression implementation
private enum Subexpression: CustomStringConvertible {
  case literal(CalcExpression.Value)
  case symbol(CalcExpression.Symbol, [Subexpression], CalcExpression.SymbolEvaluator?)
  case error(CalcExpression.Error, String)

  var isOperand: Bool {
    switch self {
    case let .symbol(symbol, args, _) where args.isEmpty:
      switch symbol {
      case .infix, .prefix, .postfix:
        return false
      default:
        return true
      }
    case .symbol, .literal:
      return true
    case .error:
      return false
    }
  }

  func evaluate() throws -> CalcExpression.Value {
    switch self {
    case let .literal(literal):
      return literal
    case let .symbol(symbol, args, fn):
      guard let fn = fn else { throw CalcExpression.Error.undefinedSymbol(symbol) }
      return try fn(args.map { try $0.evaluate() })
    case let .error(error, _):
      throw error
    }
  }

  var description: String {
    func arguments(_ args: [Subexpression]) -> String {
      args.map {
        if case .symbol(.infix(","), _, _) = $0 {
          return "(\($0))"
        }
        return $0.description
      }.joined(separator: ", ")
    }
    switch self {
    case let .literal(literal):
      return CalcExpression.stringify(literal.value)
    case let .symbol(symbol, args, _):
      guard isOperand else {
        return symbol.escapedName
      }
      func needsSeparation(_ lhs: String, _ rhs: String) -> Bool {
        let lhs = lhs.unicodeScalars.last!, rhs = rhs.unicodeScalars.first!
        return lhs == "." || (CalcExpression.isOperator(lhs) || lhs == "-")
          == (CalcExpression.isOperator(rhs) || rhs == "-")
      }
      switch symbol {
      case let .prefix(name):
        let arg = args[0]
        let description = "\(arg)"
        switch arg {
        case .symbol(.infix, _, _), .symbol(.postfix, _, _), .error,
             .symbol where needsSeparation(name, description):
          return "\(symbol.escapedName)(\(description))" // Parens required
        case .symbol, .literal:
          return "\(symbol.escapedName)\(description)" // No parens needed
        }
      case let .postfix(name):
        let arg = args[0]
        let description = "\(arg)"
        switch arg {
        case .symbol(.infix, _, _), .symbol(.postfix, _, _), .error,
             .symbol where needsSeparation(description, name):
          return "(\(description))\(symbol.escapedName)" // Parens required
        case .symbol, .literal:
          return "\(description)\(symbol.escapedName)" // No parens needed
        }
      case .infix(","):
        return "\(args[0]), \(args[1])"
      case .infix("?:") where args.count == 3:
        return "\(args[0]) ? \(args[1]) : \(args[2])"
      case .infix("[]"):
        return "\(args[0])[\(args[1])]"
      case let .infix(name):
        let lhs = args[0]
        let lhsDescription: String
        switch lhs {
        case let .symbol(.infix(opName), _, _)
          where !CalcExpression.operator(opName, takesPrecedenceOver: name):
          lhsDescription = "(\(lhs))"
        default:
          lhsDescription = "\(lhs)"
        }
        let rhs = args[1]
        let rhsDescription: String
        switch rhs {
        case let .symbol(.infix(opName), _, _)
          where CalcExpression.operator(name, takesPrecedenceOver: opName):
          rhsDescription = "(\(rhs))"
        default:
          rhsDescription = "\(rhs)"
        }
        return "\(lhsDescription) \(symbol.escapedName) \(rhsDescription)"
      case .variable:
        return symbol.escapedName
      case .function("[]", _):
        return "[\(arguments(args))]"
      case .function:
        return "\(symbol.escapedName)(\(arguments(args)))"
      case .array:
        return "\(symbol.escapedName)[\(arguments(args))]"
      }
    case let .error(_, expression):
      return expression
    }
  }

  var symbols: Set<CalcExpression.Symbol> {
    switch self {
    case .literal, .error:
      return []
    case let .symbol(symbol, subexpressions, _):
      var symbols = Set([symbol])
      for subexpression in subexpressions {
        symbols.formUnion(subexpression.symbols)
      }
      return symbols
    }
  }

  func optimized(
    withImpureSymbols impureSymbols: (CalcExpression.Symbol) throws -> CalcExpression
      .SymbolEvaluator?,
    pureSymbols: (CalcExpression.Symbol) throws -> CalcExpression.SymbolEvaluator
  ) throws -> Subexpression {
    guard case .symbol(let symbol, var args, _) = self else {
      return self
    }
    args = try args.map {
      try $0.optimized(withImpureSymbols: impureSymbols, pureSymbols: pureSymbols)
    }
    if let fn = try impureSymbols(symbol) {
      return .symbol(symbol, args, fn)
    }
    let fn = try pureSymbols(symbol)
    var argValues = [CalcExpression.Value]()
    for arg in args {
      guard case let .literal(value) = arg else {
        return .symbol(symbol, args, fn)
      }
      argValues.append(value)
    }
    guard let result = try? fn(argValues) else {
      return .symbol(symbol, args, fn)
    }
    return .literal(result)
  }
}

// MARK: Expression parsing

// Workaround for horribly slow Substring.UnicodeScalarView perf
private struct UnicodeScalarView {
  typealias Index = String.UnicodeScalarView.Index

  private let characters: String.UnicodeScalarView
  private(set) var startIndex: Index
  private(set) var endIndex: Index

  init(_ unicodeScalars: String.UnicodeScalarView) {
    characters = unicodeScalars
    startIndex = characters.startIndex
    endIndex = characters.endIndex
  }

  init(_ unicodeScalars: Substring.UnicodeScalarView) {
    self.init(String.UnicodeScalarView(unicodeScalars))
  }

  init(_ string: String) {
    self.init(string.unicodeScalars)
  }

  var first: UnicodeScalar? {
    isEmpty ? nil : characters[startIndex]
  }

  var isEmpty: Bool {
    startIndex >= endIndex
  }

  subscript(_ index: Index) -> UnicodeScalar {
    characters[index]
  }

  func index(after index: Index) -> Index {
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

  mutating func popFirst() -> UnicodeScalar? {
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

extension Substring.UnicodeScalarView {
  fileprivate init(_ unicodeScalarView: _UnicodeScalarView) {
    self.init(unicodeScalarView.unicodeScalars)
  }
}

extension UnicodeScalarView {
  fileprivate enum Number {
    case number(String)
    case integer(String)

    var value: String {
      switch self {
      case let .number(value):
        return value
      case let .integer(value):
        return value
      }
    }
  }

  fileprivate mutating func scanCharacters(_ matching: (UnicodeScalar) -> Bool) -> String? {
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

  fileprivate mutating func scanCharacter(_ matching: (UnicodeScalar) -> Bool = { _ in
    true
  }) -> String? {
    if let c = first, matching(c) {
      self = suffix(from: index(after: startIndex))
      return String(c)
    }
    return nil
  }

  fileprivate mutating func scanCharacter(_ character: UnicodeScalar) -> Bool {
    scanCharacter { $0 == character } != nil
  }

  fileprivate mutating func scanToEndOfToken() -> String? {
    scanCharacters {
      switch $0 {
      case " ", "\t", "\n", "\r":
        return false
      default:
        return true
      }
    }
  }

  fileprivate mutating func skipWhitespace() -> Bool {
    if let _ = scanCharacters({
      switch $0 {
      case " ", "\t", "\n", "\r":
        return true
      default:
        return false
      }
    }) {
      return true
    }
    return false
  }

  fileprivate mutating func parseDelimiter(_ delimiters: [String]) -> Bool {
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

  fileprivate mutating func parseNumericLiteral() throws -> Subexpression? {
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
          return true
        default:
          return false
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

    func scanNumber() -> Number? {
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
            return .number(sign + integer)
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

    guard let number = scanNumber() else {
      return nil
    }
    switch number {
    case let .integer(value):
      guard let result = Int(value) else {
        return .error(CalcExpression.Value.integerError(value), value)
      }
      return .literal(.integer(result))
    case let .number(value):
      guard let result = Double(value) else {
        return .error(.unexpectedToken(value), value)
      }
      return .literal(.number(result))
    }
  }

  fileprivate mutating func parseOperator() -> Subexpression? {
    if var op = scanCharacters({ $0 == "." }) ?? scanCharacters({ $0 == "-" }) {
      if let tail = scanCharacters(CalcExpression.isOperator) {
        op += tail
      }
      return .symbol(.infix(op), [], nil)
    }
    if let op = scanCharacters(CalcExpression.isOperator) ??
      scanCharacter({ "([,".unicodeScalars.contains($0) }) {
      return .symbol(.infix(op), [], nil)
    }
    return nil
  }

  fileprivate mutating func parseIdentifier() -> Subexpression? {
    func scanIdentifier() -> String? {
      var start = self
      var identifier = ""
      if scanCharacter(".") {
        identifier = "."
      } else if let head = scanCharacter(CalcExpression.isIdentifierHead) {
        identifier = head
        start = self
        if scanCharacter(".") {
          identifier.append(".")
        }
      } else {
        return nil
      }
      while let tail = scanCharacters(CalcExpression.isIdentifier) {
        identifier += tail
        start = self
        if scanCharacter(".") {
          identifier.append(".")
        }
      }
      if identifier.hasSuffix(".") {
        self = start
        if identifier == "." {
          return nil
        }
        identifier = String(identifier.unicodeScalars.dropLast())
      } else if scanCharacter("'") {
        identifier.append("'")
      }
      return identifier
    }

    guard let identifier = scanIdentifier() else {
      return nil
    }
    return .symbol(.variable(identifier), [], nil)
  }

  // Note: this is not actually part of the parser, but is colocated
  // with `parseEscapedIdentifier()` because they should be updated together
  fileprivate func escapedIdentifier() -> String {
    guard let delimiter = first, "`'\"".unicodeScalars.contains(delimiter) else {
      return String(self)
    }
    var result = String(delimiter)
    var index = self.index(after: startIndex)
    while index != endIndex {
      let char = self[index]
      switch char.value {
      case 0:
        result += "\\0"
      case 9:
        result += "\\t"
      case 10:
        result += "\\n"
      case 13:
        result += "\\r"
      case 0x20..<0x7F,
           _ where CalcExpression.isOperator(char) || CalcExpression.isIdentifier(char):
        result.append(Character(char))
      default:
        result += "\\u{\(String(format: "%X", char.value))}"
      }
      index = self.index(after: index)
    }
    return result
  }

  fileprivate mutating func parseEscapedIdentifier() -> Subexpression? {
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
              return true
            default:
              return false
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
          return .error(.escaping, string)
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
    return .symbol(.variable(string), [], nil)
  }

  fileprivate mutating func parseSubexpression(upTo delimiters: [String]) throws
    -> Subexpression {
    var stack: [Subexpression] = []

    func collapseStack(from i: Int) throws {
      guard stack.count > i + 1 else {
        return
      }
      let lhs = stack[i]
      let rhs = stack[i + 1]
      if lhs.isOperand {
        if rhs.isOperand {
          guard case let .symbol(.postfix(op), args, _) = lhs else {
            // Cannot follow an operand
            throw CalcExpression.Error.unexpectedToken("\(rhs)")
          }
          // Assume postfix operator was actually an infix operator
          stack[i] = args[0]
          stack.insert(.symbol(.infix(op), [], nil), at: i + 1)
          try collapseStack(from: i)
        } else if case let .symbol(symbol, _, _) = rhs {
          switch symbol {
          case _ where stack.count <= i + 2, .postfix:
            stack[i...i + 1] = [.symbol(.postfix(symbol.name), [lhs], nil)]
            try collapseStack(from: 0)
          default:
            let rhs = stack[i + 2]
            if rhs.isOperand {
              if stack.count > i + 3 {
                switch stack[i + 3] {
                case let .symbol(.infix(op2), _, _),
                     let .symbol(.prefix(op2), _, _),
                     let .symbol(.postfix(op2), _, _):
                  guard stack.count > i + 4,
                        CalcExpression.operator(symbol.name, takesPrecedenceOver: op2)
                  else {
                    fallthrough
                  }

                default:
                  try collapseStack(from: i + 2)
                  return
                }
              }
              if symbol.name == ":",
                 case let .symbol(.infix("?"), args, _) = lhs { // ternary
                stack[i...i + 2] =
                  [.symbol(.infix("?:"), [args[0], args[1], rhs], nil)]
              } else {
                stack[i...i + 2] = [.symbol(.infix(symbol.name), [lhs, rhs], nil)]
              }
              let from = symbol.name == "?" ? i : 0
              try collapseStack(from: from)
            } else if case let .symbol(symbol2, _, _) = rhs {
              if case .prefix = symbol2 {
                try collapseStack(from: i + 2)
              } else if ["+", "/", "*"].contains(symbol.name) { // Assume infix
                stack[i + 2] = .symbol(.prefix(symbol2.name), [], nil)
                try collapseStack(from: i + 2)
              } else { // Assume postfix
                stack[i + 1] = .symbol(.postfix(symbol.name), [], nil)
                try collapseStack(from: i)
              }
            } else if case let .error(error, _) = rhs {
              throw error
            }
          }
        } else if case let .error(error, _) = rhs {
          throw error
        }
      } else if case let .symbol(symbol, _, _) = lhs {
        // Treat as prefix operator
        if rhs.isOperand {
          stack[i...i + 1] = [.symbol(.prefix(symbol.name), [rhs], nil)]
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

    func scanArguments(upTo delimiter: Unicode.Scalar) throws -> [Subexpression] {
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
    while !parseDelimiter(delimiters), let expression =
      try parseNumericLiteral() ??
      parseIdentifier() ??
      parseOperator() ??
      parseEscapedIdentifier() {
      // Prepare for next iteration
      var followedByWhitespace = skipWhitespace() || isEmpty

      func appendLiteralWithNegativeValue() {
        operandPosition = false
        if let last = stack.last, last.isOperand {
          stack.append(.symbol(.infix("+"), [], nil))
        }
        stack.append(expression)
      }

      switch expression {
      case let .symbol(.infix(name), _, _):
        switch name {
        case "(":
          switch stack.last {
          case let .symbol(.variable(name), _, _)?:
            let args = try scanArguments(upTo: ")")
            stack[stack.count - 1] =
              .symbol(.function(name, arity: .exactly(args.count)), args, nil)
          case let last? where last.isOperand:
            let args = try scanArguments(upTo: ")")
            stack[stack.count - 1] = .symbol(.infix("()"), [last] + args, nil)
          default:
            // TODO: if we make `,` a multifix operator, we can use `scanArguments()` here instead
            // Alternatively: add .function("()", arity: .any), as with []
            var subexpression = try parseSubexpression(upTo: [")"])
            switch subexpression {
            case let .literal(literal):
              switch literal {
              case let .integer(value):
                if CalcExpression.Value.minInteger <= value,
                   value <= CalcExpression.Value.maxInteger {
                  break
                } else {
                  subexpression = .error(
                    CalcExpression.Value.integerError(value),
                    expression.description
                  )
                }
              case .number:
                break
              case .string:
                break
              case .datetime:
                break
              }
            case .error, .symbol:
              break
            }
            stack.append(subexpression)
            guard scanCharacter(")") else {
              throw CalcExpression.Error.missingDelimiter(")")
            }
          }
          operandPosition = false
          followedByWhitespace = skipWhitespace()
        case ",":
          operandPosition = true
          if let last = stack.last, !last.isOperand,
             case let .symbol(.infix(op), _, _) = last {
            // If previous token was an infix operator, convert it to postfix
            stack[stack.count - 1] = .symbol(.postfix(op), [], nil)
          }
          stack.append(expression)
          operandPosition = true
          followedByWhitespace = skipWhitespace()
        case "[":
          let args = try scanArguments(upTo: "]")
          switch stack.last {
          case let .symbol(.variable(name), _, _)?:
            guard args.count == 1 else {
              throw CalcExpression.Error.arityMismatch(.array(name))
            }
            stack[stack.count - 1] = .symbol(.array(name), [args[0]], nil)
          case let last? where last.isOperand:
            guard args.count == 1 else {
              throw CalcExpression.Error.arityMismatch(.infix("[]"))
            }
            stack[stack.count - 1] = .symbol(.infix("[]"), [last, args[0]], nil)
          default:
            stack
              .append(.symbol(.function("[]", arity: .exactly(args.count)), args, nil))
          }
          operandPosition = false
          followedByWhitespace = skipWhitespace()
        default:
          switch (precededByWhitespace, followedByWhitespace) {
          case (true, true), (false, false):
            stack.append(expression)
          case (true, false):
            stack.append(.symbol(.prefix(name), [], nil))
          case (false, true):
            stack.append(.symbol(.postfix(name), [], nil))
          }
          operandPosition = true
        }
      case let .symbol(.variable(name), _, _) where !operandPosition:
        operandPosition = true
        stack.append(.symbol(.infix(name), [], nil))
      case let .literal(.integer(value)) where value < 0:
        appendLiteralWithNegativeValue()
      case let .literal(.number(value)) where value < 0:
        appendLiteralWithNegativeValue()
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
      throw CalcExpression.Error.emptyExpression
    }
  }
}
