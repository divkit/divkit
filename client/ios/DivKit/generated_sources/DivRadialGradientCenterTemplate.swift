// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivRadialGradientCenterTemplate: TemplateValue {
  case divRadialGradientFixedCenterTemplate(DivRadialGradientFixedCenterTemplate)
  case divRadialGradientRelativeCenterTemplate(DivRadialGradientRelativeCenterTemplate)

  public var value: Any {
    switch self {
    case let .divRadialGradientFixedCenterTemplate(value):
      return value
    case let .divRadialGradientRelativeCenterTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivRadialGradientCenterTemplate {
    switch self {
    case let .divRadialGradientFixedCenterTemplate(value):
      return .divRadialGradientFixedCenterTemplate(try value.resolveParent(templates: templates))
    case let .divRadialGradientRelativeCenterTemplate(value):
      return .divRadialGradientRelativeCenterTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRadialGradientCenterTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradientCenter> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divRadialGradientFixedCenterTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradientFixedCenter(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradientFixedCenter(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divRadialGradientRelativeCenterTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradientRelativeCenter(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradientRelativeCenter(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradientCenter> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivRadialGradientFixedCenter.type:
      let result = DivRadialGradientFixedCenterTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradientFixedCenter(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradientFixedCenter(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivRadialGradientRelativeCenter.type:
      let result = DivRadialGradientRelativeCenterTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divRadialGradientRelativeCenter(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divRadialGradientRelativeCenter(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivRadialGradientCenterTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivRadialGradientFixedCenterTemplate.type:
      self = .divRadialGradientFixedCenterTemplate(try DivRadialGradientFixedCenterTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivRadialGradientRelativeCenterTemplate.type:
      self = .divRadialGradientRelativeCenterTemplate(try DivRadialGradientRelativeCenterTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-radial-gradient-center_template", representation: dictionary)
    }
  }
}
