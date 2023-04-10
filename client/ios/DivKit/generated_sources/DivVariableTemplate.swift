// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivVariableTemplate: TemplateValue {
  case stringVariableTemplate(StringVariableTemplate)
  case numberVariableTemplate(NumberVariableTemplate)
  case integerVariableTemplate(IntegerVariableTemplate)
  case booleanVariableTemplate(BooleanVariableTemplate)
  case colorVariableTemplate(ColorVariableTemplate)
  case urlVariableTemplate(UrlVariableTemplate)

  public var value: Any {
    switch self {
    case let .stringVariableTemplate(value):
      return value
    case let .numberVariableTemplate(value):
      return value
    case let .integerVariableTemplate(value):
      return value
    case let .booleanVariableTemplate(value):
      return value
    case let .colorVariableTemplate(value):
      return value
    case let .urlVariableTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVariableTemplate {
    switch self {
    case let .stringVariableTemplate(value):
      return .stringVariableTemplate(try value.resolveParent(templates: templates))
    case let .numberVariableTemplate(value):
      return .numberVariableTemplate(try value.resolveParent(templates: templates))
    case let .integerVariableTemplate(value):
      return .integerVariableTemplate(try value.resolveParent(templates: templates))
    case let .booleanVariableTemplate(value):
      return .booleanVariableTemplate(try value.resolveParent(templates: templates))
    case let .colorVariableTemplate(value):
      return .colorVariableTemplate(try value.resolveParent(templates: templates))
    case let .urlVariableTemplate(value):
      return .urlVariableTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVariableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVariable> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .stringVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.stringVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.stringVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .numberVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.numberVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.numberVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .integerVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.integerVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.integerVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .booleanVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.booleanVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.booleanVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .colorVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.colorVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.colorVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .urlVariableTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.urlVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.urlVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivVariable> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case StringVariable.type:
      let result = StringVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.stringVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.stringVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case NumberVariable.type:
      let result = NumberVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.numberVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.numberVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case IntegerVariable.type:
      let result = IntegerVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.integerVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.integerVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case BooleanVariable.type:
      let result = BooleanVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.booleanVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.booleanVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case ColorVariable.type:
      let result = ColorVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.colorVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.colorVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case UrlVariable.type:
      let result = UrlVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.urlVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.urlVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivVariableTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case StringVariableTemplate.type:
      self = .stringVariableTemplate(try StringVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case NumberVariableTemplate.type:
      self = .numberVariableTemplate(try NumberVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case IntegerVariableTemplate.type:
      self = .integerVariableTemplate(try IntegerVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case BooleanVariableTemplate.type:
      self = .booleanVariableTemplate(try BooleanVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case ColorVariableTemplate.type:
      self = .colorVariableTemplate(try ColorVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case UrlVariableTemplate.type:
      self = .urlVariableTemplate(try UrlVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-variable_template", representation: dictionary)
    }
  }
}
