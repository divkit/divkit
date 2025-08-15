// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionClearFocus: Sendable {
  public static let type: String = "clear_focus"

  init() {}
}

#if DEBUG
extension DivActionClearFocus: Equatable {
  public static func ==(lhs: DivActionClearFocus, rhs: DivActionClearFocus) -> Bool {
    return true
  }
}
#endif

extension DivActionClearFocus: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
