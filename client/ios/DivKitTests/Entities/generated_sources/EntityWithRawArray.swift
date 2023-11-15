// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithRawArray {
  public static let type: String = "entity_with_raw_array"
  public let array: [Any]

  init(
    array: [Any]
  ) {
    self.array = array
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [Any] in class fields
extension EntityWithRawArray: Equatable {
  public static func ==(lhs: EntityWithRawArray, rhs: EntityWithRawArray) -> Bool {
    return true
  }
}
#endif

extension EntityWithRawArray: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["array"] = array
    return result
  }
}
