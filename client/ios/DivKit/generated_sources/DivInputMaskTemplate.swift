// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivInputMaskTemplate: TemplateValue {
  case divFixedLengthInputMaskTemplate(DivFixedLengthInputMaskTemplate)
  case divCurrencyInputMaskTemplate(DivCurrencyInputMaskTemplate)

  public var value: Any {
    switch self {
    case let .divFixedLengthInputMaskTemplate(value):
      return value
    case let .divCurrencyInputMaskTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputMaskTemplate {
    switch self {
    case let .divFixedLengthInputMaskTemplate(value):
      return .divFixedLengthInputMaskTemplate(try value.resolveParent(templates: templates))
    case let .divCurrencyInputMaskTemplate(value):
      return .divCurrencyInputMaskTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputMask> {
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
    case let .divCurrencyInputMaskTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divCurrencyInputMask(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divCurrencyInputMask(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivInputMask> {
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
    case DivCurrencyInputMask.type:
      let result = DivCurrencyInputMaskTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divCurrencyInputMask(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divCurrencyInputMask(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivInputMaskTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivFixedLengthInputMaskTemplate.type:
      self = .divFixedLengthInputMaskTemplate(try DivFixedLengthInputMaskTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivCurrencyInputMaskTemplate.type:
      self = .divCurrencyInputMaskTemplate(try DivCurrencyInputMaskTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-mask_template", representation: dictionary)
    }
  }
}
