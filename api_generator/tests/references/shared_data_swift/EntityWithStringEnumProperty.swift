// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStringEnumProperty {
  @frozen
  public enum Property: String, CaseIterable {
    case first = "first"
    case second = "second"
  }

  public static let type: String = "entity_with_string_enum_property"
  public let property: Expression<Property>

  public func resolveProperty(_ resolver: ExpressionResolver) -> Property? {
    resolver.resolveStringBasedValue(expression: property, initializer: Property.init(rawValue:))
  }

  init(
    property: Expression<Property>
  ) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithStringEnumProperty: Equatable {
  public static func ==(lhs: EntityWithStringEnumProperty, rhs: EntityWithStringEnumProperty) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif
