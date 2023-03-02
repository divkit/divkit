// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

@frozen
public enum DivInputMaskTemplate: TemplateValue {
  case divFixedLengthInputMaskTemplate(DivFixedLengthInputMaskTemplate)

  public var value: Any {
    switch self {
    case let .divFixedLengthInputMaskTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: Templates) throws -> DivInputMaskTemplate {
    switch self {
    case let .divFixedLengthInputMaskTemplate(value):
      return .divFixedLengthInputMaskTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: Context, parent: DivInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputMask> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divFixedLengthInputMaskTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedLengthInputMask(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedLengthInputMask(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: Context, useOnlyLinks: Bool) -> DeserializationResult<DivInputMask> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivFixedLengthInputMask.type:
      let result = DivFixedLengthInputMaskTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedLengthInputMask(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedLengthInputMask(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivInputMaskTemplate: TemplateDeserializable {
  public init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivFixedLengthInputMaskTemplate.type:
      self = .divFixedLengthInputMaskTemplate(try DivFixedLengthInputMaskTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-mask_template", representation: dictionary)
    }
  }
}
