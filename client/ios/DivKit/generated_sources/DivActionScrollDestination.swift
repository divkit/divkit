// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionScrollDestination: Sendable {
  case offsetDestination(OffsetDestination)
  case indexDestination(IndexDestination)
  case startDestination(StartDestination)
  case endDestination(EndDestination)

  public var value: Serializable {
    switch self {
    case let .offsetDestination(value):
      return value
    case let .indexDestination(value):
      return value
    case let .startDestination(value):
      return value
    case let .endDestination(value):
      return value
    }
  }
}

#if DEBUG
extension DivActionScrollDestination: Equatable {
  public static func ==(lhs: DivActionScrollDestination, rhs: DivActionScrollDestination) -> Bool {
    switch (lhs, rhs) {
    case let (.offsetDestination(l), .offsetDestination(r)):
      return l == r
    case let (.indexDestination(l), .indexDestination(r)):
      return l == r
    case let (.startDestination(l), .startDestination(r)):
      return l == r
    case let (.endDestination(l), .endDestination(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivActionScrollDestination: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
