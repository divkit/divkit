// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class IntegerVariable: Sendable {
  public static let type: String = "integer"
  public let name: String
  public let value: Expression<Int>

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(value)
  }

  init(
    name: String,
    value: Expression<Int>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension IntegerVariable: Equatable {
  public static func ==(lhs: IntegerVariable, rhs: IntegerVariable) -> Bool {
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

extension IntegerVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
