// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTransformationTemplate: TemplateValue, Sendable {
  case divRotationTransformationTemplate(DivRotationTransformationTemplate)
  case divTranslationTransformationTemplate(DivTranslationTransformationTemplate)

  public var value: Any {
    switch self {
    case let .divRotationTransformationTemplate(value):
      return value
    case let .divTranslationTransformationTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTransformationTemplate {
    switch self {
    case let .divRotationTransformationTemplate(value):
      return .divRotationTransformationTemplate(try value.resolveParent(templates: templates))
    case let .divTranslationTransformationTemplate(value):
      return .divTranslationTransformationTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTransformationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTransformation> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivTransformation>!
      result = result ?? {
        if case let .divRotationTransformationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divRotationTransformation(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divRotationTransformation(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divTranslationTransformationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divTranslationTransformation(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divTranslationTransformation(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTransformation> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivTransformation>?
    result = result ?? { if type == DivRotationTransformation.type {
      let result = { DivRotationTransformationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divRotationTransformation(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRotationTransformation(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivTranslationTransformation.type {
      let result = { DivTranslationTransformationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divTranslationTransformation(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTranslationTransformation(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivTransformationTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivRotationTransformationTemplate.type:
      self = .divRotationTransformationTemplate(try DivRotationTransformationTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivTranslationTransformationTemplate.type:
      self = .divTranslationTransformationTemplate(try DivTranslationTransformationTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-transformation_template", representation: dictionary)
    }
  }
}
