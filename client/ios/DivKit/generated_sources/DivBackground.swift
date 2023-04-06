// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivBackground {
  case divLinearGradient(DivLinearGradient)
  case divRadialGradient(DivRadialGradient)
  case divImageBackground(DivImageBackground)
  case divSolidBackground(DivSolidBackground)
  case divNinePatchBackground(DivNinePatchBackground)

  public var value: Serializable {
    switch self {
    case let .divLinearGradient(value):
      return value
    case let .divRadialGradient(value):
      return value
    case let .divImageBackground(value):
      return value
    case let .divSolidBackground(value):
      return value
    case let .divNinePatchBackground(value):
      return value
    }
  }
}

#if DEBUG
extension DivBackground: Equatable {
  public static func ==(lhs: DivBackground, rhs: DivBackground) -> Bool {
    switch (lhs, rhs) {
    case let (.divLinearGradient(l), .divLinearGradient(r)):
      return l == r
    case let (.divRadialGradient(l), .divRadialGradient(r)):
      return l == r
    case let (.divImageBackground(l), .divImageBackground(r)):
      return l == r
    case let (.divSolidBackground(l), .divSolidBackground(r)):
      return l == r
    case let (.divNinePatchBackground(l), .divNinePatchBackground(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
