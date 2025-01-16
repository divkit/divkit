// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputValidatorRegex: Sendable {
  public static let type: String = "regex"
  public let allowEmpty: Expression<Bool> // default value: false
  public let labelId: Expression<String>
  public let pattern: Expression<String>
  public let variable: String

  public func resolveAllowEmpty(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(allowEmpty) ?? false
  }

  public func resolveLabelId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(labelId)
  }

  public func resolvePattern(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(pattern)
  }

  init(
    allowEmpty: Expression<Bool>? = nil,
    labelId: Expression<String>,
    pattern: Expression<String>,
    variable: String
  ) {
    self.allowEmpty = allowEmpty ?? .value(false)
    self.labelId = labelId
    self.pattern = pattern
    self.variable = variable
  }
}

#if DEBUG
extension DivInputValidatorRegex: Equatable {
  public static func ==(lhs: DivInputValidatorRegex, rhs: DivInputValidatorRegex) -> Bool {
    guard
      lhs.allowEmpty == rhs.allowEmpty,
      lhs.labelId == rhs.labelId,
      lhs.pattern == rhs.pattern
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

extension DivInputValidatorRegex: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["allow_empty"] = allowEmpty.toValidSerializationValue()
    result["label_id"] = labelId.toValidSerializationValue()
    result["pattern"] = pattern.toValidSerializationValue()
    result["variable"] = variable
    return result
  }
}
