// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTextGradientTemplate: TemplateValue, Sendable {
  case divLinearGradientTemplate(DivLinearGradientTemplate)
  case divRadialGradientTemplate(DivRadialGradientTemplate)

  public var value: Any {
    switch self {
    case let .divLinearGradientTemplate(value):
      return value
    case let .divRadialGradientTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextGradientTemplate {
    switch self {
    case let .divLinearGradientTemplate(value):
      return .divLinearGradientTemplate(try value.resolveParent(templates: templates))
    case let .divRadialGradientTemplate(value):
      return .divRadialGradientTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextGradientTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextGradient> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivTextGradient>!
      result = result ?? {
        if case let .divLinearGradientTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divLinearGradient(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divLinearGradient(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divRadialGradientTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divRadialGradient(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradient(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTextGradient> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivTextGradient>?
    result = result ?? { if type == DivLinearGradient.type {
      let result = { DivLinearGradientTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divLinearGradient(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divLinearGradient(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivRadialGradient.type {
      let result = { DivRadialGradientTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divRadialGradient(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradient(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivTextGradientTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivLinearGradientTemplate.type:
      self = .divLinearGradientTemplate(try DivLinearGradientTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivRadialGradientTemplate.type:
      self = .divRadialGradientTemplate(try DivRadialGradientTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-text-gradient_template", representation: dictionary)
    }
  }
}
