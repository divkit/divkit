// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithComplexPropertyWithDefaultValue {
  public final class Property {
    public let value: Expression<String>

    public func resolveValue(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: value, initializer: { $0 })
    }

    init(
      value: Expression<String>
    ) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_complex_property_with_default_value"
  public let property: Property // default value: EntityWithComplexPropertyWithDefaultValue.Property(value: .value("Default text"))

  static let propertyValidator: AnyValueValidator<EntityWithComplexPropertyWithDefaultValue.Property> =
    makeNoOpValueValidator()

  init(
    property: Property? = nil
  ) {
    self.property = property ?? EntityWithComplexPropertyWithDefaultValue.Property(value: .value("Default text"))
  }
}

#if DEBUG
extension EntityWithComplexPropertyWithDefaultValue: Equatable {
  public static func ==(lhs: EntityWithComplexPropertyWithDefaultValue, rhs: EntityWithComplexPropertyWithDefaultValue) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithComplexPropertyWithDefaultValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["property"] = property.toDictionary()
    return result
  }
}

#if DEBUG
extension EntityWithComplexPropertyWithDefaultValue.Property: Equatable {
  public static func ==(lhs: EntityWithComplexPropertyWithDefaultValue.Property, rhs: EntityWithComplexPropertyWithDefaultValue.Property) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithComplexPropertyWithDefaultValue.Property: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
