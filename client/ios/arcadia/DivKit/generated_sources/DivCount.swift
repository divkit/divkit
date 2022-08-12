// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public enum DivCount {
  case divInfinityCount(DivInfinityCount)
  case divFixedCount(DivFixedCount)

  public var value: Serializable {
    switch self {
    case let .divInfinityCount(value):
      return value
    case let .divFixedCount(value):
      return value
    }
  }
}

#if DEBUG
extension DivCount: Equatable {
  public static func ==(lhs: DivCount, rhs: DivCount) -> Bool {
    switch (lhs, rhs) {
    case let (.divInfinityCount(l), .divInfinityCount(r)):
      return l == r
    case let (.divFixedCount(l), .divFixedCount(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivCount: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    value.toDictionary()
  }
}
