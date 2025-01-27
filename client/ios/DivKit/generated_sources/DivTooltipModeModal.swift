// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipModeModal: Sendable {
  public static let type: String = "modal"

  init() {}
}

#if DEBUG
extension DivTooltipModeModal: Equatable {
  public static func ==(lhs: DivTooltipModeModal, rhs: DivTooltipModeModal) -> Bool {
    return true
  }
}
#endif

extension DivTooltipModeModal: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
