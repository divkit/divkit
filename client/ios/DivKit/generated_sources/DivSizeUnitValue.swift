// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSizeUnitValue: Sendable {
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let value: Expression<Int> // constraint: number >= 0

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(unit) ?? DivSizeUnit.dp
  }

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(value)
  }

  static let valueValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      unit: try dictionary.getOptionalExpressionField("unit", context: context),
      value: try dictionary.getExpressionField("value", validator: Self.valueValidator, context: context)
    )
  }

  init(
    unit: Expression<DivSizeUnit>? = nil,
    value: Expression<Int>
  ) {
    self.unit = unit ?? .value(.dp)
    self.value = value
  }
}

#if DEBUG
extension DivSizeUnitValue: Equatable {
  public static func ==(lhs: DivSizeUnitValue, rhs: DivSizeUnitValue) -> Bool {
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

extension DivSizeUnitValue: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
