// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionScrollDestinationTemplate: TemplateValue, Sendable {
  case offsetDestinationTemplate(OffsetDestinationTemplate)
  case indexDestinationTemplate(IndexDestinationTemplate)
  case startDestinationTemplate(StartDestinationTemplate)
  case endDestinationTemplate(EndDestinationTemplate)

  public var value: Any {
    switch self {
    case let .offsetDestinationTemplate(value):
      return value
    case let .indexDestinationTemplate(value):
      return value
    case let .startDestinationTemplate(value):
      return value
    case let .endDestinationTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionScrollDestinationTemplate {
    switch self {
    case let .offsetDestinationTemplate(value):
      return .offsetDestinationTemplate(try value.resolveParent(templates: templates))
    case let .indexDestinationTemplate(value):
      return .indexDestinationTemplate(try value.resolveParent(templates: templates))
    case let .startDestinationTemplate(value):
      return .startDestinationTemplate(try value.resolveParent(templates: templates))
    case let .endDestinationTemplate(value):
      return .endDestinationTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionScrollDestinationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionScrollDestination> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivActionScrollDestination>!
      result = result ?? {
        if case let .offsetDestinationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.offsetDestination(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.offsetDestination(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .indexDestinationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.indexDestination(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.indexDestination(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .startDestinationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.startDestination(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.startDestination(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .endDestinationTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.endDestination(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.endDestination(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivActionScrollDestination> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivActionScrollDestination>?
    result = result ?? { if type == OffsetDestination.type {
      let result = { OffsetDestinationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.offsetDestination(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.offsetDestination(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == IndexDestination.type {
      let result = { IndexDestinationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.indexDestination(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.indexDestination(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == StartDestination.type {
      let result = { StartDestinationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.startDestination(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.startDestination(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EndDestination.type {
      let result = { EndDestinationTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.endDestination(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.endDestination(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivActionScrollDestinationTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case OffsetDestinationTemplate.type:
      self = .offsetDestinationTemplate(try OffsetDestinationTemplate(dictionary: dictionary, templateToType: templateToType))
    case IndexDestinationTemplate.type:
      self = .indexDestinationTemplate(try IndexDestinationTemplate(dictionary: dictionary, templateToType: templateToType))
    case StartDestinationTemplate.type:
      self = .startDestinationTemplate(try StartDestinationTemplate(dictionary: dictionary, templateToType: templateToType))
    case EndDestinationTemplate.type:
      self = .endDestinationTemplate(try EndDestinationTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-scroll-destination_template", representation: dictionary)
    }
  }
}
