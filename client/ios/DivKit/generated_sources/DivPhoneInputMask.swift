// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPhoneInputMask: DivInputMaskBase {
  public static let type: String = "phone"
  public let rawTextVariable: String // at least 1 char

  static let rawTextVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    rawTextVariable: String
  ) {
    self.rawTextVariable = rawTextVariable
  }
}

#if DEBUG
extension DivPhoneInputMask: Equatable {
  public static func ==(lhs: DivPhoneInputMask, rhs: DivPhoneInputMask) -> Bool {
    guard
      lhs.rawTextVariable == rhs.rawTextVariable
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPhoneInputMask: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["raw_text_variable"] = rawTextVariable
    return result
  }
}
