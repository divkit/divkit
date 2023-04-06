// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivRadialGradientRadius {
  case divFixedSize(DivFixedSize)
  case divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius)

  public var value: Serializable {
    switch self {
    case let .divFixedSize(value):
      return value
    case let .divRadialGradientRelativeRadius(value):
      return value
    }
  }
}

#if DEBUG
extension DivRadialGradientRadius: Equatable {
  public static func ==(lhs: DivRadialGradientRadius, rhs: DivRadialGradientRadius) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedSize(l), .divFixedSize(r)):
      return l == r
    case let (.divRadialGradientRelativeRadius(l), .divRadialGradientRelativeRadius(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivRadialGradientRadius: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
