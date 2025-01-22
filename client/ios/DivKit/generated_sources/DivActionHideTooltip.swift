// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionHideTooltip: Sendable {
  public static let type: String = "hide_tooltip"
  public let id: Expression<String>

  public func resolveId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(id)
  }

  init(
    id: Expression<String>
  ) {
    self.id = id
  }
}

#if DEBUG
extension DivActionHideTooltip: Equatable {
  public static func ==(lhs: DivActionHideTooltip, rhs: DivActionHideTooltip) -> Bool {
    guard
      lhs.id == rhs.id
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionHideTooltip: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["id"] = id.toValidSerializationValue()
    return result
  }
}
