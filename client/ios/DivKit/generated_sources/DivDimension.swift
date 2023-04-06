// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivDimension {
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let value: Expression<Double>

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveStringBasedValue(expression: unit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.dp
  }

  public func resolveValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: value)
  }

  static let unitValidator: AnyValueValidator<DivSizeUnit> =
    makeNoOpValueValidator()

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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
