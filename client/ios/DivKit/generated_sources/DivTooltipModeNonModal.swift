// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipModeNonModal: Sendable {
  public static let type: String = "non_modal"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
