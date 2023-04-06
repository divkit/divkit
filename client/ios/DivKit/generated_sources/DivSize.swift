// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivSize {
  case divFixedSize(DivFixedSize)
  case divMatchParentSize(DivMatchParentSize)
  case divWrapContentSize(DivWrapContentSize)

  public var value: Serializable {
    switch self {
    case let .divFixedSize(value):
      return value
    case let .divMatchParentSize(value):
      return value
    case let .divWrapContentSize(value):
      return value
    }
  }
}

#if DEBUG
extension DivSize: Equatable {
  public static func ==(lhs: DivSize, rhs: DivSize) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedSize(l), .divFixedSize(r)):
      return l == r
    case let (.divMatchParentSize(l), .divMatchParentSize(r)):
      return l == r
    case let (.divWrapContentSize(l), .divWrapContentSize(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
