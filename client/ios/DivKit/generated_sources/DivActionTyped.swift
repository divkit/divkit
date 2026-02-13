// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionTyped: Sendable {
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
  case divActionScrollBy(DivActionScrollBy)
  case divActionScrollTo(DivActionScrollTo)
  case divActionSetState(DivActionSetState)
  case divActionSetStoredValue(DivActionSetStoredValue)
  case divActionSetVariable(DivActionSetVariable)
  case divActionShowTooltip(DivActionShowTooltip)
  case divActionSubmit(DivActionSubmit)
  case divActionTimer(DivActionTimer)
  case divActionUpdateStructure(DivActionUpdateStructure)
  case divActionVideo(DivActionVideo)
  case divActionCustom(DivActionCustom)

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
    case let .divActionScrollBy(value):
      return value
    case let .divActionScrollTo(value):
      return value
    case let .divActionSetState(value):
      return value
    case let .divActionSetStoredValue(value):
      return value
    case let .divActionSetVariable(value):
      return value
    case let .divActionShowTooltip(value):
      return value
    case let .divActionSubmit(value):
      return value
    case let .divActionTimer(value):
      return value
    case let .divActionUpdateStructure(value):
      return value
    case let .divActionVideo(value):
      return value
    case let .divActionCustom(value):
      return value
    }
  }
}

extension DivActionTyped {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivActionAnimatorStart.type:
      self = .divActionAnimatorStart(try DivActionAnimatorStart(dictionary: dictionary, context: context))
    case DivActionAnimatorStop.type:
      self = .divActionAnimatorStop(try DivActionAnimatorStop(dictionary: dictionary, context: context))
    case DivActionArrayInsertValue.type:
      self = .divActionArrayInsertValue(try DivActionArrayInsertValue(dictionary: dictionary, context: context))
    case DivActionArrayRemoveValue.type:
      self = .divActionArrayRemoveValue(try DivActionArrayRemoveValue(dictionary: dictionary, context: context))
    case DivActionArraySetValue.type:
      self = .divActionArraySetValue(try DivActionArraySetValue(dictionary: dictionary, context: context))
    case DivActionClearFocus.type:
      self = .divActionClearFocus(try DivActionClearFocus(dictionary: dictionary, context: context))
    case DivActionCopyToClipboard.type:
      self = .divActionCopyToClipboard(try DivActionCopyToClipboard(dictionary: dictionary, context: context))
    case DivActionDictSetValue.type:
      self = .divActionDictSetValue(try DivActionDictSetValue(dictionary: dictionary, context: context))
    case DivActionDownload.type:
      self = .divActionDownload(try DivActionDownload(dictionary: dictionary, context: context))
    case DivActionFocusElement.type:
      self = .divActionFocusElement(try DivActionFocusElement(dictionary: dictionary, context: context))
    case DivActionHideTooltip.type:
      self = .divActionHideTooltip(try DivActionHideTooltip(dictionary: dictionary, context: context))
    case DivActionScrollBy.type:
      self = .divActionScrollBy(try DivActionScrollBy(dictionary: dictionary, context: context))
    case DivActionScrollTo.type:
      self = .divActionScrollTo(try DivActionScrollTo(dictionary: dictionary, context: context))
    case DivActionSetState.type:
      self = .divActionSetState(try DivActionSetState(dictionary: dictionary, context: context))
    case DivActionSetStoredValue.type:
      self = .divActionSetStoredValue(try DivActionSetStoredValue(dictionary: dictionary, context: context))
    case DivActionSetVariable.type:
      self = .divActionSetVariable(try DivActionSetVariable(dictionary: dictionary, context: context))
    case DivActionShowTooltip.type:
      self = .divActionShowTooltip(try DivActionShowTooltip(dictionary: dictionary, context: context))
    case DivActionSubmit.type:
      self = .divActionSubmit(try DivActionSubmit(dictionary: dictionary, context: context))
    case DivActionTimer.type:
      self = .divActionTimer(try DivActionTimer(dictionary: dictionary, context: context))
    case DivActionUpdateStructure.type:
      self = .divActionUpdateStructure(try DivActionUpdateStructure(dictionary: dictionary, context: context))
    case DivActionVideo.type:
      self = .divActionVideo(try DivActionVideo(dictionary: dictionary, context: context))
    case DivActionCustom.type:
      self = .divActionCustom(try DivActionCustom(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-typed", representation: dictionary)
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
    case let (.divActionScrollBy(l), .divActionScrollBy(r)):
      return l == r
    case let (.divActionScrollTo(l), .divActionScrollTo(r)):
      return l == r
    case let (.divActionSetState(l), .divActionSetState(r)):
      return l == r
    case let (.divActionSetStoredValue(l), .divActionSetStoredValue(r)):
      return l == r
    case let (.divActionSetVariable(l), .divActionSetVariable(r)):
      return l == r
    case let (.divActionShowTooltip(l), .divActionShowTooltip(r)):
      return l == r
    case let (.divActionSubmit(l), .divActionSubmit(r)):
      return l == r
    case let (.divActionTimer(l), .divActionTimer(r)):
      return l == r
    case let (.divActionUpdateStructure(l), .divActionUpdateStructure(r)):
      return l == r
    case let (.divActionVideo(l), .divActionVideo(r)):
      return l == r
    case let (.divActionCustom(l), .divActionCustom(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivActionTyped: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
