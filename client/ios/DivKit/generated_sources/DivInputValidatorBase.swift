// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInputValidatorBase {
  public let allowEmpty: Expression<Bool> // default value: false
  public let labelId: Expression<String>? // at least 1 char
  public let variable: String? // at least 1 char

  public func resolveAllowEmpty(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: allowEmpty) ?? false
  }

  public func resolveLabelId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: labelId, initializer: { $0 })
  }

  static let allowEmptyValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let labelIdValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let variableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    allowEmpty: Expression<Bool>? = nil,
    labelId: Expression<String>? = nil,
    variable: String? = nil
  ) {
    self.allowEmpty = allowEmpty ?? .value(false)
    self.labelId = labelId
    self.variable = variable
  }
}

#if DEBUG
extension DivInputValidatorBase: Equatable {
  public static func ==(lhs: DivInputValidatorBase, rhs: DivInputValidatorBase) -> Bool {
    guard
      lhs.allowEmpty == rhs.allowEmpty,
      lhs.labelId == rhs.labelId,
      lhs.variable == rhs.variable
    else {
      return false
    }
    return true
  }
}
#endif

extension DivInputValidatorBase: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["allow_empty"] = allowEmpty.toValidSerializationValue()
    result["label_id"] = labelId?.toValidSerializationValue()
    result["variable"] = variable
    return result
  }
}
