// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionAnimatorStop {
  public static let type: String = "animator_stop"
  public let animatorId: Expression<String>

  public func resolveAnimatorId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(animatorId)
  }

  init(
    animatorId: Expression<String>
  ) {
    self.animatorId = animatorId
  }
}

#if DEBUG
extension DivActionAnimatorStop: Equatable {
  public static func ==(lhs: DivActionAnimatorStop, rhs: DivActionAnimatorStop) -> Bool {
    guard
      lhs.animatorId == rhs.animatorId
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionAnimatorStop: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["animator_id"] = animatorId.toValidSerializationValue()
    return result
  }
}
