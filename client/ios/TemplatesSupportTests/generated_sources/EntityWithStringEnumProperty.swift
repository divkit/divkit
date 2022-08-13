// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithStringEnumProperty {
  public enum Property: String, CaseIterable {
    case first
    case second
  }

  public static let type: String = "entity_with_string_enum_property"
  public let property: Property

  init(property: Property) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithStringEnumProperty: Equatable {
  public static func ==(
    lhs: EntityWithStringEnumProperty,
    rhs: EntityWithStringEnumProperty
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
