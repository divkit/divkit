// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithComplexPropertyWithDefaultValue {
  public final class Property {
    public let value: String

    init(value: String) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_complex_property_with_default_value"
  public let property: Property // default value: EntityWithComplexPropertyWithDefaultValue.Property(value: "Default text")

  static let propertyValidator: AnyValueValidator<
    EntityWithComplexPropertyWithDefaultValue
      .Property
  > =
    makeNoOpValueValidator()

  init(property: Property? = nil) {
    self.property = property ?? EntityWithComplexPropertyWithDefaultValue
      .Property(value: "Default text")
  }
}

#if DEBUG
extension EntityWithComplexPropertyWithDefaultValue: Equatable {
  public static func ==(
    lhs: EntityWithComplexPropertyWithDefaultValue,
    rhs: EntityWithComplexPropertyWithDefaultValue
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
extension EntityWithComplexPropertyWithDefaultValue.Property: Equatable {
  public static func ==(
    lhs: EntityWithComplexPropertyWithDefaultValue.Property,
    rhs: EntityWithComplexPropertyWithDefaultValue.Property
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
