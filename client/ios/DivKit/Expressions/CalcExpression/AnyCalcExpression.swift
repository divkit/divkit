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

  /// Evaluator for individual symbols
  typealias SymbolEvaluator = (_ args: [Any]) throws -> Any

  private typealias Symbol = CalcExpression.Symbol
  private typealias Error = CalcExpression.Error

  init(
    _ expression: ParsedCalcExpression,
    evaluators: @escaping (CalcExpression.Symbol) -> SymbolEvaluator?
  ) throws {
    let box = NanBox()

    let expression = CalcExpression(
      expression,
      evaluators: { symbol in
        if let evaluator = evaluators(symbol) {
          return { try box.store(evaluator($0.map(box.load))) }
        }
        switch symbol {
        case let .variable(name):
          // it is either a string literal or an undefined variable
          if name.count >= 2, "'\"".contains(name.first!) {
            let value = String(name.dropFirst().dropLast())
            return { _ in .string(value) }
          }
          return { _ in throw Error.message("Variable '\(name)' is missing.") }
        case .infix("!:"):
          return { args in
            switch args[0] {
            case .error:
              args[1]
            default:
              args[0]
            }
          }
        case let .function(name, actualArity):
          for i in 0...5 {
            let symbol = Symbol.function(name, arity: .exactly(i))
            if evaluators(symbol) != nil {
              if actualArity == .exactly(0) {
                return { _ in
                  throw Error.shortMessage("Non empty argument list is required for function '\(name)'.")
                }
              }
              return { _ in throw Error.arityMismatch(symbol) }
            }
          }
        default:
          break
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
      let value = expression.evaluate()
      return try box.load(value)
    }
    self.expression = expression
  }

  @inlinable
  func evaluate<T>() throws -> T {
    let value = try evaluator()
    if let castedValue: T = AnyCalcExpression.cast(value) {
      return castedValue
    }

    switch T.self {
    case is _String.Type:
      return (AnyCalcExpression.cast(AnyCalcExpression.stringify(value)) as T?)!
    default:
      break
    }

    throw Error.message("Result type \(Swift.type(of: value)) is not compatible with expected type \(T.self)")
  }
}

extension AnyCalcExpression {
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
      if anyValue is Bool {
        return nil
      }
      return (anyValue as? NSNumber).map { numericType.init(truncating: $0) as! T }
    case is String.Type:
      return (anyValue as? _String).map { String($0.substring) as! T }
    case is Substring.Type:
      return (anyValue as? _String)?.substring as! T?
    default:
      return nil
    }
  }

  @usableFromInline
  static func stringify(_ value: Any) -> String {
    switch value {
    case let number as NSNumber:
      // https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html
      switch UnicodeScalar(UInt8(number.objCType.pointee)) {
      case "c", "B":
        return number == 0 ? "false" : "true"
      case "d":
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 1
        formatter.maximumFractionDigits = 15
        formatter.locale = Locale(identifier: "en")
        return formatter.string(from: number)!
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
    default:
      return "\(value)"
    }
  }
}

// MARK: Private API

extension AnyCalcExpression {
  // Value storage
  fileprivate final class NanBox {
    private static let mask = (-Double.nan).bitPattern
    private static let indexOffset = 4

    private static func bitPattern(for index: Int) -> UInt64 {
      assert(index > -indexOffset)
      return UInt64(index + indexOffset) | mask
    }

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
      guard var index = Int(exactly: arg.bitPattern ^ NanBox.mask) else {
        return nil
      }
      index -= NanBox.indexOffset
      return values.indices.contains(index) ? values[index] : nil
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
        }
        throw CalcExpression.Value.integerError(value)
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

private protocol _Optional {
  var value: Any? { get }
  static var wrappedType: Any.Type { get }
}

extension Optional: _Optional {
  fileprivate var value: Any? { self }
  fileprivate static var wrappedType: Any.Type { Wrapped.self }
}
