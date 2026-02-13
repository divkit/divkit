// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipModeModal: Sendable {
  public static let type: String = "modal"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
