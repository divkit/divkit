// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithOptionalComplexProperty {
  public final class Property {
    public let value: URL

    init(value: URL) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_optional_complex_property"
  public let property: Property?

  static let propertyValidator: AnyValueValidator<EntityWithOptionalComplexProperty.Property> =
    makeNoOpValueValidator()

  init(property: Property? = nil) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithOptionalComplexProperty: Equatable {
  public static func ==(
    lhs: EntityWithOptionalComplexProperty,
    rhs: EntityWithOptionalComplexProperty
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

#if DEBUG
extension EntityWithOptionalComplexProperty.Property: Equatable {
  public static func ==(
    lhs: EntityWithOptionalComplexProperty.Property,
    rhs: EntityWithOptionalComplexProperty.Property
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
