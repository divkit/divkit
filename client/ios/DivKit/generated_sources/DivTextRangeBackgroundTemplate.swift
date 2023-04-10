// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivTextRangeBackgroundTemplate: TemplateValue {
  case divSolidBackgroundTemplate(DivSolidBackgroundTemplate)

  public var value: Any {
    switch self {
    case let .divSolidBackgroundTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextRangeBackgroundTemplate {
    switch self {
    case let .divSolidBackgroundTemplate(value):
      return .divSolidBackgroundTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeBackground> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divSolidBackgroundTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSolidBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSolidBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeBackground> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivSolidBackground.type:
      let result = DivSolidBackgroundTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSolidBackground(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSolidBackground(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivTextRangeBackgroundTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivSolidBackgroundTemplate.type:
      self = .divSolidBackgroundTemplate(try DivSolidBackgroundTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-text-range-background_template", representation: dictionary)
    }
  }
}
