// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class BooleanVariable: Sendable {
  public static let type: String = "boolean"
  public let name: String
  public let value: Expression<Bool>

  public func resolveValue(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(value)
  }

  init(
    name: String,
    value: Expression<Bool>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension BooleanVariable: Equatable {
  public static func ==(lhs: BooleanVariable, rhs: BooleanVariable) -> Bool {
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

extension BooleanVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
