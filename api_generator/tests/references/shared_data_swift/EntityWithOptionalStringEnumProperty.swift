// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithOptionalStringEnumProperty: Sendable {
  @frozen
  public enum Property: String, CaseIterable, Sendable {
    case first = "first"
    case second = "second"
  }

  public static let type: String = "entity_with_optional_string_enum_property"
  public let property: Expression<Property>?

  public func resolveProperty(_ resolver: ExpressionResolver) -> Property? {
    resolver.resolveEnum(property)
  }

  init(
    property: Expression<Property>? = nil
  ) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithOptionalStringEnumProperty: Equatable {
  public static func ==(lhs: EntityWithOptionalStringEnumProperty, rhs: EntityWithOptionalStringEnumProperty) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif
