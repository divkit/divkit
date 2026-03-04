// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivStrokeStyleTemplate: TemplateValue, Sendable {
  case divStrokeStyleSolidTemplate(DivStrokeStyleSolidTemplate)
  case divStrokeStyleDashedTemplate(DivStrokeStyleDashedTemplate)

  public var value: Any {
    switch self {
    case let .divStrokeStyleSolidTemplate(value):
      return value
    case let .divStrokeStyleDashedTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivStrokeStyleTemplate {
    switch self {
    case let .divStrokeStyleSolidTemplate(value):
      return .divStrokeStyleSolidTemplate(try value.resolveParent(templates: templates))
    case let .divStrokeStyleDashedTemplate(value):
      return .divStrokeStyleDashedTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivStrokeStyleTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivStrokeStyle> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divStrokeStyleSolidTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divStrokeStyleSolid(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divStrokeStyleSolid(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divStrokeStyleDashedTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divStrokeStyleDashed(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divStrokeStyleDashed(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivStrokeStyle> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivStrokeStyleSolid.type:
      let result = DivStrokeStyleSolidTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divStrokeStyleSolid(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divStrokeStyleSolid(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivStrokeStyleDashed.type:
      let result = DivStrokeStyleDashedTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divStrokeStyleDashed(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divStrokeStyleDashed(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivStrokeStyleTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivStrokeStyleSolidTemplate.type:
      self = .divStrokeStyleSolidTemplate(try DivStrokeStyleSolidTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivStrokeStyleDashedTemplate.type:
      self = .divStrokeStyleDashedTemplate(try DivStrokeStyleDashedTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}
