// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public enum DivChangeTransition {
  case divChangeSetTransition(DivChangeSetTransition)
  case divChangeBoundsTransition(DivChangeBoundsTransition)

  public var value: Serializable {
    switch self {
    case let .divChangeSetTransition(value):
      return value
    case let .divChangeBoundsTransition(value):
      return value
    }
  }
}

#if DEBUG
extension DivChangeTransition: Equatable {
  public static func ==(lhs: DivChangeTransition, rhs: DivChangeTransition) -> Bool {
    switch (lhs, rhs) {
    case let (.divChangeSetTransition(l), .divChangeSetTransition(r)):
      return l == r
    case let (.divChangeBoundsTransition(l), .divChangeBoundsTransition(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivChangeTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
