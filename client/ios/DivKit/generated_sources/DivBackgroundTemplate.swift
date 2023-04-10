// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivBackgroundTemplate: TemplateValue {
  case divLinearGradientTemplate(DivLinearGradientTemplate)
  case divRadialGradientTemplate(DivRadialGradientTemplate)
  case divImageBackgroundTemplate(DivImageBackgroundTemplate)
  case divSolidBackgroundTemplate(DivSolidBackgroundTemplate)
  case divNinePatchBackgroundTemplate(DivNinePatchBackgroundTemplate)

  public var value: Any {
    switch self {
    case let .divLinearGradientTemplate(value):
      return value
    case let .divRadialGradientTemplate(value):
      return value
    case let .divImageBackgroundTemplate(value):
      return value
    case let .divSolidBackgroundTemplate(value):
      return value
    case let .divNinePatchBackgroundTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivBackgroundTemplate {
    switch self {
    case let .divLinearGradientTemplate(value):
      return .divLinearGradientTemplate(try value.resolveParent(templates: templates))
    case let .divRadialGradientTemplate(value):
      return .divRadialGradientTemplate(try value.resolveParent(templates: templates))
    case let .divImageBackgroundTemplate(value):
      return .divImageBackgroundTemplate(try value.resolveParent(templates: templates))
    case let .divSolidBackgroundTemplate(value):
      return .divSolidBackgroundTemplate(try value.resolveParent(templates: templates))
    case let .divNinePatchBackgroundTemplate(value):
      return .divNinePatchBackgroundTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivBackground> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divLinearGradientTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divLinearGradient(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divLinearGradient(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divRadialGradientTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradient(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradient(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divImageBackgroundTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divImageBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divImageBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divSolidBackgroundTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSolidBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSolidBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divNinePatchBackgroundTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divNinePatchBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divNinePatchBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivBackground> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivLinearGradient.type:
      let result = DivLinearGradientTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divLinearGradient(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divLinearGradient(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivRadialGradient.type:
      let result = DivRadialGradientTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradient(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradient(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivImageBackground.type:
      let result = DivImageBackgroundTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divImageBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divImageBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivSolidBackground.type:
      let result = DivSolidBackgroundTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSolidBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSolidBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivNinePatchBackground.type:
      let result = DivNinePatchBackgroundTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divNinePatchBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divNinePatchBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivBackgroundTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivLinearGradientTemplate.type:
      self = .divLinearGradientTemplate(try DivLinearGradientTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivRadialGradientTemplate.type:
      self = .divRadialGradientTemplate(try DivRadialGradientTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivImageBackgroundTemplate.type:
      self = .divImageBackgroundTemplate(try DivImageBackgroundTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivSolidBackgroundTemplate.type:
      self = .divSolidBackgroundTemplate(try DivSolidBackgroundTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivNinePatchBackgroundTemplate.type:
      self = .divNinePatchBackgroundTemplate(try DivNinePatchBackgroundTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-background_template", representation: dictionary)
    }
  }
}
