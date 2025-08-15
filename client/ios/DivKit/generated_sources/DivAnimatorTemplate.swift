// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivAnimatorTemplate: TemplateValue, Sendable {
  case divColorAnimatorTemplate(DivColorAnimatorTemplate)
  case divNumberAnimatorTemplate(DivNumberAnimatorTemplate)

  public var value: Any {
    switch self {
    case let .divColorAnimatorTemplate(value):
      return value
    case let .divNumberAnimatorTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAnimatorTemplate {
    switch self {
    case let .divColorAnimatorTemplate(value):
      return .divColorAnimatorTemplate(try value.resolveParent(templates: templates))
    case let .divNumberAnimatorTemplate(value):
      return .divNumberAnimatorTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAnimatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAnimator> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivAnimator>!
      result = result ?? {
        if case let .divColorAnimatorTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divColorAnimator(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divColorAnimator(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divNumberAnimatorTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divNumberAnimator(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divNumberAnimator(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivAnimator> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivAnimator>?
    result = result ?? { if type == DivColorAnimator.type {
      let result = { DivColorAnimatorTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divColorAnimator(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divColorAnimator(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivNumberAnimator.type {
      let result = { DivNumberAnimatorTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divNumberAnimator(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divNumberAnimator(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivAnimatorTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivColorAnimatorTemplate.type:
      self = .divColorAnimatorTemplate(try DivColorAnimatorTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivNumberAnimatorTemplate.type:
      self = .divNumberAnimatorTemplate(try DivNumberAnimatorTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-animator_template", representation: dictionary)
    }
  }
}
