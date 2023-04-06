// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivInputMask {
  case divFixedLengthInputMask(DivFixedLengthInputMask)
  case divCurrencyInputMask(DivCurrencyInputMask)

  public var value: Serializable {
    switch self {
    case let .divFixedLengthInputMask(value):
      return value
    case let .divCurrencyInputMask(value):
      return value
    }
  }
}

#if DEBUG
extension DivInputMask: Equatable {
  public static func ==(lhs: DivInputMask, rhs: DivInputMask) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedLengthInputMask(l), .divFixedLengthInputMask(r)):
      return l == r
    case let (.divCurrencyInputMask(l), .divCurrencyInputMask(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivInputMask: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
