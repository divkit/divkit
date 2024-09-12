// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionVideo {
  @frozen
  public enum Action: String, CaseIterable {
    case start = "start"
    case pause = "pause"
  }

  public static let type: String = "video"
  public let action: Expression<Action>
  public let id: Expression<String>

  public func resolveAction(_ resolver: ExpressionResolver) -> Action? {
    resolver.resolveEnum(action)
  }

  public func resolveId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(id)
  }

  init(
    action: Expression<Action>,
    id: Expression<String>
  ) {
    self.action = action
    self.id = id
  }
}

#if DEBUG
extension DivActionVideo: Equatable {
  public static func ==(lhs: DivActionVideo, rhs: DivActionVideo) -> Bool {
    guard
      lhs.action == rhs.action,
      lhs.id == rhs.id
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionVideo: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["action"] = action.toValidSerializationValue()
    result["id"] = id.toValidSerializationValue()
    return result
  }
}
