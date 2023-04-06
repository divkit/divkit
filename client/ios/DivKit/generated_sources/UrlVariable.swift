// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class UrlVariable {
  public static let type: String = "url"
  public let name: String // at least 1 char
  public let value: URL

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    name: String,
    value: URL
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension UrlVariable: Equatable {
  public static func ==(lhs: UrlVariable, rhs: UrlVariable) -> Bool {
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

extension UrlVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.absoluteString
    return result
  }
}
