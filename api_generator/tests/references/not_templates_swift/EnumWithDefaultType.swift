// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

@frozen
public enum EnumWithDefaultType {
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
  public init(dictionary: [String: Any]) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case WithDefault.type:
      self = .withDefault(try WithDefault(dictionary: dictionary))
    case WithoutDefault.type:
      self = .withoutDefault(try WithoutDefault(dictionary: dictionary))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "enum_with_default_type", representation: dictionary)
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
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
