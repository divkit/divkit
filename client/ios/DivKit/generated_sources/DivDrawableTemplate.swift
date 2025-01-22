// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivDrawableTemplate: TemplateValue, Sendable {
  case divShapeDrawableTemplate(DivShapeDrawableTemplate)

  public var value: Any {
    switch self {
    case let .divShapeDrawableTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivDrawableTemplate {
    switch self {
    case let .divShapeDrawableTemplate(value):
      return .divShapeDrawableTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDrawableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDrawable> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivDrawable>!
      result = result ?? {
        if case let .divShapeDrawableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divShapeDrawable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divShapeDrawable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivDrawable> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivDrawable>?
    result = result ?? { if type == DivShapeDrawable.type {
      let result = { DivShapeDrawableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divShapeDrawable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divShapeDrawable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivDrawableTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivShapeDrawableTemplate.type:
      self = .divShapeDrawableTemplate(try DivShapeDrawableTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-drawable_template", representation: dictionary)
    }
  }
}
