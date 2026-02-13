// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPivotFixed: Sendable {
  public static let type: String = "pivot-fixed"
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let value: Expression<Int>?

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(unit) ?? DivSizeUnit.dp
  }

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      unit: try dictionary.getOptionalExpressionField("unit", context: context),
      value: try dictionary.getOptionalExpressionField("value", context: context)
    )
  }

  init(
    unit: Expression<DivSizeUnit>? = nil,
    value: Expression<Int>? = nil
  ) {
    self.unit = unit ?? .value(.dp)
    self.value = value
  }
}

#if DEBUG
extension DivPivotFixed: Equatable {
  public static func ==(lhs: DivPivotFixed, rhs: DivPivotFixed) -> Bool {
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

extension DivPivotFixed: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value?.toValidSerializationValue()
    return result
  }
}
