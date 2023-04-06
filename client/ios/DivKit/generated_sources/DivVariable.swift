// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivVariable {
  case stringVariable(StringVariable)
  case numberVariable(NumberVariable)
  case integerVariable(IntegerVariable)
  case booleanVariable(BooleanVariable)
  case colorVariable(ColorVariable)
  case urlVariable(UrlVariable)

  public var value: Serializable {
    switch self {
    case let .stringVariable(value):
      return value
    case let .numberVariable(value):
      return value
    case let .integerVariable(value):
      return value
    case let .booleanVariable(value):
      return value
    case let .colorVariable(value):
      return value
    case let .urlVariable(value):
      return value
    }
  }
}

#if DEBUG
extension DivVariable: Equatable {
  public static func ==(lhs: DivVariable, rhs: DivVariable) -> Bool {
    switch (lhs, rhs) {
    case let (.stringVariable(l), .stringVariable(r)):
      return l == r
    case let (.numberVariable(l), .numberVariable(r)):
      return l == r
    case let (.integerVariable(l), .integerVariable(r)):
      return l == r
    case let (.booleanVariable(l), .booleanVariable(r)):
      return l == r
    case let (.colorVariable(l), .colorVariable(r)):
      return l == r
    case let (.urlVariable(l), .urlVariable(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
