// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivStrokeStyle: Sendable {
  case divStrokeStyleSolid(DivStrokeStyleSolid)
  case divStrokeStyleDashed(DivStrokeStyleDashed)

  public var value: Serializable {
    switch self {
    case let .divStrokeStyleSolid(value):
      return value
    case let .divStrokeStyleDashed(value):
      return value
    }
  }
}

#if DEBUG
extension DivStrokeStyle: Equatable {
  public static func ==(lhs: DivStrokeStyle, rhs: DivStrokeStyle) -> Bool {
    switch (lhs, rhs) {
    case let (.divStrokeStyleSolid(l), .divStrokeStyleSolid(r)):
      return l == r
    case let (.divStrokeStyleDashed(l), .divStrokeStyleDashed(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivStrokeStyle: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
