// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivChangeTransitionTemplate: TemplateValue {
  case divChangeSetTransitionTemplate(DivChangeSetTransitionTemplate)
  case divChangeBoundsTransitionTemplate(DivChangeBoundsTransitionTemplate)

  public var value: Any {
    switch self {
    case let .divChangeSetTransitionTemplate(value):
      return value
    case let .divChangeBoundsTransitionTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivChangeTransitionTemplate {
    switch self {
    case let .divChangeSetTransitionTemplate(value):
      return .divChangeSetTransitionTemplate(try value.resolveParent(templates: templates))
    case let .divChangeBoundsTransitionTemplate(value):
      return .divChangeBoundsTransitionTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivChangeTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivChangeTransition> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divChangeSetTransitionTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divChangeSetTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divChangeSetTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divChangeBoundsTransitionTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divChangeBoundsTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divChangeBoundsTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivChangeTransition> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivChangeSetTransition.type:
      let result = DivChangeSetTransitionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divChangeSetTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divChangeSetTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivChangeBoundsTransition.type:
      let result = DivChangeBoundsTransitionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divChangeBoundsTransition(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divChangeBoundsTransition(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivChangeTransitionTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivChangeSetTransitionTemplate.type:
      self = .divChangeSetTransitionTemplate(try DivChangeSetTransitionTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivChangeBoundsTransitionTemplate.type:
      self = .divChangeBoundsTransitionTemplate(try DivChangeBoundsTransitionTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-change-transition_template", representation: dictionary)
    }
  }
}
