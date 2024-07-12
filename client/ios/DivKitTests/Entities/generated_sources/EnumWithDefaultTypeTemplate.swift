// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

@frozen
public enum EnumWithDefaultTypeTemplate: TemplateValue {
  case withDefaultTemplate(WithDefaultTemplate)
  case withoutDefaultTemplate(WithoutDefaultTemplate)

  public var value: Any {
    switch self {
    case let .withDefaultTemplate(value):
      return value
    case let .withoutDefaultTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EnumWithDefaultTypeTemplate {
    switch self {
    case let .withDefaultTemplate(value):
      return .withDefaultTemplate(try value.resolveParent(templates: templates))
    case let .withoutDefaultTemplate(value):
      return .withoutDefaultTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: EnumWithDefaultTypeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EnumWithDefaultType> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .withDefaultTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.withDefault(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.withDefault(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .withoutDefaultTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.withoutDefault(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.withoutDefault(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<EnumWithDefaultType> {
    let type = (context.templateData["type"] as? String ?? WithDefault.type)
      .flatMap { context.templateToType[$0] ?? $0 } 

    switch type {
    case WithDefault.type:
      let result = WithDefaultTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.withDefault(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.withDefault(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case WithoutDefault.type:
      let result = WithoutDefaultTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.withoutDefault(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.withoutDefault(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension EnumWithDefaultTypeTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case WithDefaultTemplate.type:
      self = .withDefaultTemplate(try WithDefaultTemplate(dictionary: dictionary, templateToType: templateToType))
    case WithoutDefaultTemplate.type:
      self = .withoutDefaultTemplate(try WithoutDefaultTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "enum_with_default_type_template", representation: dictionary)
    }
  }
}
