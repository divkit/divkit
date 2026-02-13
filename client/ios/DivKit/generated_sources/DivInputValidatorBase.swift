// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputValidatorBase: Sendable {
  public let allowEmpty: Expression<Bool> // default value: false
  public let labelId: Expression<String>?
  public let variable: String?

  public func resolveAllowEmpty(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(allowEmpty) ?? false
  }

  public func resolveLabelId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(labelId)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      allowEmpty: try dictionary.getOptionalExpressionField("allow_empty", context: context),
      labelId: try dictionary.getOptionalExpressionField("label_id", context: context),
      variable: try dictionary.getOptionalField("variable", context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["allow_empty"] = allowEmpty.toValidSerializationValue()
    result["label_id"] = labelId?.toValidSerializationValue()
    result["variable"] = variable
    return result
  }
}
