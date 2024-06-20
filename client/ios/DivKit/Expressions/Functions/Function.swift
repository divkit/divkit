import Foundation

import BasePublic

protocol Function {
  func invoke(_ args: [Any], context: ExpressionContext) throws -> Any
  func invoke(args: [Subexpression], context: ExpressionContext) throws -> Any
}

extension Function {
  func invoke(args: [Subexpression], context: ExpressionContext) throws -> Any {
    try invoke(
      args.map { try $0.evaluate(context) },
      context: context
    )
  }
}

protocol SimpleFunction: Function {
  var signature: FunctionSignature { get throws }
}

struct NoMatchingSignatureError: Error {}

struct ConstantFunction<R>: SimpleFunction {
  private let value: R

  init(_ value: R) {
    self.value = value
  }

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [],
        resultType: .from(R.self)
      )
    }
  }

  func invoke(_: [Any], context _: ExpressionContext) throws -> Any {
    value
  }
}

struct LazyFunction: Function {
  private let impl: ([Subexpression], ExpressionContext) throws -> Any

  init(_ impl: @escaping ([Subexpression], ExpressionContext) throws -> Any) {
    self.impl = impl
  }

  func invoke(_: [Any], context _: ExpressionContext) throws -> Any {
    throw ExpressionError("Lazy function must be called with lazy args")
  }

  func invoke(args: [Subexpression], context: ExpressionContext) throws -> Any {
    try impl(args, context)
  }
}

struct FunctionNullary<R>: SimpleFunction {
  private let impl: (ExpressionContext) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping () throws -> R) {
    self.impl = { _ in try impl() }
  }

  init(impl: @escaping (ExpressionContext) throws -> R) {
    self.impl = impl
  }

  func invoke(_: [Any], context: ExpressionContext) throws -> Any {
    try impl(context)
  }
}

struct FunctionUnary<T1, R>: SimpleFunction {
  private let impl: (T1) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(T1.self)),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping (T1) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    try checkArgs(args, count: 1)
    return try impl(castArg(args[0]))
  }
}

struct FunctionBinary<T1, T2, R>: SimpleFunction {
  private let impl: (T1, T2, ExpressionContext) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(T1.self)),
          .init(type: .from(T2.self)),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2) throws -> R) {
    self.impl = { arg1, arg2, _ in try impl(arg1, arg2) }
  }

  init(impl: @escaping (T1, T2, ExpressionContext) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context: ExpressionContext) throws -> Any {
    try checkArgs(args, count: 2)
    return try impl(
      castArg(args[0]),
      castArg(args[1]),
      context
    )
  }
}

struct FunctionTernary<T1, T2, T3, R>: SimpleFunction {
  private let impl: (T1, T2, T3) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(T1.self)),
          .init(type: .from(T2.self)),
          .init(type: .from(T3.self)),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2, T3) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    try checkArgs(args, count: 3)
    return try impl(
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
          .init(type: .from(T1.self)),
          .init(type: .from(T2.self)),
          .init(type: .from(T3.self)),
          .init(type: .from(T4.self)),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2, T3, T4) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    try checkArgs(args, count: 4)
    return try impl(
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
          .init(type: .from(T1.self), vararg: true),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping ([T1]) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    try checkVarArgs(args, count: 1)
    return try impl(args.map { try castArg($0) })
  }
}

struct FunctionVarBinary<T1, T2, R>: SimpleFunction {
  private let impl: (T1, [T2]) throws -> R

  var signature: FunctionSignature {
    get throws {
      try .init(
        arguments: [
          .init(type: .from(T1.self)),
          .init(type: .from(T2.self), vararg: true),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping (T1, [T2]) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    try checkVarArgs(args, count: 2)
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
          .init(type: .from(T1.self)),
          .init(type: .from(T2.self)),
          .init(type: .from(T3.self), vararg: true),
        ],
        resultType: .from(R.self)
      )
    }
  }

  init(impl: @escaping (T1, T2, [T3]) throws -> R) {
    self.impl = impl
  }

  func invoke(_ args: [Any], context _: ExpressionContext) throws -> Any {
    try checkVarArgs(args, count: 3)
    return try impl(
      castArg(args[0]),
      castArg(args[1]),
      args.dropFirst(2).map { try castArg($0) }
    )
  }
}

struct OverloadedFunction: Function {
  let functions: [SimpleFunction]
  private let makeError: ([Any]) -> Error

  init(functions: [SimpleFunction], makeError: (([Any]) -> Error)? = nil) {
    self.functions = functions
    self.makeError = makeError ?? { _ in NoMatchingSignatureError() }
  }

  func invoke(_ args: [Any], context: ExpressionContext) throws -> Any {
    let arguments = try args.map {
      try ArgumentSignature(type: .from(unwrapHashableType($0)))
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
      return try function.invoke(args, context: context)
    }
    throw makeError(args)
  }

  private func getFunction(
    args: [ArgumentSignature],
    predicate: (ArgumentSignature, ArgumentSignature) -> Bool
  ) throws -> SimpleFunction? {
    let sutableFunctions = try functions.filter {
      try $0.signature.isApplicable(args: args, predicate: predicate)
    }
    if sutableFunctions.count > 1 {
      throw ExpressionError("Multiple matching overloads")
    }
    return sutableFunctions.first
  }
}

struct ArgumentSignature: Decodable, Equatable {
  let type: ArgumentType
  var vararg: Bool?
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
      DivDictionary.self
    case .array:
      [AnyHashable].self
    }
  }

  func isCastableFrom(_ type: ArgumentType) -> Bool {
    if self == type {
      return true
    }
    switch self {
    case .number:
      return type == .integer
    default:
      return false
    }
  }

  static func from(_ type: Any.Type) throws -> ArgumentType {
    guard let type = allCases.first(where: { $0.swiftType == type }) else {
      throw ExpressionError("Type is not supported: \(type).")
    }
    return type
  }
}

struct FunctionSignature: Decodable, Equatable {
  let arguments: [ArgumentSignature]
  let resultType: ArgumentType

  func isApplicable(
    args: [ArgumentSignature],
    predicate: (ArgumentSignature, ArgumentSignature) -> Bool
  ) -> Bool {
    if !argsMatch(args) {
      return false
    }

    let expectedArgs: [ArgumentSignature]
    if let last = arguments.last, last.vararg == true {
      let extraArgs = Array(repeating: last, times: UInt(args.count - arguments.count))
      expectedArgs = (arguments + extraArgs).map { ArgumentSignature(type: $0.type) }
    } else {
      expectedArgs = arguments
    }
    return zip(expectedArgs, args).allSatisfy(predicate)
  }

  private func argsMatch(_ args: [ArgumentSignature]) -> Bool {
    args.count == arguments.count ||
      (arguments.last?.vararg == true && args.count > arguments.count)
  }
}

private func unwrapHashableType(_ value: Any) -> Any.Type {
  let valueType = type(of: value)
  if valueType is AnyHashable.Type, let hashableValue = value as? AnyHashable {
    return type(of: hashableValue.base)
  }
  return valueType
}

private func checkArgs(_ args: [Any], count: Int) throws {
  if args.count != count {
    throw ExpressionError("Exactly \(count) argument(s) expected.")
  }
}

private func checkVarArgs(_ args: [Any], count: Int) throws {
  if args.count < count {
    throw ExpressionError("At least \(count) argument(s) expected.")
  }
}

private func castArg<T>(_ value: Any) throws -> T {
  if let castedValue = value as? T {
    return castedValue
  }

  if T.self == Double.self, let intValue = value as? Int {
    return Double(intValue) as! T
  }

  throw ExpressionError(
    "Invalid argument type: expected \(formatTypeForError(T.self)), got \(formatTypeForError(value))."
  )
}
