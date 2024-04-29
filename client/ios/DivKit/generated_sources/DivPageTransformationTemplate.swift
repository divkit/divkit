// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivPageTransformationTemplate: TemplateValue {
  case divPageTransformationSlideTemplate(DivPageTransformationSlideTemplate)
  case divPageTransformationOverlapTemplate(DivPageTransformationOverlapTemplate)

  public var value: Any {
    switch self {
    case let .divPageTransformationSlideTemplate(value):
      return value
    case let .divPageTransformationOverlapTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPageTransformationTemplate {
    switch self {
    case let .divPageTransformationSlideTemplate(value):
      return .divPageTransformationSlideTemplate(try value.resolveParent(templates: templates))
    case let .divPageTransformationOverlapTemplate(value):
      return .divPageTransformationOverlapTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPageTransformationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPageTransformation> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divPageTransformationSlideTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divPageTransformationSlide(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPageTransformationSlide(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divPageTransformationOverlapTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divPageTransformationOverlap(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPageTransformationOverlap(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivPageTransformation> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivPageTransformationSlide.type:
      let result = DivPageTransformationSlideTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divPageTransformationSlide(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPageTransformationSlide(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivPageTransformationOverlap.type:
      let result = DivPageTransformationOverlapTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divPageTransformationOverlap(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPageTransformationOverlap(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivPageTransformationTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivPageTransformationSlideTemplate.type:
      self = .divPageTransformationSlideTemplate(try DivPageTransformationSlideTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivPageTransformationOverlapTemplate.type:
      self = .divPageTransformationOverlapTemplate(try DivPageTransformationOverlapTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-page-transformation_template", representation: dictionary)
    }
  }
}
