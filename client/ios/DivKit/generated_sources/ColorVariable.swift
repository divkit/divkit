// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class ColorVariable {
  public static let type: String = "color"
  public let name: String // at least 1 char
  public let value: Color

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    name: String,
    value: Color
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension ColorVariable: Equatable {
  public static func ==(lhs: ColorVariable, rhs: ColorVariable) -> Bool {
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

extension ColorVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.hexString
    return result
  }
}
