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
      symbols: { symbol in
        switch symbol {
        case .variable("true"):
          { _ in true }
        case .variable("false"):
          { _ in false }
        case let .variable(name):
          variables(name).map { value in { _ in value } }
        case .function, .infix, .prefix:
          functions[symbol]?.symbolEvaluator
        default:
          nil
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
    symbols: (Symbol) -> SymbolEvaluator?
  ) throws {
    let box = NanBox()
    func unwrapString(_ name: String) -> String? {
      guard name.count >= 2, "'\"".contains(name.first!) else {
        return nil
      }
      return String(name.dropFirst().dropLast())
    }

    func defaultEvaluator(_ symbol: Symbol) throws -> CalcExpression.SymbolEvaluator? {
      switch symbol {
      case let .variable(name):
        // actually it is not a variable, it is a string literal
        guard let string = unwrapString(name) else {
          return { _ in throw Error.missingVariable(symbol) }
        }
        let stringRef = try box.store(string)
        return { _ in stringRef }
      case .infix("!:"):
        return { args in
          switch args[0] {
          case .error:
            args[1]
          default:
            args[0]
          }
        }
      default:
        return nil
      }
    }

    let expression = try CalcExpression(
      expression,
      symbols: { symbol in
        if let evaluator = symbols(symbol) {
          return { try box.store(evaluator($0.map(box.load))) }
        }
        if let evaluator = try defaultEvaluator(symbol) {
          return evaluator
        }
        if case let .function(name, actualArity) = symbol {
          for i in 0...10 {
            let symbol = Symbol.function(name, arity: .exactly(i))
            if symbols(symbol) != nil {
              if actualArity == .exactly(0) {
                return { _ in
                  throw Error.shortMessage("Non empty argument list is required for function '\(name)'.")
                }
              }
              return { _ in throw Error.arityMismatch(symbol) }
            }
          }
        }
        return CalcExpression.errorEvaluator(for: symbol)
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
      default:
        break
      }
      throw Error.resultTypeMismatch(T.self, anyValue)
    }
    return value
  }
}

// MARK: Internal API

extension AnyCalcExpression.Error {
  @usableFromInline
  static func resultTypeMismatch(_ type: Any.Type, _ value: Any) -> AnyCalcExpression.Error {
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
