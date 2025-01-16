// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class ArrayVariable: @unchecked Sendable {
  public static let type: String = "array"
  public let name: String
  public let value: [Any]

  init(
    name: String,
    value: [Any]
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension ArrayVariable: Equatable {
  public static func ==(lhs: ArrayVariable, rhs: ArrayVariable) -> Bool {
    guard
      lhs.name == rhs.name
    else {
      return false
    }
    return true
  }
}
#endif

extension ArrayVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value
    return result
  }
}
