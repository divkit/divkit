// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DictVariable: @unchecked Sendable {
  public static let type: String = "dict"
  public let name: String
  public let value: [String: Any]

  init(
    name: String,
    value: [String: Any]
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DictVariable: Equatable {
  public static func ==(lhs: DictVariable, rhs: DictVariable) -> Bool {
    guard
      lhs.name == rhs.name
    else {
      return false
    }
    return true
  }
}
#endif

extension DictVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value
    return result
  }
}
