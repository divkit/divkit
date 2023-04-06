// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivShape {
  case divRoundedRectangleShape(DivRoundedRectangleShape)
  case divCircleShape(DivCircleShape)

  public var value: Serializable {
    switch self {
    case let .divRoundedRectangleShape(value):
      return value
    case let .divCircleShape(value):
      return value
    }
  }
}

#if DEBUG
extension DivShape: Equatable {
  public static func ==(lhs: DivShape, rhs: DivShape) -> Bool {
    switch (lhs, rhs) {
    case let (.divRoundedRectangleShape(l), .divRoundedRectangleShape(r)):
      return l == r
    case let (.divCircleShape(l), .divCircleShape(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivShape: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
