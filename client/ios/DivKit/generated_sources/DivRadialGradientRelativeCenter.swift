// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRadialGradientRelativeCenter: Sendable {
  public static let type: String = "relative"
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
extension DivRadialGradientRelativeCenter: Equatable {
  public static func ==(lhs: DivRadialGradientRelativeCenter, rhs: DivRadialGradientRelativeCenter) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivRadialGradientRelativeCenter: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
