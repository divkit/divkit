// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithOptionalProperty: Sendable {
  public static let type: String = "entity_with_optional_property"
  public let property: Expression<String>?

  public func resolveProperty(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(property)
  }

  init(
    property: Expression<String>? = nil
  ) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithOptionalProperty: Equatable {
  public static func ==(lhs: EntityWithOptionalProperty, rhs: EntityWithOptionalProperty) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithOptionalProperty: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["property"] = property?.toValidSerializationValue()
    return result
  }
}
