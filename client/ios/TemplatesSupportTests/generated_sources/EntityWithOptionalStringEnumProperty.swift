// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithOptionalStringEnumProperty {
  public enum Property: String, CaseIterable {
    case first
    case second
  }

  public static let type: String = "entity_with_optional_string_enum_property"
  public let property: Property?

  static let propertyValidator: AnyValueValidator<EntityWithOptionalStringEnumProperty.Property> =
    makeNoOpValueValidator()

  init(property: Property? = nil) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithOptionalStringEnumProperty: Equatable {
  public static func ==(
    lhs: EntityWithOptionalStringEnumProperty,
    rhs: EntityWithOptionalStringEnumProperty
  ) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif
