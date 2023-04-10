// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivRadialGradientRadiusTemplate: TemplateValue {
  case divFixedSizeTemplate(DivFixedSizeTemplate)
  case divRadialGradientRelativeRadiusTemplate(DivRadialGradientRelativeRadiusTemplate)

  public var value: Any {
    switch self {
    case let .divFixedSizeTemplate(value):
      return value
    case let .divRadialGradientRelativeRadiusTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivRadialGradientRadiusTemplate {
    switch self {
    case let .divFixedSizeTemplate(value):
      return .divFixedSizeTemplate(try value.resolveParent(templates: templates))
    case let .divRadialGradientRelativeRadiusTemplate(value):
      return .divRadialGradientRelativeRadiusTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRadialGradientRadiusTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradientRadius> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divFixedSizeTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divRadialGradientRelativeRadiusTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradientRelativeRadius(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradientRelativeRadius(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradientRadius> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivFixedSize.type:
      let result = DivFixedSizeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivRadialGradientRelativeRadius.type:
      let result = DivRadialGradientRelativeRadiusTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradientRelativeRadius(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradientRelativeRadius(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivRadialGradientRadiusTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivFixedSizeTemplate.type:
      self = .divFixedSizeTemplate(try DivFixedSizeTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivRadialGradientRelativeRadiusTemplate.type:
      self = .divRadialGradientRelativeRadiusTemplate(try DivRadialGradientRelativeRadiusTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-radial-gradient-radius_template", representation: dictionary)
    }
  }
}
