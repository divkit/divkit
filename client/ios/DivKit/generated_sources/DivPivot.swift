// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivPivot {
  case divPivotFixed(DivPivotFixed)
  case divPivotPercentage(DivPivotPercentage)

  public var value: Serializable {
    switch self {
    case let .divPivotFixed(value):
      return value
    case let .divPivotPercentage(value):
      return value
    }
  }
}

#if DEBUG
extension DivPivot: Equatable {
  public static func ==(lhs: DivPivot, rhs: DivPivot) -> Bool {
    switch (lhs, rhs) {
    case let (.divPivotFixed(l), .divPivotFixed(r)):
      return l == r
    case let (.divPivotPercentage(l), .divPivotPercentage(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivPivot: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
