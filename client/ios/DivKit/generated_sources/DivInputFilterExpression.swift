// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputFilterExpression: Sendable {
  public static let type: String = "expression"
  public let condition: Expression<Bool>

  public func resolveCondition(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(condition)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      condition: try dictionary.getExpressionField("condition", context: context)
    )
  }

  init(
    condition: Expression<Bool>
  ) {
    self.condition = condition
  }
}

#if DEBUG
extension DivInputFilterExpression: Equatable {
  public static func ==(lhs: DivInputFilterExpression, rhs: DivInputFilterExpression) -> Bool {
    guard
      lhs.condition == rhs.condition
    else {
      return false
    }
    return true
  }
}
#endif

extension DivInputFilterExpression: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["condition"] = condition.toValidSerializationValue()
    return result
  }
}
