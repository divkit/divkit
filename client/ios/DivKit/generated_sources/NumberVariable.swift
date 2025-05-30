// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class NumberVariable: Sendable {
  public static let type: String = "number"
  public let name: String
  public let value: Expression<Double>

  public func resolveValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(value)
  }

  init(
    name: String,
    value: Expression<Double>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension NumberVariable: Equatable {
  public static func ==(lhs: NumberVariable, rhs: NumberVariable) -> Bool {
    guard
      lhs.name == rhs.name,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension NumberVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
