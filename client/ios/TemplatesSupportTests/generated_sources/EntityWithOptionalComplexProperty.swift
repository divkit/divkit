// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

public final class EntityWithOptionalComplexProperty {
  public final class Property {
    public let value: Expression<URL>

    public func resolveValue(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveStringBasedValue(expression: value, initializer: URL.init(string:))
    }

    init(
      value: Expression<URL>
    ) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_optional_complex_property"
  public let property: Property?

  static let propertyValidator: AnyValueValidator<EntityWithOptionalComplexProperty.Property> =
    makeNoOpValueValidator()

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

extension EntityWithOptionalComplexProperty: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["property"] = property?.toDictionary()
    return result
  }
}

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

extension EntityWithOptionalComplexProperty.Property: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
