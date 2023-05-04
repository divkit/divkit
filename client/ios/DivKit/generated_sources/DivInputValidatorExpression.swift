// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInputValidatorExpression {
  public static let type: String = "expression"
  public let allowEmpty: Expression<Bool> // default value: false
  public let condition: Expression<String> // at least 1 char
  public let labelId: Expression<String> // at least 1 char
  public let variable: String // at least 1 char

  public func resolveAllowEmpty(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: allowEmpty) ?? false
  }

  public func resolveCondition(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: condition, initializer: { $0 })
  }

  public func resolveLabelId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: labelId, initializer: { $0 })
  }

  static let allowEmptyValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let conditionValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let labelIdValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let variableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    allowEmpty: Expression<Bool>? = nil,
    condition: Expression<String>,
    labelId: Expression<String>,
    variable: String
  ) {
    self.allowEmpty = allowEmpty ?? .value(false)
    self.condition = condition
    self.labelId = labelId
    self.variable = variable
  }
}

#if DEBUG
extension DivInputValidatorExpression: Equatable {
  public static func ==(lhs: DivInputValidatorExpression, rhs: DivInputValidatorExpression) -> Bool {
    guard
      lhs.allowEmpty == rhs.allowEmpty,
      lhs.condition == rhs.condition,
      lhs.labelId == rhs.labelId
    else {
      return false
    }
    guard
      lhs.variable == rhs.variable
    else {
      return false
    }
    return true
  }
}
#endif

extension DivInputValidatorExpression: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["allow_empty"] = allowEmpty.toValidSerializationValue()
    result["condition"] = condition.toValidSerializationValue()
    result["label_id"] = labelId.toValidSerializationValue()
    result["variable"] = variable
    return result
  }
}
