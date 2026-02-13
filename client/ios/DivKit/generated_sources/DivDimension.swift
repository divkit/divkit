// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDimension: Sendable {
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let value: Expression<Double>

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(unit) ?? DivSizeUnit.dp
  }

  public func resolveValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      unit: try dictionary.getOptionalExpressionField("unit", context: context),
      value: try dictionary.getExpressionField("value", context: context)
    )
  }

  init(
    unit: Expression<DivSizeUnit>? = nil,
    value: Expression<Double>
  ) {
    self.unit = unit ?? .value(.dp)
    self.value = value
  }
}

#if DEBUG
extension DivDimension: Equatable {
  public static func ==(lhs: DivDimension, rhs: DivDimension) -> Bool {
    guard
      lhs.unit == rhs.unit,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivDimension: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
