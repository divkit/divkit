// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithOptionalComplexProperty: Sendable {
  public final class Property: Sendable {
    public let value: Expression<URL>

    public func resolveValue(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveUrl(value)
    }

    init(
      value: Expression<URL>
    ) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_optional_complex_property"
  public let property: Property?

  init(
    property: Property? = nil
  ) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithOptionalComplexProperty: Equatable {
  public static func ==(lhs: EntityWithOptionalComplexProperty, rhs: EntityWithOptionalComplexProperty) -> Bool {
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
  public static func ==(lhs: EntityWithOptionalComplexProperty.Property, rhs: EntityWithOptionalComplexProperty.Property) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif
