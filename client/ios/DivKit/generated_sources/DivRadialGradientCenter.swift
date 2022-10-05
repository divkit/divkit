// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public enum DivRadialGradientCenter {
  case divRadialGradientFixedCenter(DivRadialGradientFixedCenter)
  case divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter)

  public var value: Serializable {
    switch self {
    case let .divRadialGradientFixedCenter(value):
      return value
    case let .divRadialGradientRelativeCenter(value):
      return value
    }
  }
}

#if DEBUG
extension DivRadialGradientCenter: Equatable {
  public static func ==(lhs: DivRadialGradientCenter, rhs: DivRadialGradientCenter) -> Bool {
    switch (lhs, rhs) {
    case let (.divRadialGradientFixedCenter(l), .divRadialGradientFixedCenter(r)):
      return l == r
    case let (.divRadialGradientRelativeCenter(l), .divRadialGradientRelativeCenter(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivRadialGradientCenter: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
