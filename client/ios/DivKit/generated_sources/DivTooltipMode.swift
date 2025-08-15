// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTooltipMode: Sendable {
  case divTooltipModeNonModal(DivTooltipModeNonModal)
  case divTooltipModeModal(DivTooltipModeModal)

  public var value: Serializable {
    switch self {
    case let .divTooltipModeNonModal(value):
      return value
    case let .divTooltipModeModal(value):
      return value
    }
  }
}

#if DEBUG
extension DivTooltipMode: Equatable {
  public static func ==(lhs: DivTooltipMode, rhs: DivTooltipMode) -> Bool {
    switch (lhs, rhs) {
    case let (.divTooltipModeNonModal(l), .divTooltipModeNonModal(r)):
      return l == r
    case let (.divTooltipModeModal(l), .divTooltipModeModal(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTooltipMode: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
