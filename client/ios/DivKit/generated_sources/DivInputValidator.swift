// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputValidator {
  case divInputValidatorRegex(DivInputValidatorRegex)
  case divInputValidatorExpression(DivInputValidatorExpression)

  public var value: Serializable {
    switch self {
    case let .divInputValidatorRegex(value):
      return value
    case let .divInputValidatorExpression(value):
      return value
    }
  }
}

#if DEBUG
extension DivInputValidator: Equatable {
  public static func ==(lhs: DivInputValidator, rhs: DivInputValidator) -> Bool {
    switch (lhs, rhs) {
    case let (.divInputValidatorRegex(l), .divInputValidatorRegex(r)):
      return l == r
    case let (.divInputValidatorExpression(l), .divInputValidatorExpression(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivInputValidator: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
