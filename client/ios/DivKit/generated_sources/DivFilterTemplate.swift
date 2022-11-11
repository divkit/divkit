// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

@frozen
public enum DivFilterTemplate: TemplateValue {
  case divBlurTemplate(DivBlurTemplate)

  public var value: Any {
    switch self {
    case let .divBlurTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: Templates) throws -> DivFilterTemplate {
    switch self {
    case let .divBlurTemplate(value):
      return .divBlurTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: Context, parent: DivFilterTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFilter> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divBlurTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divBlur(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divBlur(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: Context, useOnlyLinks: Bool) -> DeserializationResult<DivFilter> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(FieldError(fieldName: "type", level: .error, error: .requiredFieldIsMissing)))
    }

    switch type {
    case DivBlur.type:
      let result = DivBlurTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divBlur(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divBlur(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(FieldError(fieldName: "type", level: .error, error: .requiredFieldIsMissing)))
    }
  }
}

extension DivFilterTemplate: TemplateDeserializable {
  public init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivBlurTemplate.type:
      self = .divBlurTemplate(try DivBlurTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-filter_template", representation: dictionary)
    }
  }
}
