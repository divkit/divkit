// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionTypedTemplate: TemplateValue {
  case divActionArrayInsertValueTemplate(DivActionArrayInsertValueTemplate)
  case divActionArrayRemoveValueTemplate(DivActionArrayRemoveValueTemplate)
  case divActionArraySetValueTemplate(DivActionArraySetValueTemplate)
  case divActionClearFocusTemplate(DivActionClearFocusTemplate)
  case divActionCopyToClipboardTemplate(DivActionCopyToClipboardTemplate)
  case divActionDictSetValueTemplate(DivActionDictSetValueTemplate)
  case divActionFocusElementTemplate(DivActionFocusElementTemplate)
  case divActionSetVariableTemplate(DivActionSetVariableTemplate)

  public var value: Any {
    switch self {
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
    case let .divActionFocusElementTemplate(value):
      return value
    case let .divActionSetVariableTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionTypedTemplate {
    switch self {
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
    case let .divActionFocusElementTemplate(value):
      return .divActionFocusElementTemplate(try value.resolveParent(templates: templates))
    case let .divActionSetVariableTemplate(value):
      return .divActionSetVariableTemplate(try value.resolveParent(templates: templates))
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
    case let .divActionFocusElementTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionFocusElement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionFocusElement(value), warnings: warnings)
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
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivActionTyped> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
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
    case DivActionFocusElement.type:
      let result = DivActionFocusElementTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divActionFocusElement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divActionFocusElement(value), warnings: warnings)
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
    case DivActionFocusElementTemplate.type:
      self = .divActionFocusElementTemplate(try DivActionFocusElementTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivActionSetVariableTemplate.type:
      self = .divActionSetVariableTemplate(try DivActionSetVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-typed_template", representation: dictionary)
    }
  }
}
