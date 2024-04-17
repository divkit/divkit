import Foundation

import BasePublic

protocol Function {
  func invoke(args: [Any]) throws -> Any
  func verify(signature: FunctionSignature) -> Bool
}

protocol SimpleFunction: Function {
  var signature: FunctionSignature { get throws }
}

extension SimpleFunction {
  func verify(signature: FunctionSignature) -> Bool {
    if let selfSignature = try? self.signature {
      signature.verify(selfSignature)
    } else {
      false
    }
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
    try impl(
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
  let functions: [SimpleFunction]
  private let makeError: ([Argument]) -> Error

  init(functions: [SimpleFunction], makeError: (([Argument]) -> Error)? = nil) {
    self.functions = functions
    self.makeError = makeError ?? { _ in CalcExpression.Error.noMatchingSignature }
  }

  func verify(signature: FunctionSignature) -> Bool {
    functions.contains { $0.verify(signature: signature) }
  }

  func invoke(args: [Any]) throws -> Any {
    let arguments = try args.map {
      try ArgumentSignature(type: .from(type: type(of: $0)))
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
    args: [ArgumentSignature],
    predicate: (ArgumentSignature, ArgumentSignature) -> Bool
  ) throws -> SimpleFunction? {
    let sutableFunctions = try functions.filter {
      try $0.signature.isApplicable(args: args, predicate: predicate)
    }
    if sutableFunctions.count > 1 {
      throw CalcExpression.Error.message("Multiple matching overloads")
    }
    return sutableFunctions.first
  }
}

struct Argument {
  let type: ArgumentType
  let value: Any

  var formattedValue: String {
    let value = CalcExpression.stringify(value)
    switch type {
    case .string:
      return "'\(value)'"
    case .integer, .number, .boolean, .datetime, .color, .url, .dict, .array, .any, .error:
      return value
    }
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
  case any
  case error

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
    case .error:
      CalcExpression.Error.self
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
      throw CalcExpression.Error.message("Type is not supported")
    }
    return type
  }
}

struct FunctionSignature: Decodable {
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

  fileprivate func verify(_ signature: FunctionSignature) -> Bool {
    guard argsMatch(signature.arguments) else {
      return false
    }
    let argsMatch = zip(arguments, signature.arguments).enumerated().allSatisfy { _, args in
      let expectedArg = args.0
      let arg = args.1
      return expectedArg == arg
    }
    return argsMatch && resultType == signature.resultType
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
