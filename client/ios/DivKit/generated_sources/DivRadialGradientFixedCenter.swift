// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRadialGradientFixedCenter {
  public static let type: String = "fixed"
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let value: Expression<Int>

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(unit) ?? DivSizeUnit.dp
  }

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(value)
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
extension DivRadialGradientFixedCenter: Equatable {
  public static func ==(lhs: DivRadialGradientFixedCenter, rhs: DivRadialGradientFixedCenter) -> Bool {
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

extension DivRadialGradientFixedCenter: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
