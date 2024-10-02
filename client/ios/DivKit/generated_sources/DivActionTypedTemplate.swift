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
  case divActionSetStateTemplate(DivActionSetStateTemplate)
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
    case let .divActionSetStateTemplate(value):
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
    case let .divActionSetStateTemplate(value):
      return .divActionSetStateTemplate(try value.resolveParent(templates: templates))
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

    switch parent {
    case let .divActionAnimatorStartTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionAnimatorStart(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStart(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionAnimatorStopTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionAnimatorStop(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStop(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionArrayInsertValueTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionArrayInsertValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayInsertValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionArrayRemoveValueTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionArrayRemoveValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayRemoveValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionArraySetValueTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionArraySetValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArraySetValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionClearFocusTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionClearFocus(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionClearFocus(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionCopyToClipboardTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionCopyToClipboard(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionCopyToClipboard(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionDictSetValueTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionDictSetValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDictSetValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionDownloadTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionDownload(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDownload(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionFocusElementTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionFocusElement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionFocusElement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionHideTooltipTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionHideTooltip(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionHideTooltip(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionSetStateTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionSetState(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetState(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionSetVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionSetVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionShowTooltipTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionShowTooltip(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionShowTooltip(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionSubmitTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionSubmit(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSubmit(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionTimerTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionTimer(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionTimer(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divActionVideoTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionVideo(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionVideo(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivActionTyped> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivActionAnimatorStart.type:
      let result = DivActionAnimatorStartTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionAnimatorStart(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStart(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionAnimatorStop.type:
      let result = DivActionAnimatorStopTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionAnimatorStop(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionAnimatorStop(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionArrayInsertValue.type:
      let result = DivActionArrayInsertValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionArrayInsertValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayInsertValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionArrayRemoveValue.type:
      let result = DivActionArrayRemoveValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionArrayRemoveValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArrayRemoveValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionArraySetValue.type:
      let result = DivActionArraySetValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionArraySetValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionArraySetValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionClearFocus.type:
      let result = DivActionClearFocusTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionClearFocus(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionClearFocus(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionCopyToClipboard.type:
      let result = DivActionCopyToClipboardTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionCopyToClipboard(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionCopyToClipboard(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionDictSetValue.type:
      let result = DivActionDictSetValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionDictSetValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDictSetValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionDownload.type:
      let result = DivActionDownloadTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionDownload(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionDownload(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionFocusElement.type:
      let result = DivActionFocusElementTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionFocusElement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionFocusElement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionHideTooltip.type:
      let result = DivActionHideTooltipTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionHideTooltip(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionHideTooltip(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionSetState.type:
      let result = DivActionSetStateTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionSetState(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetState(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionSetVariable.type:
      let result = DivActionSetVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionSetVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSetVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionShowTooltip.type:
      let result = DivActionShowTooltipTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionShowTooltip(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionShowTooltip(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionSubmit.type:
      let result = DivActionSubmitTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionSubmit(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionSubmit(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionTimer.type:
      let result = DivActionTimerTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionTimer(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionTimer(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivActionVideo.type:
      let result = DivActionVideoTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionVideo(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionVideo(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
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
    case DivActionSetStateTemplate.type:
      self = .divActionSetStateTemplate(try DivActionSetStateTemplate(dictionary: dictionary, templateToType: templateToType))
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
