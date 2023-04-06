// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPivotPercentage {
  public static let type: String = "pivot-percentage"
  public let value: Expression<Double>

  public func resolveValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: value)
  }

  init(
    value: Expression<Double>
  ) {
    self.value = value
  }
}

#if DEBUG
extension DivPivotPercentage: Equatable {
  public static func ==(lhs: DivPivotPercentage, rhs: DivPivotPercentage) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPivotPercentage: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
