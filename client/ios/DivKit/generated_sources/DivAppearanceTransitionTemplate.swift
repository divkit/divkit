// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivAppearanceTransitionTemplate: TemplateValue {
  case divAppearanceSetTransitionTemplate(DivAppearanceSetTransitionTemplate)
  case divFadeTransitionTemplate(DivFadeTransitionTemplate)
  case divScaleTransitionTemplate(DivScaleTransitionTemplate)
  case divSlideTransitionTemplate(DivSlideTransitionTemplate)

  public var value: Any {
    switch self {
    case let .divAppearanceSetTransitionTemplate(value):
      return value
    case let .divFadeTransitionTemplate(value):
      return value
    case let .divScaleTransitionTemplate(value):
      return value
    case let .divSlideTransitionTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAppearanceTransitionTemplate {
    switch self {
    case let .divAppearanceSetTransitionTemplate(value):
      return .divAppearanceSetTransitionTemplate(try value.resolveParent(templates: templates))
    case let .divFadeTransitionTemplate(value):
      return .divFadeTransitionTemplate(try value.resolveParent(templates: templates))
    case let .divScaleTransitionTemplate(value):
      return .divScaleTransitionTemplate(try value.resolveParent(templates: templates))
    case let .divSlideTransitionTemplate(value):
      return .divSlideTransitionTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAppearanceTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAppearanceTransition> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divAppearanceSetTransitionTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divAppearanceSetTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divAppearanceSetTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divFadeTransitionTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFadeTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFadeTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divScaleTransitionTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divScaleTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divScaleTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divSlideTransitionTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSlideTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSlideTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivAppearanceTransition> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivAppearanceSetTransition.type:
      let result = DivAppearanceSetTransitionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divAppearanceSetTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divAppearanceSetTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivFadeTransition.type:
      let result = DivFadeTransitionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFadeTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFadeTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivScaleTransition.type:
      let result = DivScaleTransitionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divScaleTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divScaleTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivSlideTransition.type:
      let result = DivSlideTransitionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSlideTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSlideTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivAppearanceTransitionTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivAppearanceSetTransitionTemplate.type:
      self = .divAppearanceSetTransitionTemplate(try DivAppearanceSetTransitionTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivFadeTransitionTemplate.type:
      self = .divFadeTransitionTemplate(try DivFadeTransitionTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivScaleTransitionTemplate.type:
      self = .divScaleTransitionTemplate(try DivScaleTransitionTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivSlideTransitionTemplate.type:
      self = .divSlideTransitionTemplate(try DivSlideTransitionTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-appearance-transition_template", representation: dictionary)
    }
  }
}
