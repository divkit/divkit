// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithComplexProperty {
  public final class Property {
    public let value: URL

    init(value: URL) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_complex_property"
  public let property: Property

  init(property: Property) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithComplexProperty: Equatable {
  public static func ==(lhs: EntityWithComplexProperty, rhs: EntityWithComplexProperty) -> Bool {
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
extension EntityWithComplexProperty.Property: Equatable {
  public static func ==(
    lhs: EntityWithComplexProperty.Property,
    rhs: EntityWithComplexProperty.Property
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
