// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithEntityProperty: Sendable {
  public static let type: String = "entity_with_entity_property"
  public let entity: Entity // default value: .entityWithStringEnumProperty(EntityWithStringEnumProperty(property: .value(.second)))

  init(
    entity: Entity? = nil
  ) {
    self.entity = entity ?? .entityWithStringEnumProperty(EntityWithStringEnumProperty(property: .value(.second)))
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

extension EntityWithEntityProperty: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["entity"] = entity.toDictionary()
    return result
  }
}
