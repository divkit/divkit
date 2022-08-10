// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public enum DivShapeTemplate: TemplateValue {
  case divRoundedRectangleShapeTemplate(DivRoundedRectangleShapeTemplate)

  public var value: Any {
    switch self {
    case let .divRoundedRectangleShapeTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: Templates) throws -> DivShapeTemplate {
    switch self {
    case let .divRoundedRectangleShapeTemplate(value):
      return .divRoundedRectangleShapeTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: Context, parent: DivShapeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivShape> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divRoundedRectangleShapeTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRoundedRectangleShape(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRoundedRectangleShape(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: Context, useOnlyLinks: Bool) -> DeserializationResult<DivShape> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(FieldError(fieldName: "type", level: .error, error: .requiredFieldIsMissing)))
    }

    switch type {
    case DivRoundedRectangleShape.type:
      let result = DivRoundedRectangleShapeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRoundedRectangleShape(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRoundedRectangleShape(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(FieldError(fieldName: "type", level: .error, error: .requiredFieldIsMissing)))
    }
  }
}

extension DivShapeTemplate: TemplateDeserializable {
  public init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivRoundedRectangleShapeTemplate.type:
      self = .divRoundedRectangleShapeTemplate(try DivRoundedRectangleShapeTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-shape_template", representation: dictionary)
    }
  }
}
