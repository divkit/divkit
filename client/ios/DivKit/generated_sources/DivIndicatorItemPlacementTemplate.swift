// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivIndicatorItemPlacementTemplate: TemplateValue {
  case divDefaultIndicatorItemPlacementTemplate(DivDefaultIndicatorItemPlacementTemplate)
  case divStretchIndicatorItemPlacementTemplate(DivStretchIndicatorItemPlacementTemplate)

  public var value: Any {
    switch self {
    case let .divDefaultIndicatorItemPlacementTemplate(value):
      return value
    case let .divStretchIndicatorItemPlacementTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivIndicatorItemPlacementTemplate {
    switch self {
    case let .divDefaultIndicatorItemPlacementTemplate(value):
      return .divDefaultIndicatorItemPlacementTemplate(try value.resolveParent(templates: templates))
    case let .divStretchIndicatorItemPlacementTemplate(value):
      return .divStretchIndicatorItemPlacementTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivIndicatorItemPlacementTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivIndicatorItemPlacement> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divDefaultIndicatorItemPlacementTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divDefaultIndicatorItemPlacement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divDefaultIndicatorItemPlacement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divStretchIndicatorItemPlacementTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divStretchIndicatorItemPlacement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divStretchIndicatorItemPlacement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivIndicatorItemPlacement> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivDefaultIndicatorItemPlacement.type:
      let result = DivDefaultIndicatorItemPlacementTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divDefaultIndicatorItemPlacement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divDefaultIndicatorItemPlacement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivStretchIndicatorItemPlacement.type:
      let result = DivStretchIndicatorItemPlacementTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divStretchIndicatorItemPlacement(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divStretchIndicatorItemPlacement(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivIndicatorItemPlacementTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivDefaultIndicatorItemPlacementTemplate.type:
      self = .divDefaultIndicatorItemPlacementTemplate(try DivDefaultIndicatorItemPlacementTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivStretchIndicatorItemPlacementTemplate.type:
      self = .divStretchIndicatorItemPlacementTemplate(try DivStretchIndicatorItemPlacementTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-indicator-item-placement_template", representation: dictionary)
    }
  }
}
