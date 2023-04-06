// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class BooleanVariable {
  public static let type: String = "boolean"
  public let name: String // at least 1 char
  public let value: Bool

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    name: String,
    value: Bool
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension BooleanVariable: Equatable {
  public static func ==(lhs: BooleanVariable, rhs: BooleanVariable) -> Bool {
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

extension BooleanVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value
    return result
  }
}
