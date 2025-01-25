// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputFilter {
  case divInputFilterRegex(DivInputFilterRegex)
  case divInputFilterExpression(DivInputFilterExpression)

  public var value: Serializable {
    switch self {
    case let .divInputFilterRegex(value):
      return value
    case let .divInputFilterExpression(value):
      return value
    }
  }
}

#if DEBUG
extension DivInputFilter: Equatable {
  public static func ==(lhs: DivInputFilter, rhs: DivInputFilter) -> Bool {
    switch (lhs, rhs) {
    case let (.divInputFilterRegex(l), .divInputFilterRegex(r)):
      return l == r
    case let (.divInputFilterExpression(l), .divInputFilterExpression(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivInputFilter: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
