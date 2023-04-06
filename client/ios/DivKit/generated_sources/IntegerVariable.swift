// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class IntegerVariable {
  public static let type: String = "integer"
  public let name: String // at least 1 char
  public let value: Int

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    name: String,
    value: Int
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension IntegerVariable: Equatable {
  public static func ==(lhs: IntegerVariable, rhs: IntegerVariable) -> Bool {
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

extension IntegerVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value
    return result
  }
}
