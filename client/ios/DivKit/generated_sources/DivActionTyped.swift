// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivActionTyped {
  case divActionArrayInsertValue(DivActionArrayInsertValue)
  case divActionArrayRemoveValue(DivActionArrayRemoveValue)
  case divActionSetVariable(DivActionSetVariable)
  case divActionFocusElement(DivActionFocusElement)
  case divActionClearFocus(DivActionClearFocus)
  case divActionCopyToClipboard(DivActionCopyToClipboard)

  public var value: Serializable {
    switch self {
    case let .divActionArrayInsertValue(value):
      return value
    case let .divActionArrayRemoveValue(value):
      return value
    case let .divActionSetVariable(value):
      return value
    case let .divActionFocusElement(value):
      return value
    case let .divActionClearFocus(value):
      return value
    case let .divActionCopyToClipboard(value):
      return value
    }
  }
}

#if DEBUG
extension DivActionTyped: Equatable {
  public static func ==(lhs: DivActionTyped, rhs: DivActionTyped) -> Bool {
    switch (lhs, rhs) {
    case let (.divActionArrayInsertValue(l), .divActionArrayInsertValue(r)):
      return l == r
    case let (.divActionArrayRemoveValue(l), .divActionArrayRemoveValue(r)):
      return l == r
    case let (.divActionSetVariable(l), .divActionSetVariable(r)):
      return l == r
    case let (.divActionFocusElement(l), .divActionFocusElement(r)):
      return l == r
    case let (.divActionClearFocus(l), .divActionClearFocus(r)):
      return l == r
    case let (.divActionCopyToClipboard(l), .divActionCopyToClipboard(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivActionTyped: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
