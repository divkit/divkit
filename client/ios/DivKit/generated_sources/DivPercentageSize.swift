// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPercentageSize {
  public static let type: String = "percentage"
  public let value: Expression<Double> // constraint: number > 0

  public func resolveValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: value)
  }

  static let valueValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    value: Expression<Double>
  ) {
    self.value = value
  }
}

#if DEBUG
extension DivPercentageSize: Equatable {
  public static func ==(lhs: DivPercentageSize, rhs: DivPercentageSize) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPercentageSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
