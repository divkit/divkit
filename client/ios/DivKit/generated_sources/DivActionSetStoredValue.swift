// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetStoredValue {
  public static let type: String = "set_stored_value"
  public let lifetime: Expression<Int>
  public let name: Expression<String>
  public let value: DivTypedValue

  public func resolveLifetime(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(lifetime)
  }

  public func resolveName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(name)
  }

  init(
    lifetime: Expression<Int>,
    name: Expression<String>,
    value: DivTypedValue
  ) {
    self.lifetime = lifetime
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension DivActionSetStoredValue: Equatable {
  public static func ==(lhs: DivActionSetStoredValue, rhs: DivActionSetStoredValue) -> Bool {
    guard
      lhs.lifetime == rhs.lifetime,
      lhs.name == rhs.name,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSetStoredValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["lifetime"] = lifetime.toValidSerializationValue()
    result["name"] = name.toValidSerializationValue()
    result["value"] = value.toDictionary()
    return result
  }
}
