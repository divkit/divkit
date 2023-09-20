// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class NumberValue {
  public static let type: String = "number"
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
extension NumberValue: Equatable {
  public static func ==(lhs: NumberValue, rhs: NumberValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension NumberValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
