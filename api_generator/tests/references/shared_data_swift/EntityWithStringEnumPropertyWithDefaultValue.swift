// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStringEnumPropertyWithDefaultValue {
  @frozen
  public enum Value: String, CaseIterable {
    case first = "first"
    case second = "second"
    case third = "third"
  }

  public static let type: String = "entity_with_string_enum_property_with_default_value"
  public let value: Expression<Value> // default value: second

  public func resolveValue(_ resolver: ExpressionResolver) -> Value {
    resolver.resolveStringBasedValue(expression: value, initializer: Value.init(rawValue:)) ?? Value.second
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
