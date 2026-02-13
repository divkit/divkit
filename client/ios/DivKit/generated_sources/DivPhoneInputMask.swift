// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPhoneInputMask: DivInputMaskBase, Sendable {
  public static let type: String = "phone"
  public let rawTextVariable: String

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      rawTextVariable: try dictionary.getField("raw_text_variable", context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["raw_text_variable"] = rawTextVariable
    return result
  }
}
