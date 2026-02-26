// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithArrayOfNestedItems: Sendable {
  public final class Item: Sendable {
    public let entity: Entity
    public let property: Expression<String>

    public func resolveProperty(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(property)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        entity: try dictionary.getField("entity", transform: { (dict: [String: Any]) in try Entity(dictionary: dict, context: context) }, context: context),
        property: try dictionary.getExpressionField("property", context: context)
      )
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

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      items: try dictionary.getArray("items", transform: { (dict: [String: Any]) in try? EntityWithArrayOfNestedItems.Item(dictionary: dict, context: context) }, validator: Self.itemsValidator, context: context)
    )
  }

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

extension EntityWithArrayOfNestedItems: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toDictionary() }
    return result
  }
}

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

extension EntityWithArrayOfNestedItems.Item: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["entity"] = entity.toDictionary()
    result["property"] = property.toValidSerializationValue()
    return result
  }
}
