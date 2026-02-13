// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionShowTooltip: Sendable {
  public static let type: String = "show_tooltip"
  public let id: Expression<String>
  public let multiple: Expression<Bool>?

  public func resolveId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(id)
  }

  public func resolveMultiple(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(multiple)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      id: try dictionary.getExpressionField("id", context: context),
      multiple: try dictionary.getOptionalExpressionField("multiple", context: context)
    )
  }

  init(
    id: Expression<String>,
    multiple: Expression<Bool>? = nil
  ) {
    self.id = id
    self.multiple = multiple
  }
}

#if DEBUG
extension DivActionShowTooltip: Equatable {
  public static func ==(lhs: DivActionShowTooltip, rhs: DivActionShowTooltip) -> Bool {
    guard
      lhs.id == rhs.id,
      lhs.multiple == rhs.multiple
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionShowTooltip: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["id"] = id.toValidSerializationValue()
    result["multiple"] = multiple?.toValidSerializationValue()
    return result
  }
}
