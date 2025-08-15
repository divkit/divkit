// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithoutProperties: Sendable {
  public static let type: String = "entity_without_properties"

  init() {}
}

#if DEBUG
extension EntityWithoutProperties: Equatable {
  public static func ==(lhs: EntityWithoutProperties, rhs: EntityWithoutProperties) -> Bool {
    return true
  }
}
#endif

extension EntityWithoutProperties: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
