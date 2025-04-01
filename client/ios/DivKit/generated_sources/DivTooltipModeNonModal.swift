// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipModeNonModal: Sendable {
  public static let type: String = "non_modal"

  init() {}
}

#if DEBUG
extension DivTooltipModeNonModal: Equatable {
  public static func ==(lhs: DivTooltipModeNonModal, rhs: DivTooltipModeNonModal) -> Bool {
    return true
  }
}
#endif

extension DivTooltipModeNonModal: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
