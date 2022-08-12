// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithEntityProperty {
  public static let type: String = "entity_with_entity_property"
  public let entity: Entity // default value: .entityWithStringEnumProperty(EntityWithStringEnumProperty(property: .second))

  static let entityValidator: AnyValueValidator<Entity> =
    makeNoOpValueValidator()

  init(entity: Entity? = nil) {
    self
      .entity = entity ??
      .entityWithStringEnumProperty(EntityWithStringEnumProperty(property: .second))
  }
}

#if DEBUG
extension EntityWithEntityProperty: Equatable {
  public static func ==(lhs: EntityWithEntityProperty, rhs: EntityWithEntityProperty) -> Bool {
    guard
      lhs.entity == rhs.entity
    else {
      return false
    }
    return true
  }
}
#endif
