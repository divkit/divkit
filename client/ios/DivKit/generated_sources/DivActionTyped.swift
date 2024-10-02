// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionTyped {
  case divActionAnimatorStart(DivActionAnimatorStart)
  case divActionAnimatorStop(DivActionAnimatorStop)
  case divActionArrayInsertValue(DivActionArrayInsertValue)
  case divActionArrayRemoveValue(DivActionArrayRemoveValue)
  case divActionArraySetValue(DivActionArraySetValue)
  case divActionClearFocus(DivActionClearFocus)
  case divActionCopyToClipboard(DivActionCopyToClipboard)
  case divActionDictSetValue(DivActionDictSetValue)
  case divActionDownload(DivActionDownload)
  case divActionFocusElement(DivActionFocusElement)
  case divActionHideTooltip(DivActionHideTooltip)
  case divActionSetState(DivActionSetState)
  case divActionSetVariable(DivActionSetVariable)
  case divActionShowTooltip(DivActionShowTooltip)
  case divActionSubmit(DivActionSubmit)
  case divActionTimer(DivActionTimer)
  case divActionVideo(DivActionVideo)

  public var value: Serializable {
    switch self {
    case let .divActionAnimatorStart(value):
      return value
    case let .divActionAnimatorStop(value):
      return value
    case let .divActionArrayInsertValue(value):
      return value
    case let .divActionArrayRemoveValue(value):
      return value
    case let .divActionArraySetValue(value):
      return value
    case let .divActionClearFocus(value):
      return value
    case let .divActionCopyToClipboard(value):
      return value
    case let .divActionDictSetValue(value):
      return value
    case let .divActionDownload(value):
      return value
    case let .divActionFocusElement(value):
      return value
    case let .divActionHideTooltip(value):
      return value
    case let .divActionSetState(value):
      return value
    case let .divActionSetVariable(value):
      return value
    case let .divActionShowTooltip(value):
      return value
    case let .divActionSubmit(value):
      return value
    case let .divActionTimer(value):
      return value
    case let .divActionVideo(value):
      return value
    }
  }
}

#if DEBUG
extension DivActionTyped: Equatable {
  public static func ==(lhs: DivActionTyped, rhs: DivActionTyped) -> Bool {
    switch (lhs, rhs) {
    case let (.divActionAnimatorStart(l), .divActionAnimatorStart(r)):
      return l == r
    case let (.divActionAnimatorStop(l), .divActionAnimatorStop(r)):
      return l == r
    case let (.divActionArrayInsertValue(l), .divActionArrayInsertValue(r)):
      return l == r
    case let (.divActionArrayRemoveValue(l), .divActionArrayRemoveValue(r)):
      return l == r
    case let (.divActionArraySetValue(l), .divActionArraySetValue(r)):
      return l == r
    case let (.divActionClearFocus(l), .divActionClearFocus(r)):
      return l == r
    case let (.divActionCopyToClipboard(l), .divActionCopyToClipboard(r)):
      return l == r
    case let (.divActionDictSetValue(l), .divActionDictSetValue(r)):
      return l == r
    case let (.divActionDownload(l), .divActionDownload(r)):
      return l == r
    case let (.divActionFocusElement(l), .divActionFocusElement(r)):
      return l == r
    case let (.divActionHideTooltip(l), .divActionHideTooltip(r)):
      return l == r
    case let (.divActionSetState(l), .divActionSetState(r)):
      return l == r
    case let (.divActionSetVariable(l), .divActionSetVariable(r)):
      return l == r
    case let (.divActionShowTooltip(l), .divActionShowTooltip(r)):
      return l == r
    case let (.divActionSubmit(l), .divActionSubmit(r)):
      return l == r
    case let (.divActionTimer(l), .divActionTimer(r)):
      return l == r
    case let (.divActionVideo(l), .divActionVideo(r)):
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
