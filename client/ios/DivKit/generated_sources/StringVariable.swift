// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class StringVariable: Sendable {
  public static let type: String = "string"
  public let name: String
  public let value: Expression<String>

  public func resolveValue(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(value)
  }

  init(
    name: String,
    value: Expression<String>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension StringVariable: Equatable {
  public static func ==(lhs: StringVariable, rhs: StringVariable) -> Bool {
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

extension StringVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
