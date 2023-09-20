// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivActionCopyToClipboardContentTemplate: TemplateValue {
  case contentTextTemplate(ContentTextTemplate)
  case contentUrlTemplate(ContentUrlTemplate)

  public var value: Any {
    switch self {
    case let .contentTextTemplate(value):
      return value
    case let .contentUrlTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionCopyToClipboardContentTemplate {
    switch self {
    case let .contentTextTemplate(value):
      return .contentTextTemplate(try value.resolveParent(templates: templates))
    case let .contentUrlTemplate(value):
      return .contentUrlTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionCopyToClipboardContentTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionCopyToClipboardContent> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .contentTextTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.contentText(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.contentText(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .contentUrlTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.contentUrl(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.contentUrl(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivActionCopyToClipboardContent> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case ContentText.type:
      let result = ContentTextTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.contentText(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.contentText(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case ContentUrl.type:
      let result = ContentUrlTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.contentUrl(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.contentUrl(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivActionCopyToClipboardContentTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case ContentTextTemplate.type:
      self = .contentTextTemplate(try ContentTextTemplate(dictionary: dictionary, templateToType: templateToType))
    case ContentUrlTemplate.type:
      self = .contentUrlTemplate(try ContentUrlTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-copy-to-clipboard-content_template", representation: dictionary)
    }
  }
}
