// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

@frozen
public enum EnumWithDefaultType: Sendable {
  case withDefault(WithDefault)
  case withoutDefault(WithoutDefault)

  public var value: Serializable {
    switch self {
    case let .withDefault(value):
      return value
    case let .withoutDefault(value):
      return value
    }
  }
}

extension EnumWithDefaultType {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case WithDefault.type:
      self = .withDefault(try WithDefault(dictionary: dictionary, context: context))
    case WithoutDefault.type:
      self = .withoutDefault(try WithoutDefault(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension EnumWithDefaultType: Equatable {
  public static func ==(lhs: EnumWithDefaultType, rhs: EnumWithDefaultType) -> Bool {
    switch (lhs, rhs) {
    case let (.withDefault(l), .withDefault(r)):
      return l == r
    case let (.withoutDefault(l), .withoutDefault(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension EnumWithDefaultType: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
