// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFixedSize {
  public static let type: String = "fixed"
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

  init(
    unit: Expression<DivSizeUnit>? = nil,
    value: Expression<Int>
  ) {
    self.unit = unit ?? .value(.dp)
    self.value = value
  }
}

#if DEBUG
extension DivFixedSize: Equatable {
  public static func ==(lhs: DivFixedSize, rhs: DivFixedSize) -> Bool {
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

extension DivFixedSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
