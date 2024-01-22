//
//  AnyCalcExpression.swift
//  Expression
//
//  Version 0.13.5
//
//  Created by Nick Lockwood on 18/04/2017.
//  Copyright © 2017 Nick Lockwood. All rights reserved.
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

/// Wrapper for Expression that works with any type of value
struct AnyCalcExpression {
  private let expression: CalcExpression
  @usableFromInline
  let evaluator: () throws -> Any

  typealias ValueProvider = (_ name: String) -> Any?

  /// Evaluator for individual symbols
  typealias SymbolEvaluator = (_ args: [Any]) throws -> Any

  /// Symbols that make up an expression
  typealias Symbol = CalcExpression.Symbol

  /// Runtime error when parsing or evaluating an expression
  typealias Error = CalcExpression.Error

  init(
    _ expression: ParsedCalcExpression,
    variables: ValueProvider,
    functions: [Symbol: Function]
  ) throws {
    try self.init(
      expression,
      impureSymbols: { symbol in
        switch symbol {
        case .function, .infix, .prefix:
          return functions[symbol]?.symbolEvaluator
        default:
          return nil
        }
      },
      pureSymbols: { symbol in
        switch symbol {
        case let .variable(name):
          return variables(name).map { value in { _ in value } }
        case .function, .infix, .prefix:
          return functions[symbol]?.symbolEvaluator
        default:
          return nil
        }
      }
    )
  }

  /// Private initializer implementation
  /// Allows for dynamic symbol lookup or generation without any performance overhead
  /// Note that standard library symbols are all enabled by default - to disable them
  /// return `{ _ in throw AnyCalcExpression.Error.undefinedSymbol(symbol) }` from your lookup
  /// function
  private init(
    _ expression: ParsedCalcExpression,
    impureSymbols: (Symbol) -> SymbolEvaluator?,
    pureSymbols: (Symbol) -> SymbolEvaluator?
  ) throws {
    let box = NanBox()
    func unwrapString(_ name: String) -> String? {
      guard name.count >= 2, "'\"".contains(name.first!) else {
        return nil
      }
      return String(name.dropFirst().dropLast())
    }
    func funcEvaluator(for symbol: Symbol, _ value: Any) -> CalcExpression.SymbolEvaluator? {
      // TODO: should funcEvaluator call the `.infix("()")` implementation?
      switch value {
      case let fn as SymbolEvaluator:
        return { args in
          try box.store(fn(args.map(box.load)))
        }
      case let fn as CalcExpression.SymbolEvaluator:
        return { args in
          try fn(args)
        }
      default:
        return nil
      }
    }

    // Evaluators
    func defaultEvaluator(for symbol: Symbol) throws -> CalcExpression.SymbolEvaluator? {
      if let fn = AnyCalcExpression.standardSymbols[symbol] {
        return fn
      } else {
        switch symbol {
        case .function("[]", _):
          return { try box.store($0.map(box.load)) }
        case let .variable(name):
          guard let string = unwrapString(name) else {
            return { _ in throw Error.missingVariable(symbol) }
          }
          let stringRef = try box.store(string)
          return { _ in stringRef }
        default:
          return nil
        }
      }
    }

    // Build CalcExpression
    var _pureSymbols = [Symbol: CalcExpression.SymbolEvaluator]()
    let expression = try CalcExpression(
      expression,
      impureSymbols: { symbol in
        if let fn = impureSymbols(symbol) {
          return { try box.store(fn($0.map(box.load))) }
        } else if let fn = pureSymbols(symbol) {
          switch symbol {
          case .variable, .function(_, arity: 0):
            do {
              let value = try box.store(fn([]))
              _pureSymbols[symbol] = { _ in value }
            } catch {
              return { _ in throw error }
            }
          default:
            _pureSymbols[symbol] = { try box.store(fn($0.map(box.load))) }
          }
        } else if case .infix("()") = symbol {
          // TODO: check for pure `.infix("()")` implementation, and use as
          // fallback if the lhs isn't a SymbolEvaluator?
          return { args in
            switch try box.load(args[0]) {
            case let fn as SymbolEvaluator:
              return try box.store(fn(args.dropFirst().map(box.load)))
            case let fn as CalcExpression.SymbolEvaluator:
              return try fn(Array(args.dropFirst()))
            default:
              throw try Error.typeMismatch(symbol, args.map(box.load))
            }
          }
        } else if case let .function(name, _) = symbol {
          if let fn = try defaultEvaluator(for: symbol) {
            _pureSymbols[symbol] = fn
          } else if let fn = impureSymbols(.variable(name)) {
            return { args in
              let value = try fn([])
              if let fn = funcEvaluator(for: symbol, value) {
                return try fn(args)
              }
              throw try Error.typeMismatch(
                .infix("()"), [value] + [args.map(box.load)]
              )
            }
          } else if let fn = pureSymbols(.variable(name)) {
            do {
              if let fn = try funcEvaluator(for: symbol, fn([])) {
                return fn
              }
            } catch {
              return { _ in throw error }
            }
          }
        }
        return nil
      },
      pureSymbols: { symbol in
        guard let fn = try (_pureSymbols[symbol] ?? defaultEvaluator(for: symbol)) else {
          if case let .function(name, actualArity) = symbol {
            // TODO: check for pure `.infix("()")` implementation?
            for i in 0...10 {
              let symbol = Symbol.function(name, arity: .exactly(i))
              if impureSymbols(symbol) ?? pureSymbols(symbol) != nil {
                if actualArity == .exactly(0) {
                  return { _ in throw
                    Error
                    .shortMessage("Non empty argument list is required for function '\(name)'.")
                  }
                }
                return { _ in throw Error.arityMismatch(symbol) }
              }
            }
            if let fn = pureSymbols(.variable(name)) {
              return { args in
                let value = try fn([])
                throw try Error.typeMismatch(
                  .infix("()"),
                  [value] + [args.map(box.load)]
                )
              }
            }
          }
          return CalcExpression.errorEvaluator(for: symbol)
        }
        return fn
      }
    )

    // These are constant values that won't change between evaluations
    // and won't be re-stored, so must not be cleared
    let literals = box.values

    // Evaluation isn't thread-safe due to shared values
    // so we use NSLock to prevent re-entrance
    let lock = NSLock()
    evaluator = {
      lock.lock()
      defer {
        box.values = literals
        lock.unlock()
      }
      let value = try expression.evaluate()
      return try box.load(value)
    }
    self.expression = expression
  }

  /// Evaluate the expression
  @inlinable
  func evaluate<T>() throws -> T {
    let anyValue = try evaluator()
    guard let value: T = AnyCalcExpression.cast(anyValue) else {
      switch T.self {
      case _ where AnyCalcExpression.isNil(anyValue):
        break // Fall through
      case is _String.Type, is NSString?.Type, is String?.Type, is Substring?.Type:
        // TODO: should we stringify any type like this?
        return (AnyCalcExpression.cast(AnyCalcExpression.stringify(anyValue)!) as T?)!
      case is Bool.Type, is Bool?.Type:
        // TODO: should we boolify numeric types like this?
        if let value = AnyCalcExpression.cast(anyValue) as Double? {
          return (value != 0) as! T
        }
      default:
        // TODO: should we numberify Bool values like this?
        if let boolValue = anyValue as? Bool,
           let value: T = AnyCalcExpression.cast(boolValue ? 1 : 0) {
          return value
        }
      }
      throw Error.resultTypeMismatch(T.self, anyValue)
    }
    return value
  }
}

// MARK: Internal API

extension AnyCalcExpression.Error {
  /// Standard error message for mismatched argument types
  fileprivate static func typeMismatch(
    _ symbol: AnyCalcExpression.Symbol,
    _ args: [Any]
  ) -> AnyCalcExpression
    .Error {
    let types = args.map {
      AnyCalcExpression.stringifyOrNil(AnyCalcExpression.isNil($0) ? $0 : type(of: $0))
    }
    switch symbol {
    case .infix("[]") where types.count == 2:
      if AnyCalcExpression.isSubscriptable(args[0]) {
        return .message(
          "Attempted to subscript \(types[0]) with incompatible index type \(types[1])"
        )
      } else {
        return .message("Attempted to subscript \(types[0]) value")
      }
    case .array where types.count == 2:
      if AnyCalcExpression.isSubscriptable(args[0]) {
        fallthrough
      } else {
        return .message("Attempted to subscript \(types[0]) value \(symbol.escapedName)")
      }
    case .array where !types.isEmpty:
      return .message(
        "Attempted to subscript \(symbol.escapedName) with incompatible index type \(types.last!)"
      )
    case .infix("()") where !types.isEmpty:
      switch type(of: args[0]) {
      case is CalcExpression.SymbolEvaluator.Type, is AnyCalcExpression.SymbolEvaluator.Type:
        return .message(
          "Attempted to call function with incompatible arguments (\(types.dropFirst().joined(separator: ", ")))"
        )
      case _ where types[0].contains("->"):
        return .message("Attempted to call non SymbolEvaluator function type \(types[0])")
      default:
        return .message("Attempted to call non function type \(types[0])")
      }
    case .infix("==") where types.count == 2 && types[0] == types[1]:
      return .message("Arguments for \(symbol) must conform to the Hashable protocol")
    case _ where types.count == 1:
      return .message("Argument of type \(types[0]) is not compatible with \(symbol)")
    default:
      return .message(
        "Arguments of type (\(types.joined(separator: ", "))) are not compatible with \(symbol)"
      )
    }
  }

  /// Standard error message for subscripting outside of a string's bounds
  fileprivate static func stringBounds(_ string: String, _ index: Int) -> AnyCalcExpression.Error {
    let escapedString = CalcExpression.Symbol.variable("'\(string)'").escapedName
    return .message("Character index \(index) out of bounds for string \(escapedString)")
  }

  fileprivate static func stringBounds(
    _ string: Substring,
    _ index: String.Index
  ) -> AnyCalcExpression.Error {
    var _string = string
    while index > _string.endIndex {
      // Double the length until it fits
      // TODO: is there a better solution for this?
      _string += _string
    }
    let offset = _string.distance(from: _string.startIndex, to: index)
    return stringBounds(String(string), offset)
  }

  /// Standard error message for invalid range
  fileprivate static func invalidRange<T: Comparable>(_ lhs: T, _ rhs: T) -> AnyCalcExpression
    .Error {
    if lhs > rhs {
      return .message("Cannot form range with lower bound > upper bound")
    }
    return .message("Cannot form half-open range with lower bound == upper bound")
  }

  /// Standard error message for mismatched return type
  @usableFromInline
  static func resultTypeMismatch(_ type: Any.Type, _ value: Any) -> AnyCalcExpression
    .Error {
    let valueType = AnyCalcExpression
      .stringifyOrNil(AnyCalcExpression.unwrap(value).map { Swift.type(of: $0) } as Any)
    return .message(
      "Result type \(valueType) is not compatible with expected type \(AnyCalcExpression.stringifyOrNil(type))"
    )
  }
}

extension AnyCalcExpression {
  // Cast a value to the specified type
  @usableFromInline
  static func cast<T>(_ anyValue: Any) -> T? {
    if let value = anyValue as? T {
      return value
    }
    var type: Any.Type = T.self
    if let optionalType = type as? _Optional.Type {
      type = optionalType.wrappedType
    }
    switch type {
    case let numericType as _Numeric.Type:
      if anyValue is Bool { return nil }
      return (anyValue as? NSNumber).map { numericType.init(truncating: $0) as! T }
    case is String.Type:
      return (anyValue as? _String).map { String($0.substring) as! T }
    case is Substring.Type:
      return (anyValue as? _String)?.substring as! T?
    default:
      return nil
    }
  }

  fileprivate static func stringifyOrNil(_ value: Any) -> String {
    stringify(value) ?? "nil"
  }

  // Convert any value to a printable string
  @usableFromInline
  static func stringify(_ value: Any) -> String? {
    switch value {
    case let number as NSNumber:
      // https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html
      switch UnicodeScalar(UInt8(number.objCType.pointee)) {
      case "c", "B":
        return number == 0 ? "false" : "true"
      case "d":
        let nf = NumberFormatter()
        nf.minimumFractionDigits = 1
        nf.maximumFractionDigits = 15
        nf.locale = Locale(identifier: "en")
        return nf.string(from: number)
      default:
        break
      }
      if let int = Int64(exactly: number) {
        return "\(int)"
      }
      if let uint = UInt64(exactly: number) {
        return "\(uint)"
      }
      return "\(number)"
    case let color as RGBAColor:
      return color.argbString
    case let date as Date:
      let dateFormatter = DateFormatter()
      dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
      dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
      return dateFormatter.string(from: date)
    case is Any.Type:
      return "\(value)"
    case let value:
      return unwrap(value).map { "\($0)" }
    }
  }

  // Unwraps a potentially optional value
  fileprivate static func unwrap(_ value: Any) -> Any? {
    switch value {
    case let optional as _Optional:
      guard let value = optional.value else {
        fallthrough
      }
      return unwrap(value)
    case is NSNull:
      return nil
    default:
      return value
    }
  }

  // Test if a value is nil
  @usableFromInline
  static func isNil(_ value: Any) -> Bool {
    if let optional = value as? _Optional {
      guard let value = optional.value else {
        return true
      }
      return isNil(value)
    }
    return value is NSNull
  }

  // Test if a value supports subscripting
  fileprivate static func isSubscriptable(_ value: Any) -> Bool {
    value is _String
  }
}

// MARK: Private API

extension AnyCalcExpression {
  // Value storage
  fileprivate final class NanBox {
    private static let mask = (-Double.nan).bitPattern
    private static let indexOffset = 4
    private static let nilBits = bitPattern(for: -1)

    private static func bitPattern(for index: Int) -> UInt64 {
      assert(index > -indexOffset)
      return UInt64(index + indexOffset) | mask
    }

    // Literal values
    static let nilValue = Double(bitPattern: nilBits)

    // The values stored in the box
    var values = [Any]()

    // Store a value in the box
    func store(_ value: Any) throws -> CalcExpression.Value {
      switch value {
      case let doubleValue as Double:
        return .number(doubleValue)
      case let boolValue as Bool:
        return .boolean(boolValue)
      case let floatValue as Float:
        return .number(Double(floatValue))
      case is Int, is UInt, is Int32, is UInt32, is Int64, is UInt64:
        if let intValue = value as? Int {
          return .integer(intValue)
        } else {
          throw CalcExpression.Value.integerOverflow()
        }
      case let numberValue as NSNumber:
        // Hack to avoid losing type info for UIFont.Weight, etc
        if "\(value)".contains("rawValue") {
          break
        }
        return .number(Double(truncating: numberValue))
      case let datetimeValue as Date:
        return .datetime(datetimeValue)
      case let stringValue as String:
        return .string(stringValue)
      case _ where AnyCalcExpression.isNil(value):
        return .number(NanBox.nilValue)
      case let error as CalcExpression.Error:
        return .error(error)
      default:
        break
      }
      values.append(value)
      return .number(Double(bitPattern: NanBox.bitPattern(for: values.count - 1)))
    }

    // Retrieve a value from the box, if it exists
    fileprivate func loadIfStored(_ arg: Double) -> Any? {
      switch arg.bitPattern {
      case NanBox.nilBits:
        return nil as Any? as Any
      case let bits:
        guard var index = Int(exactly: bits ^ NanBox.mask) else {
          return nil
        }
        index -= NanBox.indexOffset
        return values.indices.contains(index) ? values[index] : nil
      }
    }

    // Retrieve a value if it exists, else return the argument
    func load(_ arg: CalcExpression.Value) throws -> Any {
      switch arg {
      case let .string(value):
        return value
      case let .number(value):
        return loadIfStored(value) ?? value
      case let .integer(value):
        if CalcExpression.Value.minInteger <= value, value <= CalcExpression.Value.maxInteger {
          return value
        } else {
          throw CalcExpression.Value.integerError(value)
        }
      case let .datetime(value):
        return value
      case let .boolean(value):
        return value
      case let .error(error):
        throw error
      }
    }
  }

  // Standard symbols
  fileprivate static let standardSymbols: [Symbol: CalcExpression.SymbolEvaluator] = [
    // Boolean symbols
    .variable("true"): { _ in .boolean(true) },
    .variable("false"): { _ in .boolean(false) },
    .infix("!:"): { args in
      switch args[0] {
      case .error:
        return args[1]
      default:
        return args[0]
      }
    },
  ]
}

// Used for casting numeric values
private protocol _Numeric {
  init(truncating: NSNumber)
}

extension Int: _Numeric {}
extension Int8: _Numeric {}
extension Int16: _Numeric {}
extension Int32: _Numeric {}
extension Int64: _Numeric {}

extension UInt: _Numeric {}
extension UInt8: _Numeric {}
extension UInt16: _Numeric {}
extension UInt32: _Numeric {}
extension UInt64: _Numeric {}

extension Double: _Numeric {}
extension Float: _Numeric {}

// Used for string values
@usableFromInline
protocol _String {
  var substring: Substring { get }
}

extension String: _String {
  @usableFromInline
  var substring: Substring {
    Substring(self)
  }
}

extension Substring: _String {
  @usableFromInline
  var substring: Substring {
    self
  }
}

extension NSString: _String {
  @usableFromInline
  var substring: Substring {
    Substring("\(self)")
  }
}

// Used to test if a value is Optional
private protocol _Optional {
  var value: Any? { get }
  static var wrappedType: Any.Type { get }
}

extension Optional: _Optional {
  fileprivate var value: Any? { self }
  fileprivate static var wrappedType: Any.Type { Wrapped.self }
}
