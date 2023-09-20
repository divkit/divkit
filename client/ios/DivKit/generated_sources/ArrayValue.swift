// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class ArrayValue {
  public static let type: String = "array"
  public let value: [Any]

  init(
    value: [Any]
  ) {
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [Any] in class fields
extension ArrayValue: Equatable {
  public static func ==(lhs: ArrayValue, rhs: ArrayValue) -> Bool {
    return true
  }
}
#endif

extension ArrayValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value
    return result
  }
}
