// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithStringEnumPropertyWithDefaultValue {
  public enum Value: String, CaseIterable {
    case first
    case second
    case third
  }

  public static let type: String = "entity_with_string_enum_property_with_default_value"
  public let value: Value // default value: second

  static let valueValidator: AnyValueValidator<EntityWithStringEnumPropertyWithDefaultValue.Value> =
    makeNoOpValueValidator()

  init(value: Value? = nil) {
    self.value = value ?? .second
  }
}

#if DEBUG
extension EntityWithStringEnumPropertyWithDefaultValue: Equatable {
  public static func ==(
    lhs: EntityWithStringEnumPropertyWithDefaultValue,
    rhs: EntityWithStringEnumPropertyWithDefaultValue
  ) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif
