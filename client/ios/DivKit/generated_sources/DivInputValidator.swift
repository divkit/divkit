// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputValidator: Sendable {
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

extension DivInputValidator {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivInputValidatorRegex.type:
      self = .divInputValidatorRegex(try DivInputValidatorRegex(dictionary: dictionary, context: context))
    case DivInputValidatorExpression.type:
      self = .divInputValidatorExpression(try DivInputValidatorExpression(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
