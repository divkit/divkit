// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithComplexProperty: Sendable {
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

  public static let type: String = "entity_with_complex_property"
  public let property: Property

  init(
    property: Property
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

extension EntityWithComplexProperty: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["property"] = property.toDictionary()
    return result
  }
}

#if DEBUG
extension EntityWithComplexProperty.Property: Equatable {
  public static func ==(lhs: EntityWithComplexProperty.Property, rhs: EntityWithComplexProperty.Property) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithComplexProperty.Property: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
