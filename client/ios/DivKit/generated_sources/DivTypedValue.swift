// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTypedValue: Sendable {
  case stringValue(StringValue)
  case integerValue(IntegerValue)
  case numberValue(NumberValue)
  case colorValue(ColorValue)
  case booleanValue(BooleanValue)
  case urlValue(UrlValue)
  case dictValue(DictValue)
  case arrayValue(ArrayValue)

  public var value: Serializable {
    switch self {
    case let .stringValue(value):
      return value
    case let .integerValue(value):
      return value
    case let .numberValue(value):
      return value
    case let .colorValue(value):
      return value
    case let .booleanValue(value):
      return value
    case let .urlValue(value):
      return value
    case let .dictValue(value):
      return value
    case let .arrayValue(value):
      return value
    }
  }
}

#if DEBUG
extension DivTypedValue: Equatable {
  public static func ==(lhs: DivTypedValue, rhs: DivTypedValue) -> Bool {
    switch (lhs, rhs) {
    case let (.stringValue(l), .stringValue(r)):
      return l == r
    case let (.integerValue(l), .integerValue(r)):
      return l == r
    case let (.numberValue(l), .numberValue(r)):
      return l == r
    case let (.colorValue(l), .colorValue(r)):
      return l == r
    case let (.booleanValue(l), .booleanValue(r)):
      return l == r
    case let (.urlValue(l), .urlValue(r)):
      return l == r
    case let (.dictValue(l), .dictValue(r)):
      return l == r
    case let (.arrayValue(l), .arrayValue(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTypedValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
