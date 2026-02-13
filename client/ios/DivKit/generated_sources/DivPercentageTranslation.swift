// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPercentageTranslation: Sendable {
  public static let type: String = "translation-percentage"
  public let value: Expression<Double>

  public func resolveValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      value: try dictionary.getExpressionField("value", context: context)
    )
  }

  init(
    value: Expression<Double>
  ) {
    self.value = value
  }
}

#if DEBUG
extension DivPercentageTranslation: Equatable {
  public static func ==(lhs: DivPercentageTranslation, rhs: DivPercentageTranslation) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPercentageTranslation: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
