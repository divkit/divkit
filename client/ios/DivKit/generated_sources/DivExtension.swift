// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivExtension {
  public let id: String // at least 1 char
  public let params: [String: Any]?

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let paramsValidator: AnyValueValidator<[String: Any]> =
    makeNoOpValueValidator()

  init(
    id: String,
    params: [String: Any]? = nil
  ) {
    self.id = id
    self.params = params
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivExtension: Equatable {
  public static func ==(lhs: DivExtension, rhs: DivExtension) -> Bool {
    guard
      lhs.id == rhs.id
    else {
      return false
    }
    return true
  }
}
#endif

extension DivExtension: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["id"] = id
    result["params"] = params
    return result
  }
}
