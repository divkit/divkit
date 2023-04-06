// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivTextGradient {
  case divLinearGradient(DivLinearGradient)
  case divRadialGradient(DivRadialGradient)

  public var value: Serializable {
    switch self {
    case let .divLinearGradient(value):
      return value
    case let .divRadialGradient(value):
      return value
    }
  }
}

#if DEBUG
extension DivTextGradient: Equatable {
  public static func ==(lhs: DivTextGradient, rhs: DivTextGradient) -> Bool {
    switch (lhs, rhs) {
    case let (.divLinearGradient(l), .divLinearGradient(r)):
      return l == r
    case let (.divRadialGradient(l), .divRadialGradient(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTextGradient: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
