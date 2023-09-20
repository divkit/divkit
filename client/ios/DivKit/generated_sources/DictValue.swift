// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DictValue {
  public static let type: String = "dict"
  public let value: [String: Any]

  init(
    value: [String: Any]
  ) {
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DictValue: Equatable {
  public static func ==(lhs: DictValue, rhs: DictValue) -> Bool {
    return true
  }
}
#endif

extension DictValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value
    return result
  }
}
