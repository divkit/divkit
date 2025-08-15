// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStringEnumPropertyWithDefaultValue: Sendable {
  @frozen
  public enum Value: String, CaseIterable, Sendable {
    case first = "first"
    case second = "second"
    case third = "third"
  }

  public static let type: String = "entity_with_string_enum_property_with_default_value"
  public let value: Expression<Value> // default value: second

  public func resolveValue(_ resolver: ExpressionResolver) -> Value {
    resolver.resolveEnum(value) ?? Value.second
  }

  init(
    value: Expression<Value>? = nil
  ) {
    self.value = value ?? .value(.second)
  }
}

#if DEBUG
extension EntityWithStringEnumPropertyWithDefaultValue: Equatable {
  public static func ==(lhs: EntityWithStringEnumPropertyWithDefaultValue, rhs: EntityWithStringEnumPropertyWithDefaultValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithStringEnumPropertyWithDefaultValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
