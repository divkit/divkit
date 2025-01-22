// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputValidatorExpression: Sendable {
  public static let type: String = "expression"
  public let allowEmpty: Expression<Bool> // default value: false
  public let condition: Expression<Bool>
  public let labelId: Expression<String>
  public let variable: String

  public func resolveAllowEmpty(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(allowEmpty) ?? false
  }

  public func resolveCondition(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(condition)
  }

  public func resolveLabelId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(labelId)
  }

  init(
    allowEmpty: Expression<Bool>? = nil,
    condition: Expression<Bool>,
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
