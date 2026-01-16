// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTranslationTemplate: TemplateValue, Sendable {
  case divFixedTranslationTemplate(DivFixedTranslationTemplate)
  case divPercentageTranslationTemplate(DivPercentageTranslationTemplate)

  public var value: Any {
    switch self {
    case let .divFixedTranslationTemplate(value):
      return value
    case let .divPercentageTranslationTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTranslationTemplate {
    switch self {
    case let .divFixedTranslationTemplate(value):
      return .divFixedTranslationTemplate(try value.resolveParent(templates: templates))
    case let .divPercentageTranslationTemplate(value):
      return .divPercentageTranslationTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTranslationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTranslation> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivTranslation>!
      result = result ?? {
        if case let .divFixedTranslationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divFixedTranslation(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedTranslation(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divPercentageTranslationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divPercentageTranslation(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divPercentageTranslation(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTranslation> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivTranslation>?
    result = result ?? { if type == DivFixedTranslation.type {
      let result = { DivFixedTranslationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divFixedTranslation(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedTranslation(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivPercentageTranslation.type {
      let result = { DivPercentageTranslationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divPercentageTranslation(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPercentageTranslation(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivTranslationTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivFixedTranslationTemplate.type:
      self = .divFixedTranslationTemplate(try DivFixedTranslationTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivPercentageTranslationTemplate.type:
      self = .divPercentageTranslationTemplate(try DivPercentageTranslationTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-translation_template", representation: dictionary)
    }
  }
}
