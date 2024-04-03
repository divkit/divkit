import Foundation

import BasePublic

protocol Function {
  var arity: CalcExpression.Arity { get }
  func invoke(args: [Any]) throws -> Any
  func verify(signature: FunctionSignature) throws
}

protocol SimpleFunction: Function {
  var signature: FunctionSignature { get throws }
}

extension SimpleFunction {
  var arity: CalcExpression.Arity {
    (try? signature.arity) ?? .exactly(0)
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
    try impl(castArg(args[0]))
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
    try impl(
      castArg(args[0]),
      castArg(args[1])
    )
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
    try impl(
      castArg(args[0]),
      castArg(args[1]),
      castArg(args[2])
    )
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
    try impl(
      castArg(args[0]),
      castArg(args[1]),
      castArg(args[2]),
      castArg(args[3])
    )
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
    try impl(args.map { try castArg($0) })
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
    return try impl(
      castArg(args[0]),
      args.dropFirst().map { try castArg($0) }
    )
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
    try impl(
      castArg(args[0]),
      castArg(args[1]),
      args.dropFirst(2).map { try castArg($0) }
    )
  }
}

struct OverloadedFunction: Function {
  private let functions: [SimpleFunction]
  private let makeError: ([Argument]) -> Error

  var arity: CalcExpression.Arity {
    functions.first?.arity ?? .exactly(0)
  }

  init(functions: [SimpleFunction], makeError: (([Argument]) -> Error)? = nil) {
    self.functions = functions
    self.makeError = makeError ?? { _ in FunctionSignature.Error.notFound.message }
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
    var function = try getFunction(args: arguments) {
      $0.type == $1.type
    }
    if function == nil {
      function = try getFunction(args: arguments) {
        $0.type.isCastableFrom($1.type)
      }
    }
    if let function {
      return try function.invoke(args: args)
    }
    throw makeError(zip(args, arguments).map { Argument(type: $1.type, value: $0) })
  }

  private func getFunction(
    args: [FunctionSignature.ArgumentSignature],
    predicate: (FunctionSignature.ArgumentSignature, FunctionSignature.ArgumentSignature) -> Bool
  ) throws -> SimpleFunction? {
    let sutableFunctions = try functions.filter {
      try zip($0.signature.allArguments(args.count), args).allSatisfy(predicate)
    }
    if sutableFunctions.count > 1 {
      throw CalcExpression.Error.message("Multiple matching overloads")
    }
    return sutableFunctions.first
  }
}

struct Argument {
  let type: FunctionSignature.ArgumentType
  let value: Any

  var formattedValue: String {
    let value = AnyCalcExpression.stringify(value)
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

    func isCastableFrom(_ type: ArgumentType) -> Bool {
      if self == type {
        return true
      }
      switch self {
      case .any:
        return true
      case .number:
        return type == .integer
      default:
        return false
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
    case arityMismatch
    case argumentTypeMismatch(Int, ArgumentType, ArgumentType)
    case resultTypeMismatch(ArgumentType, ArgumentType)
    case notFound
    case type

    var message: CalcExpression.Error {
      CalcExpression.Error.message(description)
    }

    private var description: String {
      switch self {
      case .arityMismatch:
        "Function arity mismatch"
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
      throw Error.arityMismatch.message
    }
    try zip(arguments, signature.arguments).enumerated().forEach { i, a in
      if a.0 != a.1 {
        throw Error.argumentTypeMismatch(i, a.0.type, a.1.type).message
      }
    }
    if resultType != signature.resultType {
      throw Error.resultTypeMismatch(resultType, signature.resultType).message
    }
  }
}

private func castArg<T>(_ value: Any) throws -> T {
  if let castedValue = value as? T {
    return castedValue
  }

  if T.self == Double.self, let intValue = value as? Int {
    return Double(intValue) as! T
  }

  throw CalcExpression.Error.message("Argument couldn't be casted to \(T.self)")
}
