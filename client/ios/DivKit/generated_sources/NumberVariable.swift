// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class NumberVariable {
  public static let type: String = "number"
  public let name: String // at least 1 char
  public let value: Double

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    name: String,
    value: Double
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension NumberVariable: Equatable {
  public static func ==(lhs: NumberVariable, rhs: NumberVariable) -> Bool {
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

extension NumberVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value
    return result
  }
}
