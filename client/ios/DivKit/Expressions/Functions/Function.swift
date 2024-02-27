import Foundation

import BasePublic

protocol Function {
  var arity: CalcExpression.Arity { get }
  func invoke(args: [Any]) throws -> Any
  func symbolEvaluator(_ args: [Any]) throws -> Any
  func verify(signature: FunctionSignature) throws
}

protocol SimpleFunction: Function {
  var signature: FunctionSignature { get throws }
}

extension SimpleFunction {
  var arity: CalcExpression.Arity {
    (try? signature.arity) ?? 0
  }

  func symbolEvaluator(_ args: [Any]) throws -> Any {
    try signature.checkArguments(args: args)
    return try invoke(args: args)
  }

  func verify(signature: FunctionSignature) throws {
    try signature.verify(self.signature)
  }
}

struct FunctionNullary<R>: SimpleFunction {
  private let impl: () throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping () throws -> R) {
    self.impl = impl
  }

  func invoke(args _: [Any]) throws -> Any {
    try impl()
  }
}

struct FunctionUnary<T1, R>: SimpleFunction {
  private let impl: (T1) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self)),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping (T1) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args[0] as? T1 else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    return try impl(v1)
  }
}

struct FunctionBinary<T1, T2, R>: SimpleFunction {
  private let impl: (T1, T2) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self)),
          .init(type: .from(type: T2.self)),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args[0] as? T1, let v2 = args[1] as? T2 else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    return try impl(v1, v2)
  }
}

struct FunctionTernary<T1, T2, T3, R>: SimpleFunction {
  private let impl: (T1, T2, T3) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self)),
          .init(type: .from(type: T2.self)),
          .init(type: .from(type: T3.self)),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2, T3) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args[0] as? T1, let v2 = args[1] as? T2, let v3 = args[2] as? T3 else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    return try impl(v1, v2, v3)
  }
}

struct FunctionQuaternary<T1, T2, T3, T4, R>: SimpleFunction {
  private let impl: (T1, T2, T3, T4) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self)),
          .init(type: .from(type: T2.self)),
          .init(type: .from(type: T3.self)),
          .init(type: .from(type: T4.self)),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2, T3, T4) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args[0] as? T1, let v2 = args[1] as? T2, let v3 = args[2] as? T3,
          let v4 = args[3] as? T4 else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    return try impl(v1, v2, v3, v4)
  }
}

struct FunctionVarUnary<T1, R>: SimpleFunction {
  private let impl: ([T1]) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self), vararg: true),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping ([T1]) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args as? [T1] else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    return try impl(v1)
  }
}

struct FunctionVarBinary<T1, T2, R>: SimpleFunction {
  private let impl: (T1, [T2]) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self)),
          .init(type: .from(type: T2.self), vararg: true),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping (T1, [T2]) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args[0] as? T1 else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    let v2 = args.dropFirst().map { $0 as! T2 }
    return try impl(v1, v2)
  }
}

struct FunctionVarTernary<T1, T2, T3, R>: SimpleFunction {
  private let impl: (T1, T2, [T3]) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(type: T1.self)),
          .init(type: .from(type: T2.self)),
          .init(type: .from(type: T3.self), vararg: true),
        ],
        resultType: .from(type: R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2, [T3]) throws -> R) {
    self.impl = impl
  }

  func invoke(args: [Any]) throws -> Any {
    guard let v1 = args[0] as? T1, let v2 = args[1] as? T2 else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    let v3 = args.dropFirst(2).map { $0 as! T3 }
    return try impl(v1, v2, v3)
  }
}

struct OverloadedFunction: Function {
  private let functions: [SimpleFunction]
  private let makeError: ([Argument]) -> Error

  var arity: CalcExpression.Arity {
    functions.first?.arity ?? 0
  }

  init(functions: [SimpleFunction], makeError: (([Argument]) -> Error)? = nil) {
    self.functions = functions
    self.makeError = makeError ?? { FunctionSignature.Error.typeMismatch(args: $0).message }
  }

  func verify(signature: FunctionSignature) throws {
    if !functions.contains(where: { (try? $0.verify(signature: signature)) != nil }) {
      throw FunctionSignature.Error.notFound.message
    }
  }

  func invoke(args: [Any]) throws -> Any {
    let arguments = try args.map {
      try FunctionSignature.ArgumentSignature(type: .from(type: type(of: $0)))
    }
    guard let function = try functions
      .first(where: { try
          zip($0.signature.allArguments(arguments.count), arguments)
          .allSatisfy { $0.type == $1.type || $0.type == .any }

      })
    else {
      throw makeError(zip(args, arguments).map { Argument(type: $1.type, value: $0) })
    }
    return try function.invoke(args: args)
  }

  func symbolEvaluator(_ args: [Any]) throws -> Any {
    try invoke(args: args)
  }
}

struct Argument {
  let type: FunctionSignature.ArgumentType
  let value: Any

  var formattedValue: String {
    let value = AnyCalcExpression.stringify(value) ?? ""
    switch type {
    case .string:
      return "'\(value)'"
    case .integer, .number, .boolean, .datetime, .color, .url, .dict, .array, .any:
      return value
    }
  }
}

struct FunctionSignature: Decodable {
  let arguments: [ArgumentSignature]
  let resultType: ArgumentType

  struct ArgumentSignature: Decodable, Equatable {
    let type: ArgumentType
    var vararg: Bool?
  }

  var arity: CalcExpression.Arity {
    if arguments.last?.vararg == true {
      .atLeast(arguments.count)
    } else {
      .exactly(arguments.count)
    }
  }

  enum ArgumentType: String, Decodable, CaseIterable {
    case string
    case number
    case integer
    case boolean
    case datetime
    case color
    case url
    case dict
    case array
    case any

    func check(arg: Any) -> Bool {
      type(of: arg) == swiftType
    }

    var swiftType: Any.Type {
      switch self {
      case .string:
        String.self
      case .number:
        Double.self
      case .integer:
        Int.self
      case .boolean:
        Bool.self
      case .datetime:
        Date.self
      case .color:
        Color.self
      case .url:
        URL.self
      case .dict:
        [String: AnyHashable].self
      case .array:
        [AnyHashable].self
      case .any:
        Any.self
      }
    }

    var name: String {
      switch self {
      case .datetime:
        "DateTime"
      default:
        rawValue.stringWithFirstCharCapitalized()
      }
    }

    static func from(type: Any.Type) throws -> ArgumentType {
      guard let type = allCases.first(where: { $0.swiftType == type }) else {
        throw Error.type.message
      }
      return type
    }
  }

  enum Error {
    case cast(ArgumentType)
    case arityMismatch
    case typeMismatch(args: [Argument])
    case argumentTypeMismatch(Int, ArgumentType, ArgumentType)
    case resultTypeMismatch(ArgumentType, ArgumentType)
    case notFound
    case type

    var message: AnyCalcExpression.Error {
      AnyCalcExpression.Error.message(description)
    }

    private var description: String {
      switch self {
      case let .cast(type):
        "Argument couldn't be casted to \(type.rawValue.capitalized)"
      case .arityMismatch:
        "Function arity mismatch"
      case let .typeMismatch(args):
        "Type mismatch \(args.map(\.formattedValue).joined(separator: " "))"
      case let .argumentTypeMismatch(i, expected, found):
        "Argument \(i + 1) type mismatch, expected: \(expected), found: \(found)"
      case let .resultTypeMismatch(expected, found):
        "Result type mismatch, expected: \(expected), found: \(found)"
      case .notFound:
        "Function with signature is not found"
      case .type:
        "Type is not supported"
      }
    }
  }

  fileprivate func checkArguments(args: [Any]) throws {
    guard arity.matches(.exactly(args.count)) else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    try zip(allArguments(args.count), args).forEach { signatureArg, arg in
      if !signatureArg.type.check(arg: arg) {
        throw FunctionSignature.Error.cast(signatureArg.type).message
      }
    }
  }

  fileprivate func allArguments(_ i: Int) -> [ArgumentSignature] {
    if let last = arguments.last, last.vararg == true {
      (arguments + Array(repeating: last, times: UInt(i - arguments.count))).map {
        .init(type: $0.type)
      }
    } else {
      arguments
    }
  }

  fileprivate func verify(_ signature: FunctionSignature) throws {
    guard arity.matches(signature.arity) else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    try zip(arguments, signature.arguments).enumerated().forEach { i, a in
      if a.0 != a.1 {
        throw Error.argumentTypeMismatch(i, a.0.type, a.1.type).message
      }
    }
    if resultType != signature.resultType {
      throw FunctionSignature.Error.resultTypeMismatch(resultType, signature.resultType).message
    }
  }
}
