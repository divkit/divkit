// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class StringVariable {
  public static let type: String = "string"
  public let name: String // at least 1 char
  public let value: String

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    name: String,
    value: String
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension StringVariable: Equatable {
  public static func ==(lhs: StringVariable, rhs: StringVariable) -> Bool {
    guard
      lhs.name == rhs.name,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension StringVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value
    return result
  }
}
