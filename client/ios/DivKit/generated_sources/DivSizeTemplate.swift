// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivSizeTemplate: TemplateValue {
  case divFixedSizeTemplate(DivFixedSizeTemplate)
  case divMatchParentSizeTemplate(DivMatchParentSizeTemplate)
  case divWrapContentSizeTemplate(DivWrapContentSizeTemplate)

  public var value: Any {
    switch self {
    case let .divFixedSizeTemplate(value):
      return value
    case let .divMatchParentSizeTemplate(value):
      return value
    case let .divWrapContentSizeTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSizeTemplate {
    switch self {
    case let .divFixedSizeTemplate(value):
      return .divFixedSizeTemplate(try value.resolveParent(templates: templates))
    case let .divMatchParentSizeTemplate(value):
      return .divMatchParentSizeTemplate(try value.resolveParent(templates: templates))
    case let .divWrapContentSizeTemplate(value):
      return .divWrapContentSizeTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSize> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divFixedSizeTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divMatchParentSizeTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divMatchParentSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divMatchParentSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divWrapContentSizeTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divWrapContentSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divWrapContentSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivSize> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivFixedSize.type:
      let result = DivFixedSizeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divFixedSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divFixedSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivMatchParentSize.type:
      let result = DivMatchParentSizeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divMatchParentSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divMatchParentSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivWrapContentSize.type:
      let result = DivWrapContentSizeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divWrapContentSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divWrapContentSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivSizeTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivFixedSizeTemplate.type:
      self = .divFixedSizeTemplate(try DivFixedSizeTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivMatchParentSizeTemplate.type:
      self = .divMatchParentSizeTemplate(try DivMatchParentSizeTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivWrapContentSizeTemplate.type:
      self = .divWrapContentSizeTemplate(try DivWrapContentSizeTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-size_template", representation: dictionary)
    }
  }
}
