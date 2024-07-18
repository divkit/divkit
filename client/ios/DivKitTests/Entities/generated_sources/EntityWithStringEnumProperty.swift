// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class EntityWithStringEnumProperty {
  @frozen
  public enum Property: String, CaseIterable {
    case first = "first"
    case second = "second"
  }

  public static let type: String = "entity_with_string_enum_property"
  public let property: Expression<Property>

  public func resolveProperty(_ resolver: ExpressionResolver) -> Property? {
    resolver.resolveEnum(property)
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

extension EntityWithStringEnumProperty: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["property"] = property.toValidSerializationValue()
    return result
  }
}
