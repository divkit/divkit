// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithJsonProperty {
  public static let type: String = "entity_with_json_property"
  public let jsonProperty: [String: Any] // default value: { "key": "value", "items": [ "value" ] }

  init(
    jsonProperty: [String: Any]? = nil
  ) {
    self.jsonProperty = jsonProperty ?? (try! JSONSerialization.jsonObject(jsonString: """
    {
        "key": "value",
        "items": [
            "value"
        ]
    }
    """) as! [String: Any])
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

extension EntityWithJsonProperty: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["json_property"] = jsonProperty
    return result
  }
}
