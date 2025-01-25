// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionTypedTemplate: TemplateValue {
  case divActionAnimatorStartTemplate(DivActionAnimatorStartTemplate)
  case divActionAnimatorStopTemplate(DivActionAnimatorStopTemplate)
  case divActionArrayInsertValueTemplate(DivActionArrayInsertValueTemplate)
  case divActionArrayRemoveValueTemplate(DivActionArrayRemoveValueTemplate)
  case divActionArraySetValueTemplate(DivActionArraySetValueTemplate)
  case divActionClearFocusTemplate(DivActionClearFocusTemplate)
  case divActionCopyToClipboardTemplate(DivActionCopyToClipboardTemplate)
  case divActionDictSetValueTemplate(DivActionDictSetValueTemplate)
  case divActionDownloadTemplate(DivActionDownloadTemplate)
  case divActionFocusElementTemplate(DivActionFocusElementTemplate)
  case divActionHideTooltipTemplate(DivActionHideTooltipTemplate)
  case divActionScrollByTemplate(DivActionScrollByTemplate)
  case divActionScrollToTemplate(DivActionScrollToTemplate)
  case divActionSetStateTemplate(DivActionSetStateTemplate)
  case divActionSetStoredValueTemplate(DivActionSetStoredValueTemplate)
  case divActionSetVariableTemplate(DivActionSetVariableTemplate)
  case divActionShowTooltipTemplate(DivActionShowTooltipTemplate)
  case divActionSubmitTemplate(DivActionSubmitTemplate)
  case divActionTimerTemplate(DivActionTimerTemplate)
  case divActionVideoTemplate(DivActionVideoTemplate)

  public var value: Any {
    switch self {
    case let .divActionAnimatorStartTemplate(value):
      return value
    case let .divActionAnimatorStopTemplate(value):
      return value
    case let .divActionArrayInsertValueTemplate(value):
      return value
    case let .divActionArrayRemoveValueTemplate(value):
      return value
    case let .divActionArraySetValueTemplate(value):
      return value
    case let .divActionClearFocusTemplate(value):
      return value
    case let .divActionCopyToClipboardTemplate(value):
      return value
    case let .divActionDictSetValueTemplate(value):
      return value
    case let .divActionDownloadTemplate(value):
      return value
    case let .divActionFocusElementTemplate(value):
      return value
    case let .divActionHideTooltipTemplate(value):
      return value
    case let .divActionScrollByTemplate(value):
      return value
    case let .divActionScrollToTemplate(value):
      return value
    case let .divActionSetStateTemplate(value):
      return value
    case let .divActionSetStoredValueTemplate(value):
      return value
    case let .divActionSetVariableTemplate(value):
      return value
    case let .divActionShowTooltipTemplate(value):
      return value
    case let .divActionSubmitTemplate(value):
      return value
    case let .divActionTimerTemplate(value):
      return value
    case let .divActionVideoTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionTypedTemplate {
    switch self {
    case let .divActionAnimatorStartTemplate(value):
      return .divActionAnimatorStartTemplate(try value.resolveParent(templates: templates))
    case let .divActionAnimatorStopTemplate(value):
      return .divActionAnimatorStopTemplate(try value.resolveParent(templates: templates))
    case let .divActionArrayInsertValueTemplate(value):
      return .divActionArrayInsertValueTemplate(try value.resolveParent(templates: templates))
    case let .divActionArrayRemoveValueTemplate(value):
      return .divActionArrayRemoveValueTemplate(try value.resolveParent(templates: templates))
    case let .divActionArraySetValueTemplate(value):
      return .divActionArraySetValueTemplate(try value.resolveParent(templates: templates))
    case let .divActionClearFocusTemplate(value):
      return .divActionClearFocusTemplate(try value.resolveParent(templates: templates))
    case let .divActionCopyToClipboardTemplate(value):
      return .divActionCopyToClipboardTemplate(try value.resolveParent(templates: templates))
    case let .divActionDictSetValueTemplate(value):
      return .divActionDictSetValueTemplate(try value.resolveParent(templates: templates))
    case let .divActionDownloadTemplate(value):
      return .divActionDownloadTemplate(try value.resolveParent(templates: templates))
    case let .divActionFocusElementTemplate(value):
      return .divActionFocusElementTemplate(try value.resolveParent(templates: templates))
    case let .divActionHideTooltipTemplate(value):
      return .divActionHideTooltipTemplate(try value.resolveParent(templates: templates))
    case let .divActionScrollByTemplate(value):
      return .divActionScrollByTemplate(try value.resolveParent(templates: templates))
    case let .divActionScrollToTemplate(value):
      return .divActionScrollToTemplate(try value.resolveParent(templates: templates))
    case let .divActionSetStateTemplate(value):
      return .divActionSetStateTemplate(try value.resolveParent(templates: templates))
    case let .divActionSetStoredValueTemplate(value):
      return .divActionSetStoredValueTemplate(try value.resolveParent(templates: templates))
    case let .divActionSetVariableTemplate(value):
      return .divActionSetVariableTemplate(try value.resolveParent(templates: templates))
    case let .divActionShowTooltipTemplate(value):
      return .divActionShowTooltipTemplate(try value.resolveParent(templates: templates))
    case let .divActionSubmitTemplate(value):
      return .divActionSubmitTemplate(try value.resolveParent(templates: templates))
    case let .divActionTimerTemplate(value):
      return .divActionTimerTemplate(try value.resolveParent(templates: templates))
    case let .divActionVideoTemplate(value):
      return .divActionVideoTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionTypedTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionTyped> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivActionTyped>!
      result = result ?? {
        if case let .divActionAnimatorStartTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionAnimatorStart(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStart(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionAnimatorStopTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionAnimatorStop(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStop(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionArrayInsertValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionArrayInsertValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayInsertValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionArrayRemoveValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionArrayRemoveValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayRemoveValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionArraySetValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionArraySetValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArraySetValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionClearFocusTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionClearFocus(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionClearFocus(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionCopyToClipboardTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionCopyToClipboard(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionCopyToClipboard(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionDictSetValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionDictSetValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDictSetValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionDownloadTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionDownload(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDownload(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionFocusElementTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionFocusElement(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionFocusElement(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionHideTooltipTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionHideTooltip(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionHideTooltip(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionScrollByTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionScrollBy(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionScrollBy(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionScrollToTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionScrollTo(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionScrollTo(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionSetStateTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionSetState(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetState(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionSetStoredValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionSetStoredValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetStoredValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionSetVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionSetVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionShowTooltipTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionShowTooltip(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionShowTooltip(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionSubmitTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionSubmit(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSubmit(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionTimerTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionTimer(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionTimer(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divActionVideoTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divActionVideo(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divActionVideo(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivActionTyped> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivActionTyped>?
    result = result ?? { if type == DivActionAnimatorStart.type {
      let result = { DivActionAnimatorStartTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionAnimatorStart(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStart(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionAnimatorStop.type {
      let result = { DivActionAnimatorStopTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionAnimatorStop(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStop(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionArrayInsertValue.type {
      let result = { DivActionArrayInsertValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionArrayInsertValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayInsertValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionArrayRemoveValue.type {
      let result = { DivActionArrayRemoveValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionArrayRemoveValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayRemoveValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionArraySetValue.type {
      let result = { DivActionArraySetValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionArraySetValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArraySetValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionClearFocus.type {
      let result = { DivActionClearFocusTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionClearFocus(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionClearFocus(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionCopyToClipboard.type {
      let result = { DivActionCopyToClipboardTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionCopyToClipboard(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionCopyToClipboard(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionDictSetValue.type {
      let result = { DivActionDictSetValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionDictSetValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDictSetValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionDownload.type {
      let result = { DivActionDownloadTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionDownload(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDownload(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionFocusElement.type {
      let result = { DivActionFocusElementTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionFocusElement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionFocusElement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionHideTooltip.type {
      let result = { DivActionHideTooltipTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionHideTooltip(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionHideTooltip(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionScrollBy.type {
      let result = { DivActionScrollByTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionScrollBy(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionScrollBy(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionScrollTo.type {
      let result = { DivActionScrollToTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionScrollTo(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionScrollTo(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionSetState.type {
      let result = { DivActionSetStateTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionSetState(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetState(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionSetStoredValue.type {
      let result = { DivActionSetStoredValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionSetStoredValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetStoredValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionSetVariable.type {
      let result = { DivActionSetVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionSetVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionShowTooltip.type {
      let result = { DivActionShowTooltipTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionShowTooltip(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionShowTooltip(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionSubmit.type {
      let result = { DivActionSubmitTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionSubmit(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSubmit(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionTimer.type {
      let result = { DivActionTimerTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionTimer(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionTimer(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivActionVideo.type {
      let result = { DivActionVideoTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divActionVideo(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionVideo(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivActionTypedTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivActionAnimatorStartTemplate.type:
      self = .divActionAnimatorStartTemplate(try DivActionAnimatorStartTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionAnimatorStopTemplate.type:
      self = .divActionAnimatorStopTemplate(try DivActionAnimatorStopTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionArrayInsertValueTemplate.type:
      self = .divActionArrayInsertValueTemplate(try DivActionArrayInsertValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionArrayRemoveValueTemplate.type:
      self = .divActionArrayRemoveValueTemplate(try DivActionArrayRemoveValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionArraySetValueTemplate.type:
      self = .divActionArraySetValueTemplate(try DivActionArraySetValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionClearFocusTemplate.type:
      self = .divActionClearFocusTemplate(try DivActionClearFocusTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionCopyToClipboardTemplate.type:
      self = .divActionCopyToClipboardTemplate(try DivActionCopyToClipboardTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionDictSetValueTemplate.type:
      self = .divActionDictSetValueTemplate(try DivActionDictSetValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionDownloadTemplate.type:
      self = .divActionDownloadTemplate(try DivActionDownloadTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionFocusElementTemplate.type:
      self = .divActionFocusElementTemplate(try DivActionFocusElementTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionHideTooltipTemplate.type:
      self = .divActionHideTooltipTemplate(try DivActionHideTooltipTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionScrollByTemplate.type:
      self = .divActionScrollByTemplate(try DivActionScrollByTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionScrollToTemplate.type:
      self = .divActionScrollToTemplate(try DivActionScrollToTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionSetStateTemplate.type:
      self = .divActionSetStateTemplate(try DivActionSetStateTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionSetStoredValueTemplate.type:
      self = .divActionSetStoredValueTemplate(try DivActionSetStoredValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionSetVariableTemplate.type:
      self = .divActionSetVariableTemplate(try DivActionSetVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionShowTooltipTemplate.type:
      self = .divActionShowTooltipTemplate(try DivActionShowTooltipTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionSubmitTemplate.type:
      self = .divActionSubmitTemplate(try DivActionSubmitTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionTimerTemplate.type:
      self = .divActionTimerTemplate(try DivActionTimerTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionVideoTemplate.type:
      self = .divActionVideoTemplate(try DivActionVideoTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-typed_template", representation: dictionary)
    }
  }
}
