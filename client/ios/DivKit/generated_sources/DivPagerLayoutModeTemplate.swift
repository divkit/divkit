// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivPagerLayoutModeTemplate: TemplateValue {
  case divPageSizeTemplate(DivPageSizeTemplate)
  case divNeighbourPageSizeTemplate(DivNeighbourPageSizeTemplate)

  public var value: Any {
    switch self {
    case let .divPageSizeTemplate(value):
      return value
    case let .divNeighbourPageSizeTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPagerLayoutModeTemplate {
    switch self {
    case let .divPageSizeTemplate(value):
      return .divPageSizeTemplate(try value.resolveParent(templates: templates))
    case let .divNeighbourPageSizeTemplate(value):
      return .divNeighbourPageSizeTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPagerLayoutModeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPagerLayoutMode> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivPagerLayoutMode>!
      result = result ?? {
        if case let .divPageSizeTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divPageSize(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divPageSize(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .divNeighbourPageSizeTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.divNeighbourPageSize(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.divNeighbourPageSize(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivPagerLayoutMode> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivPagerLayoutMode>?
    result = result ?? { if type == DivPageSize.type {
      let result = { DivPageSizeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divPageSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPageSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DivNeighbourPageSize.type {
      let result = { DivNeighbourPageSizeTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.divNeighbourPageSize(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divNeighbourPageSize(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivPagerLayoutModeTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivPageSizeTemplate.type:
      self = .divPageSizeTemplate(try DivPageSizeTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivNeighbourPageSizeTemplate.type:
      self = .divNeighbourPageSizeTemplate(try DivNeighbourPageSizeTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-pager-layout-mode_template", representation: dictionary)
    }
  }
}
