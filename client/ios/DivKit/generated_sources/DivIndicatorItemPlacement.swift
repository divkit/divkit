// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivIndicatorItemPlacement: Sendable {
  case divDefaultIndicatorItemPlacement(DivDefaultIndicatorItemPlacement)
  case divStretchIndicatorItemPlacement(DivStretchIndicatorItemPlacement)

  public var value: Serializable {
    switch self {
    case let .divDefaultIndicatorItemPlacement(value):
      return value
    case let .divStretchIndicatorItemPlacement(value):
      return value
    }
  }
}

#if DEBUG
extension DivIndicatorItemPlacement: Equatable {
  public static func ==(lhs: DivIndicatorItemPlacement, rhs: DivIndicatorItemPlacement) -> Bool {
    switch (lhs, rhs) {
    case let (.divDefaultIndicatorItemPlacement(l), .divDefaultIndicatorItemPlacement(r)):
      return l == r
    case let (.divStretchIndicatorItemPlacement(l), .divStretchIndicatorItemPlacement(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivIndicatorItemPlacement: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
