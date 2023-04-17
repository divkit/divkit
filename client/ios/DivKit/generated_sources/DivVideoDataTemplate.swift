// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivVideoDataTemplate: TemplateValue {
  case divVideoDataVideoTemplate(DivVideoDataVideoTemplate)
  case divVideoDataStreamTemplate(DivVideoDataStreamTemplate)

  public var value: Any {
    switch self {
    case let .divVideoDataVideoTemplate(value):
      return value
    case let .divVideoDataStreamTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVideoDataTemplate {
    switch self {
    case let .divVideoDataVideoTemplate(value):
      return .divVideoDataVideoTemplate(try value.resolveParent(templates: templates))
    case let .divVideoDataStreamTemplate(value):
      return .divVideoDataStreamTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVideoDataTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoData> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divVideoDataVideoTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divVideoDataVideo(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divVideoDataVideo(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divVideoDataStreamTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divVideoDataStream(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divVideoDataStream(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivVideoData> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivVideoDataVideo.type:
      let result = DivVideoDataVideoTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divVideoDataVideo(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divVideoDataVideo(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivVideoDataStream.type:
      let result = DivVideoDataStreamTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divVideoDataStream(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divVideoDataStream(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivVideoDataTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivVideoDataVideoTemplate.type:
      self = .divVideoDataVideoTemplate(try DivVideoDataVideoTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivVideoDataStreamTemplate.type:
      self = .divVideoDataStreamTemplate(try DivVideoDataStreamTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-video-data_template", representation: dictionary)
    }
  }
}
