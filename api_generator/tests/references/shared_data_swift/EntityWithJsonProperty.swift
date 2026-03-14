// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithJsonProperty: @unchecked Sendable {
  public static let type: String = "entity_with_json_property"
  public let jsonProperty: Expression<[String: Any]> // default value: { "key": "value", "items": [ "value" ] }

  public func resolveJsonProperty(_ resolver: ExpressionResolver) -> [String: Any] {
    resolver.resolveDict(jsonProperty) ?? (try! JSONSerialization.jsonObject(jsonString: """
    {
        "key": "value",
        "items": [
            "value"
        ]
    }
    """) as! [String: Any])
  }

  init(
    jsonProperty: Expression<[String: Any]>? = nil
  ) {
    self.jsonProperty = jsonProperty ?? .value((try! JSONSerialization.jsonObject(jsonString: """
    {
        "key": "value",
        "items": [
            "value"
        ]
    }
    """) as! [String: Any]))
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension EntityWithJsonProperty: Equatable {
  public static func ==(lhs: EntityWithJsonProperty, rhs: EntityWithJsonProperty) -> Bool {
    return true
  }
}
#endif
