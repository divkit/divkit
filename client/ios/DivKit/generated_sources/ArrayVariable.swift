// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class ArrayVariable {
  public static let type: String = "array"
  public let name: String // at least 1 char
  public let value: [Any]

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

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
