// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTextRangeMaskTemplate: TemplateValue, Sendable {
  case divTextRangeMaskParticlesTemplate(DivTextRangeMaskParticlesTemplate)
  case divTextRangeMaskSolidTemplate(DivTextRangeMaskSolidTemplate)

  public var value: Any {
    switch self {
    case let .divTextRangeMaskParticlesTemplate(value):
      return value
    case let .divTextRangeMaskSolidTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskTemplate {
    switch self {
    case let .divTextRangeMaskParticlesTemplate(value):
      return .divTextRangeMaskParticlesTemplate(try value.resolveParent(templates: templates))
    case let .divTextRangeMaskSolidTemplate(value):
      return .divTextRangeMaskSolidTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeMask> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivTextRangeMask>!
      result = result ?? {
        if case let .divTextRangeMaskParticlesTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divTextRangeMaskParticles(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divTextRangeMaskParticles(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divTextRangeMaskSolidTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divTextRangeMaskSolid(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divTextRangeMaskSolid(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeMask> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivTextRangeMask>?
    result = result ?? { if type == DivTextRangeMaskParticles.type {
      let result = { DivTextRangeMaskParticlesTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divTextRangeMaskParticles(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTextRangeMaskParticles(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivTextRangeMaskSolid.type {
      let result = { DivTextRangeMaskSolidTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divTextRangeMaskSolid(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTextRangeMaskSolid(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivTextRangeMaskTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivTextRangeMaskParticlesTemplate.type:
      self = .divTextRangeMaskParticlesTemplate(try DivTextRangeMaskParticlesTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivTextRangeMaskSolidTemplate.type:
      self = .divTextRangeMaskSolidTemplate(try DivTextRangeMaskSolidTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-text-range-mask_template", representation: dictionary)
    }
  }
}
