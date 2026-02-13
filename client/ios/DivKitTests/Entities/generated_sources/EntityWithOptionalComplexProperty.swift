// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithOptionalComplexProperty: Sendable {
  public final class Property: Sendable {
    public let value: Expression<URL>

    public func resolveValue(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveUrl(value)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        value: try dictionary.getExpressionField("value", transform: URL.makeFromNonEncodedString, context: context)
      )
    }

    init(
      value: Expression<URL>
    ) {
      self.value = value
    }
  }

  public static let type: String = "entity_with_optional_complex_property"
  public let property: Property?

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      property: try dictionary.getOptionalField("property", transform: { (dict: [String: Any]) in try EntityWithOptionalComplexProperty.Property(dictionary: dict, context: context) })
    )
  }

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
  @_optimize(size)
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
