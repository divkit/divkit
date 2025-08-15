// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputFilterTemplate: TemplateValue, Sendable {
  case divInputFilterRegexTemplate(DivInputFilterRegexTemplate)
  case divInputFilterExpressionTemplate(DivInputFilterExpressionTemplate)

  public var value: Any {
    switch self {
    case let .divInputFilterRegexTemplate(value):
      return value
    case let .divInputFilterExpressionTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputFilterTemplate {
    switch self {
    case let .divInputFilterRegexTemplate(value):
      return .divInputFilterRegexTemplate(try value.resolveParent(templates: templates))
    case let .divInputFilterExpressionTemplate(value):
      return .divInputFilterExpressionTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputFilterTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputFilter> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivInputFilter>!
      result = result ?? {
        if case let .divInputFilterRegexTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divInputFilterRegex(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divInputFilterRegex(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divInputFilterExpressionTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divInputFilterExpression(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divInputFilterExpression(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivInputFilter> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivInputFilter>?
    result = result ?? { if type == DivInputFilterRegex.type {
      let result = { DivInputFilterRegexTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divInputFilterRegex(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInputFilterRegex(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivInputFilterExpression.type {
      let result = { DivInputFilterExpressionTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divInputFilterExpression(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInputFilterExpression(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivInputFilterTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivInputFilterRegexTemplate.type:
      self = .divInputFilterRegexTemplate(try DivInputFilterRegexTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivInputFilterExpressionTemplate.type:
      self = .divInputFilterExpressionTemplate(try DivInputFilterExpressionTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-filter_template", representation: dictionary)
    }
  }
}
