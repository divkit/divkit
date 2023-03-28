// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

@frozen
public enum DivInputMethodTemplate: TemplateValue {
  case divKeyboardInputTemplate(DivKeyboardInputTemplate)
  case divSelectionInputTemplate(DivSelectionInputTemplate)

  public var value: Any {
    switch self {
    case let .divKeyboardInputTemplate(value):
      return value
    case let .divSelectionInputTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: Templates) throws -> DivInputMethodTemplate {
    switch self {
    case let .divKeyboardInputTemplate(value):
      return .divKeyboardInputTemplate(try value.resolveParent(templates: templates))
    case let .divSelectionInputTemplate(value):
      return .divSelectionInputTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: Context, parent: DivInputMethodTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputMethod> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divKeyboardInputTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divKeyboardInput(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divKeyboardInput(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divSelectionInputTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSelectionInput(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSelectionInput(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: Context, useOnlyLinks: Bool) -> DeserializationResult<DivInputMethod> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivKeyboardInput.type:
      let result = DivKeyboardInputTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divKeyboardInput(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divKeyboardInput(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivSelectionInput.type:
      let result = DivSelectionInputTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSelectionInput(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSelectionInput(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivInputMethodTemplate: TemplateDeserializable {
  public init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivKeyboardInputTemplate.type:
      self = .divKeyboardInputTemplate(try DivKeyboardInputTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivSelectionInputTemplate.type:
      self = .divSelectionInputTemplate(try DivSelectionInputTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-method_template", representation: dictionary)
    }
  }
}
