// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetState {
  public static let type: String = "set_state"
  public let stateId: Expression<String>
  public let temporary: Expression<Bool> // default value: true

  public func resolveStateId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(stateId)
  }

  public func resolveTemporary(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(temporary) ?? true
  }

  init(
    stateId: Expression<String>,
    temporary: Expression<Bool>? = nil
  ) {
    self.stateId = stateId
    self.temporary = temporary ?? .value(true)
  }
}

#if DEBUG
extension DivActionSetState: Equatable {
  public static func ==(lhs: DivActionSetState, rhs: DivActionSetState) -> Bool {
    guard
      lhs.stateId == rhs.stateId,
      lhs.temporary == rhs.temporary
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSetState: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["state_id"] = stateId.toValidSerializationValue()
    result["temporary"] = temporary.toValidSerializationValue()
    return result
  }
}
