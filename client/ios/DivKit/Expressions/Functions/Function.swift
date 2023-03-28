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
    (try? signature.arity) ?? 0
  }

  func verify(signature: FunctionSignature) throws {
    try signature.verify(self.signature)
  }
}

struct FunctionNullary<R>: SimpleFunction {
  private let impl: () throws -> R

  var signature: FunctionSignature {
    get throws {
      .init(
        arguments: [],
        resultType: try .from(type: R.self)
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
      .init(
        arguments: [
          .init(type: try .from(type: T1.self)),
        ],
        resultType: try .from(type: R.self)
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
      .init(
        arguments: [
          .init(type: try .from(type: T1.self)),
          .init(type: try .from(type: T2.self)),
        ],
        resultType: try .from(type: R.self)
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
      .init(
        arguments: [
          .init(type: try .from(type: T1.self)),
          .init(type: try .from(type: T2.self)),
          .init(type: try .from(type: T3.self)),
        ],
        resultType: try .from(type: R.self)
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
      .init(
        arguments: [
          .init(type: try .from(type: T1.self)),
          .init(type: try .from(type: T2.self)),
          .init(type: try .from(type: T3.self)),
          .init(type: try .from(type: T4.self)),
        ],
        resultType: try .from(type: R.self)
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
      .init(
        arguments: [
          .init(type: try .from(type: T1.self), vararg: true),
        ],
        resultType: try .from(type: R.self)
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

struct OverloadedFunction: Function {
  let functions: [SimpleFunction]
  let makeError: ([Any]) -> Error
  var arity: CalcExpression.Arity {
    functions.first?.arity ?? 0
  }

  init(functions: [SimpleFunction], makeError: (([Any]) -> Error)? = nil) {
    self.functions = functions
    self.makeError = makeError ?? { FunctionSignature.Error.typeMismatch(args: $0).message }
  }

  func verify(signature: FunctionSignature) throws {
    if !functions.contains(where: { (try? $0.verify(signature: signature)) != nil }) {
      throw FunctionSignature.Error.notFound.message
    }
  }

  func invoke(args: [Any]) throws -> Any {
    let arguments = try args.map { FunctionSignature.Argument(type: try .from(type: type(of: $0))) }
    guard let function = try functions
      .first(where: { try $0.signature.allArguments(arguments.count) == arguments })
    else {
      throw makeError(args)
    }
    return try function.invoke(args: args)
  }
}

struct FunctionSignature: Decodable {
  let arguments: [Argument]
  let resultType: ArgumentType

  struct Argument: Decodable, Equatable {
    let type: ArgumentType
    var vararg: Bool?
  }

  var arity: CalcExpression.Arity {
    if arguments.last?.vararg == true {
      return .atLeast(arguments.count)
    } else {
      return .exactly(arguments.count)
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

    func check(arg: Any) -> Bool {
      type(of: arg) == swiftType
    }

    var swiftType: Any.Type {
      switch self {
      case .string:
        return String.self
      case .number:
        return Double.self
      case .integer:
        return Int.self
      case .boolean:
        return Bool.self
      case .datetime:
        return Date.self
      case .color:
        return Color.self
      case .url:
        return URL.self
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
    case typeMismatch(args: [Any])
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
        return "Argument couldn't be casted to \(type.rawValue.capitalized)"
      case .arityMismatch:
        return "Function arity mismatch"
      case let .typeMismatch(args):
        return "Type mismatch \(args.map { "\($0)" }.joined(separator: " "))"
      case let .argumentTypeMismatch(i, expected, found):
        return "Argument \(i + 1) type mismatch, expected: \(expected), found: \(found)"
      case let .resultTypeMismatch(expected, found):
        return "Result type mismatch, expected: \(expected), found: \(found)"
      case .notFound:
        return "Function with signature is not found"
      case .type:
        return "Type is not supported"
      }
    }
  }
}

extension Function {
  func symbolEvaluator(_ args: [Any]) throws -> Any {
    try (self as? SimpleFunction)?.signature.checkArguments(args: args)
    return try invoke(args: args)
  }
}

extension FunctionSignature {
  func checkArguments(args: [Any]) throws {
    guard arity.matches(.exactly(args.count)) else {
      throw FunctionSignature.Error.arityMismatch.message
    }
    try zip(allArguments(args.count), args).forEach { signatureArg, arg in
      if !signatureArg.type.check(arg: arg) {
        throw FunctionSignature.Error.cast(signatureArg.type).message
      }
    }
  }

  func allArguments(_ i: Int) -> [Argument] {
    if let last = arguments.last, last.vararg == true {
      return (arguments + Array(repeating: last, times: UInt(i - arguments.count))).map {
        .init(type: $0.type)
      }
    } else {
      return arguments
    }
  }

  func verify(_ signature: FunctionSignature) throws {
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
