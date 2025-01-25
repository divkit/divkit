// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputValidatorTemplate: TemplateValue {
  case divInputValidatorRegexTemplate(DivInputValidatorRegexTemplate)
  case divInputValidatorExpressionTemplate(DivInputValidatorExpressionTemplate)

  public var value: Any {
    switch self {
    case let .divInputValidatorRegexTemplate(value):
      return value
    case let .divInputValidatorExpressionTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputValidatorTemplate {
    switch self {
    case let .divInputValidatorRegexTemplate(value):
      return .divInputValidatorRegexTemplate(try value.resolveParent(templates: templates))
    case let .divInputValidatorExpressionTemplate(value):
      return .divInputValidatorExpressionTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputValidatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidator> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivInputValidator>!
      result = result ?? {
        if case let .divInputValidatorRegexTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divInputValidatorRegex(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divInputValidatorRegex(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divInputValidatorExpressionTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divInputValidatorExpression(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divInputValidatorExpression(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidator> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivInputValidator>?
    result = result ?? { if type == DivInputValidatorRegex.type {
      let result = { DivInputValidatorRegexTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divInputValidatorRegex(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInputValidatorRegex(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivInputValidatorExpression.type {
      let result = { DivInputValidatorExpressionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divInputValidatorExpression(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInputValidatorExpression(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivInputValidatorTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivInputValidatorRegexTemplate.type:
      self = .divInputValidatorRegexTemplate(try DivInputValidatorRegexTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivInputValidatorExpressionTemplate.type:
      self = .divInputValidatorExpressionTemplate(try DivInputValidatorExpressionTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-validator_template", representation: dictionary)
    }
  }
}
