// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivShapeTemplate: TemplateValue, Sendable {
  case divRoundedRectangleShapeTemplate(DivRoundedRectangleShapeTemplate)
  case divCircleShapeTemplate(DivCircleShapeTemplate)

  public var value: Any {
    switch self {
    case let .divRoundedRectangleShapeTemplate(value):
      return value
    case let .divCircleShapeTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivShapeTemplate {
    switch self {
    case let .divRoundedRectangleShapeTemplate(value):
      return .divRoundedRectangleShapeTemplate(try value.resolveParent(templates: templates))
    case let .divCircleShapeTemplate(value):
      return .divCircleShapeTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivShapeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivShape> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivShape>!
      result = result ?? {
        if case let .divRoundedRectangleShapeTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divRoundedRectangleShape(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divRoundedRectangleShape(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divCircleShapeTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divCircleShape(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divCircleShape(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivShape> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivShape>?
    result = result ?? { if type == DivRoundedRectangleShape.type {
      let result = { DivRoundedRectangleShapeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divRoundedRectangleShape(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRoundedRectangleShape(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivCircleShape.type {
      let result = { DivCircleShapeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divCircleShape(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divCircleShape(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivShapeTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivRoundedRectangleShapeTemplate.type:
      self = .divRoundedRectangleShapeTemplate(try DivRoundedRectangleShapeTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivCircleShapeTemplate.type:
      self = .divCircleShapeTemplate(try DivCircleShapeTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-shape_template", representation: dictionary)
    }
  }
}
