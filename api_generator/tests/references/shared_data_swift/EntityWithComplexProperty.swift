// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithComplexProperty: Sendable {
  public final class ComplexProperty: Sendable {
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

  public static let type: String = "entity_with_complex_property"
  public let property: ComplexProperty

  init(
    property: ComplexProperty
  ) {
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
extension EntityWithComplexProperty.ComplexProperty: Equatable {
  public static func ==(lhs: EntityWithComplexProperty.ComplexProperty, rhs: EntityWithComplexProperty.ComplexProperty) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif
