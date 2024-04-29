// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithArrayOfNestedItems {
  public final class Item {
    public let entity: Entity
    public let property: Expression<String>

    public func resolveProperty(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(property)
    }

    init(
      entity: Entity,
      property: Expression<String>
    ) {
      self.entity = entity
      self.property = property
    }
  }

  public static let type: String = "entity_with_array_of_nested_items"
  public let items: [Item] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<EntityWithArrayOfNestedItems.Item> =
    makeArrayValidator(minItems: 1)

  init(
    items: [Item]
  ) {
    self.items = items
  }
}

#if DEBUG
extension EntityWithArrayOfNestedItems: Equatable {
  public static func ==(lhs: EntityWithArrayOfNestedItems, rhs: EntityWithArrayOfNestedItems) -> Bool {
    guard
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension EntityWithArrayOfNestedItems.Item: Equatable {
  public static func ==(lhs: EntityWithArrayOfNestedItems.Item, rhs: EntityWithArrayOfNestedItems.Item) -> Bool {
    guard
      lhs.entity == rhs.entity,
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif
