// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivCountTemplate: TemplateValue {
  case divInfinityCountTemplate(DivInfinityCountTemplate)
  case divFixedCountTemplate(DivFixedCountTemplate)

  public var value: Any {
    switch self {
    case let .divInfinityCountTemplate(value):
      return value
    case let .divFixedCountTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCountTemplate {
    switch self {
    case let .divInfinityCountTemplate(value):
      return .divInfinityCountTemplate(try value.resolveParent(templates: templates))
    case let .divFixedCountTemplate(value):
      return .divFixedCountTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCountTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCount> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divInfinityCountTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divInfinityCount(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInfinityCount(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divFixedCountTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedCount(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedCount(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivCount> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivInfinityCount.type:
      let result = DivInfinityCountTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divInfinityCount(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInfinityCount(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivFixedCount.type:
      let result = DivFixedCountTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedCount(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedCount(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivCountTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivInfinityCountTemplate.type:
      self = .divInfinityCountTemplate(try DivInfinityCountTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivFixedCountTemplate.type:
      self = .divFixedCountTemplate(try DivFixedCountTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-count_template", representation: dictionary)
    }
  }
}
