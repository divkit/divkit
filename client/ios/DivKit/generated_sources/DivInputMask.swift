// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputMask {
  case divFixedLengthInputMask(DivFixedLengthInputMask)
  case divCurrencyInputMask(DivCurrencyInputMask)
  case divPhoneInputMask(DivPhoneInputMask)

  public var value: Serializable & DivInputMaskBase {
    switch self {
    case let .divFixedLengthInputMask(value):
      return value
    case let .divCurrencyInputMask(value):
      return value
    case let .divPhoneInputMask(value):
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
    case let (.divPhoneInputMask(l), .divPhoneInputMask(r)):
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
