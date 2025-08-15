// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

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
