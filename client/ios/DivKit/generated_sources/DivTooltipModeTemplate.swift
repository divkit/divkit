// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTooltipModeTemplate: TemplateValue, Sendable {
  case divTooltipModeNonModalTemplate(DivTooltipModeNonModalTemplate)
  case divTooltipModeModalTemplate(DivTooltipModeModalTemplate)

  public var value: Any {
    switch self {
    case let .divTooltipModeNonModalTemplate(value):
      return value
    case let .divTooltipModeModalTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTooltipModeTemplate {
    switch self {
    case let .divTooltipModeNonModalTemplate(value):
      return .divTooltipModeNonModalTemplate(try value.resolveParent(templates: templates))
    case let .divTooltipModeModalTemplate(value):
      return .divTooltipModeModalTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTooltipModeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTooltipMode> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivTooltipMode>!
      result = result ?? {
        if case let .divTooltipModeNonModalTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divTooltipModeNonModal(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divTooltipModeNonModal(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divTooltipModeModalTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divTooltipModeModal(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divTooltipModeModal(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTooltipMode> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivTooltipMode>?
    result = result ?? { if type == DivTooltipModeNonModal.type {
      let result = { DivTooltipModeNonModalTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divTooltipModeNonModal(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTooltipModeNonModal(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivTooltipModeModal.type {
      let result = { DivTooltipModeModalTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divTooltipModeModal(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTooltipModeModal(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivTooltipModeTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivTooltipModeNonModalTemplate.type:
      self = .divTooltipModeNonModalTemplate(try DivTooltipModeNonModalTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivTooltipModeModalTemplate.type:
      self = .divTooltipModeModalTemplate(try DivTooltipModeModalTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-tooltip-mode_template", representation: dictionary)
    }
  }
}
